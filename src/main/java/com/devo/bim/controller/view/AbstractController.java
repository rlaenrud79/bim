package com.devo.bim.controller.view;

import com.devo.bim.component.Message;
import com.devo.bim.component.Proc;
import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.HolidayClassifiedWorkDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Config;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.enumulator.PageSizeUseType;
import com.devo.bim.service.ConfigService;
import com.devo.bim.service.LastSessionService;
import com.devo.bim.service.ProcessService;
import com.devo.bim.service.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class AbstractController {

    @Value("${system.path.win_upload}")
    protected String winPathUpload;

    @Value("${system.path.linux_upload}")
    protected String linuxPathUpload;

    @Value("${system.path.mac_upload}")
    protected String macPathUpload;

    @Autowired protected Proc proc;
    @Autowired protected Message message;
    @Autowired protected LastSessionService lastSessionService;
    @Autowired protected ConfigService configService;
    @Resource protected UserInfo userInfo;

    protected final int defaultPageSize_jobSheetList = 20;
    protected final int defaultPageSize_bulletinList = 20;
    protected final int defaultPageSize_coWorkList = 20;
    protected final int defaultPageSize_issueList = 20;
    protected final int defaultPageSize_notificationList = 20;
    protected final int defaultPageSize_documentList = 20;
    protected final int defaultPageSize_workAmountList = 20;
    protected final int defaultPageSize_workPlanList = 20;

    protected final int defaultPageSize_gisungReportList = 20;
    protected final int defaultPageSize_gisungPaymentList = 20;

    protected final int defaultPageSize_gisungList = 20;
    protected final int defaultPageSize_searchUserList = 20;
    protected final int defaultPageSize_codeValidationResultList = 50;
    protected final int defaultPageSize_companyList = 20;
    protected final int defaultPageSize_statisticsUserList = 20;
    protected final int defaultPageSize_adminUserList = 20;

    protected final String[] hourList = {"00", "01", "02", "03", "04", "05"
            , "06", "07", "08", "09", "10", "11"
            , "12", "13", "14", "15", "16", "17"
            , "18", "19", "20", "21", "22", "23"};

    protected final String[] minList = {"00", "10", "20", "30", "40", "50"};

    protected <T> void setPagingConfig(Model model, PageDTO<T> pageDTO) {

        int existCheckNum = 4;

        int totalPages = pageDTO.getPageList().getTotalPages();
        int totalCount = pageDTO.getPageList().getContent().size();
        int nowPage = pageDTO.getPageList().getPageable().getPageNumber() + 1;

        boolean[] existPrev = {false, false, false, false};
        boolean[] existNext = {false, false, false, false};

        // existPrev & existNext 초기화
        initExistPrevNextArray(existCheckNum, totalPages, nowPage, existPrev, existNext);

        // start & end page 초기화
        PageDisplayVO pageDisplayVO = new PageDisplayVO(nowPage, totalPages, totalCount);

        // 앞쪽 번호 존재 여부로 번호 생성
        checkPrevPageNoAndMake(nowPage, pageDisplayVO, existPrev, existNext);
        // 뒤쪽 번호 존재 여부로 번호 생성
        checkNextPageNoAndMake(nowPage, pageDisplayVO, existPrev, existNext);

        model.addAttribute("pagingSizes", configService.findPagingSizeConfigs());
        model.addAttribute("totalCount", pageDTO.getTotalCount());
        model.addAttribute("list", pageDTO.getPageList());
        model.addAttribute("pageSize", pageDTO.getPageList().getPageable().getPageSize());
        model.addAttribute("displayStartPage", pageDisplayVO.getDisplayStartPage());
        model.addAttribute("displayEndPage", pageDisplayVO.getDisplayEndPage());
    }

    private void checkPrevPageNoAndMake(int nowPage, PageDisplayVO pageDisplayVO, boolean[] existPrev, boolean[] existNext) {

        int loopCnt = 0;
        int startPageNo = pageDisplayVO.getDisplayStartPage();

        if(!existPrev[1] && !existPrev[0]){
            startPageNo = nowPage;
            loopCnt = existNext.length;
        }

        if(!existPrev[1] && existPrev[0]){
            startPageNo = nowPage - 1;
            loopCnt = existNext.length - 1;
        }

        if(existPrev[1] && existPrev[0]){
            loopCnt = existNext.length - 2;
        }

        pageDisplayVO.setDisplayStartPage(startPageNo);

        for(int idx = 0; idx < loopCnt; idx++){
            if(existNext[idx]) pageDisplayVO.setDisplayEndPage(nowPage + (idx + 1));
        }

    }

    private void checkNextPageNoAndMake(int nowPage, PageDisplayVO pageDisplayVO, boolean[] existPrev, boolean[] existNext) {

        int loopCnt = 0;
        int endPageNo = pageDisplayVO.getDisplayEndPage();

        if(existNext[0] && existNext[1]){
            loopCnt = existPrev.length - 2;
        }

        if(existNext[0] && !existNext[1]){
            loopCnt = existPrev.length - 1;
            endPageNo = nowPage + 1;
        }

        if(!existNext[0] && !existNext[1]){
            loopCnt = existPrev.length;
            endPageNo = nowPage;
        }

        for(int idx = 0; idx < loopCnt ; idx++){
            if(existPrev[idx]) pageDisplayVO.setDisplayStartPage(nowPage - (idx + 1));
        }
        pageDisplayVO.setDisplayEndPage(endPageNo);
    }

    private void initExistPrevNextArray(int existCheckNum, int totalPages, int nowPage, boolean[] existPrv, boolean[] existNext) {
        for(int idx = 0; idx < existCheckNum; idx ++) {
            if(nowPage - (idx + 1) > 0) existPrv[idx] = true;
            if(nowPage + (idx + 1) <= totalPages) existNext[idx] = true;
        }
    }

    public boolean isSameSession(){
        return lastSessionService.isSameSession(userInfo.getId(), userInfo.getProjectId(), userInfo.getSessionId());
    }

    protected String getCookieValue(HttpServletRequest request, String cookieName) {
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    protected <T> void getSearchVOByCookie(T searchVO, String searchVOCookie) {
        String decodedSearchVOCookie = URLDecoder.decode(searchVOCookie, StandardCharsets.UTF_8);
        String[] searchVOCookieArray = decodedSearchVOCookie.split("&");
        HashMap<String,Object> map = new HashMap<String,Object>();

        for (String str : searchVOCookieArray) {
            if (str.split("=").length > 1) {
                map.put(str.split("=")[0], str.split("=")[1]);
            }
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Field field = searchVO.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (field.getType() == long.class || field.getType() == Long.class) {
                    field.set(searchVO, Long.parseLong((String) entry.getValue()));
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    field.set(searchVO, Integer.parseInt((String) entry.getValue()));
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    field.set(searchVO, Boolean.parseBoolean((String) entry.getValue()));
                } else {
                    field.set(searchVO, entry.getValue());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected Pageable getPageableByCookie(String searchPageCookie) {
        String[] searchPageCookieArray = searchPageCookie.split("&");
        int page = Integer.parseInt(searchPageCookieArray[0].split("=")[1]);
        int size = Integer.parseInt(searchPageCookieArray[1].split("=")[1]);
        return PageRequest.of(page, size);
    }

    protected <T> String getSearchVOToString(T searchVO) {
        String encodedSearchVO = "";
        try {
            encodedSearchVO = URLEncoder.encode(searchVO.toString(), StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }
        return encodedSearchVO;
    }

    protected String getPageableToString(Pageable pageable) {
        return "page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
    }

    private void setCookieOptions(Cookie cookie, String path) {
        cookie.setMaxAge(-1);
        cookie.setPath(path);
    }

    protected <T> void setVOCookie(T searchVO, String cookieName, String path, HttpServletResponse response) {
        Cookie searchVOCookie = new Cookie(cookieName, getSearchVOToString(searchVO));
        setCookieOptions(searchVOCookie, path);
        response.addCookie(searchVOCookie);
    }

    protected void setPageableCookie(Pageable pageable, String cookieName, String path, HttpServletResponse response) {
        Cookie searchPageCookie = new Cookie(cookieName, getPageableToString(pageable));
        setCookieOptions(searchPageCookie, path);
        response.addCookie(searchPageCookie);
    }

    protected void setAttributePhoneNoPattern(Model model) {
        model.addAttribute("telNoPattern", configService.findConfigForCache("LOCALE", "FORMAT_PHONE", userInfo.getProjectId()));
        model.addAttribute("telNoPlaceholder", configService.findConfigForCache("LOCALE", "PLACEHOLDER_PHONE", userInfo.getProjectId()));
        model.addAttribute("mobileNoPattern", configService.findConfigForCache("LOCALE", "FORMAT_MOBILE", userInfo.getProjectId()));
        model.addAttribute("mobileNoPlaceholder", configService.findConfigForCache("LOCALE", "PLACEHOLDER_MOBILE", userInfo.getProjectId()));
    }
}

class PageDisplayVO {
    private int displayStartPage;
    private int displayEndPage;

    public PageDisplayVO(int nowPageNo, int totalPages, int totalCount){
        this.displayStartPage = Math.max(1, nowPageNo / 5 * 5 + 1);
        this.displayEndPage = totalCount == 0 ? 1 : Math.min(totalPages, nowPageNo / 5 * 5 + 5);
    }

    public int getDisplayStartPage(){
        return this.displayStartPage;
    }

    public int getDisplayEndPage(){
        return this.displayEndPage;
    }

    public void setDisplayStartPage(int startPage){
        this.displayStartPage = startPage;
    }

    public void setDisplayEndPage(int endPage){
        this.displayEndPage = endPage;
    }
}
