package com.devo.bim.controller.view;

import com.devo.bim.component.Utils;
import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.exception.InternalServerErrorException;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.vo.*;
import com.devo.bim.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/coWork")
@RequiredArgsConstructor
public class CoWorkController extends AbstractController {

    private final AccountService accountService;
    private final SearchUserService searchUserService;
    private final CoWorkService coWorkService;
    private final IssueService issueService;
    private final NoticeService noticeService;
    private final WorkService workService;
    private final AlertService alertService;
    private final BulletinService bulletinService;
    private final BulletinReplyService bulletinReplyService;
    private final BulletinLikesService bulletinLikesService;
    private final ConfigService configService;
    private final ChatAPIService chatAPIService;

    // region 게시판

    @GetMapping("/bulletinUpdate")
    public String bulletinUpdate(long id, Model model) {
        Bulletin bulletin = bulletinService.findByIdAndProjectId(id, userInfo.getProjectId());

        if (bulletin.getId() == 0 ||
                ((bulletin.getWriteEmbedded().getWriter().getId() != userInfo.getId())
                && !userInfo.isRoleAdminProject())) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }

        model.addAttribute("bulletin", bulletin);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("userId", userInfo.getId());
        model.addAttribute("bulletinFileExtension", String.join("||", configService.findFileExtension("BULLETIN_FILE_EXT", true, userInfo.getProjectId())));
        model.addAttribute("previewFileExtension", String.join("||", configService.findFileExtension("PREVIEW_FILE_EXT", true, userInfo.getProjectId())));
        model.addAttribute("ptype", "edit");

        return "coWork/bulletinAdd/page";
    }

    @GetMapping("/bulletinAdd")
    public String bulletinAdd(Model model) {
        model.addAttribute("bulletin", new Bulletin());
        model.addAttribute("bulletinFileExtension", String.join("||", configService.findFileExtension("BULLETIN_FILE_EXT", true, userInfo.getProjectId())));
        model.addAttribute("ptype", "write");

        return "coWork/bulletinAdd/page";
    }

    @GetMapping("/bulletinList")
    public String bulletinList(@ModelAttribute SearchBulletinVO searchBulletinVO
            , @PageableDefault(size = defaultPageSize_bulletinList) Pageable pageable
            , Model model, HttpServletRequest request, HttpServletResponse response) {

        String searchVOCookie = getCookieValue(request, "searchBulletinVO");
        String searchPageCookie = getCookieValue(request, "searchBulletinPage");

        if (!searchBulletinVO.getKeepCondition()) {
            searchVOCookie = null;
            searchPageCookie = null;
        }

        if (searchVOCookie != null) {
            getSearchVOByCookie(searchBulletinVO, searchVOCookie);
        }

        if (searchPageCookie != null) {
            pageable = getPageableByCookie(searchPageCookie);
        }

        getBulletinList(searchBulletinVO, pageable, model, response);
        return "coWork/bulletinList/page";
    }

    @GetMapping("/bulletinListCardBody")
    public String bulletinListCardBody(@ModelAttribute SearchBulletinVO searchBulletinVO
            , @PageableDefault(size = defaultPageSize_bulletinList) Pageable pageable
            , Model model, HttpServletResponse response) {

        getBulletinList(searchBulletinVO, pageable, model, response);

        return "coWork/bulletinList/cardBody";
    }

    private void getBulletinList(SearchBulletinVO searchBulletinVO, Pageable pageable, Model model, HttpServletResponse response) {
        searchBulletinVO.setProjectId(userInfo.getProjectId());

        PageDTO<BulletinDTO> pageBulletinDTOs = bulletinService.findBulletinDTOs(searchBulletinVO, pageable);

        setPagingConfig(model, pageBulletinDTOs);

        model.addAttribute("searchBulletinVO", searchBulletinVO);

        setVOCookie(searchBulletinVO, "searchBulletinVO", "/coWork", response);
        setPageableCookie(pageable, "searchBulletinPage", "/coWork", response);

        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
    }

    @GetMapping("/bulletinView")
    public String bulletinView(long id, Model model) {
        getBulletinAndAddViewCount(id, model);

        return "coWork/bulletinView/page";
    }

    private void getBulletinAndAddViewCount(long id, Model model) {
        Bulletin bulletin = bulletinService.findByIdAndProjectId(id, userInfo.getProjectId());

        if (bulletin.getId() == 0 || bulletin.getProjectId() != userInfo.getProjectId()) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }

        bulletinService.addViewCount(bulletin);

        model.addAttribute("bulletin", bulletin);
        model.addAttribute("bulletinFileNotImg", getBulletinFile(bulletin, false));
        model.addAttribute("bulletinFileImg", getBulletinFile(bulletin, true));
        model.addAttribute("isLiked", getUserLikedBulletin(bulletin));
        model.addAttribute("isWriter", bulletin.getWriteEmbedded().getWriter().getId() == userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("userId", userInfo.getId());
    }

    private Boolean getUserLikedBulletin(Bulletin bulletin) {
        return bulletin.getBulletinLikesList().stream()
                .filter(t -> t.getWriteEmbedded().getWriter().getId() == userInfo.getId())
                .findFirst()
                .orElseGet(BulletinLikes::new)
                .isEnabled();
    }

    private List<BulletinFile> getBulletinFile(Bulletin bulletin, boolean isImage) {
        return bulletin.getBulletinFiles()
                .stream()
                .filter(t -> t.isImage() == isImage)
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(BulletinFile::getSortNo))
                .collect(Collectors.toList());
    }

    @GetMapping("/bulletinViewReply")
    public String bulletinViewReply(long bulletinId, Model model) {
        List<BulletinReply> bulletinReplies = bulletinReplyService.findByBulletinId(bulletinId);
        model.addAttribute("replies", bulletinReplies);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("userId", userInfo.getId());

        return "coWork/bulletinView/reply";
    }

    @GetMapping("/bulletinViewLikesButton")
    public String bulletinViewLikesButton(long bulletinId, Model model) {
        BulletinLikes bulletinLikes = bulletinLikesService.findByBulletinIdAndWriterId(bulletinId, userInfo.getId());
        model.addAttribute("isLiked", bulletinLikes.isEnabled());

        return "coWork/bulletinView/likesButton";
    }

    @GetMapping("/bulletinViewLikesUserList")
    public String bulletinViewLikesUserList(long bulletinId, Model model) {
        Bulletin bulletin = bulletinService.findByIdAndProjectId(bulletinId, userInfo.getProjectId());
        model.addAttribute("list", bulletin.getBulletinLikesList().stream().filter(BulletinLikes::isEnabled).collect(Collectors.toList()));

        return "coWork/bulletinView/likesUserList";
    }

    // endregion


    @GetMapping("/coWorkIssueView")
    public String coWorkIssueView(Model model) {

        return "coWork/coWorkIssueView/page";
    }

    @GetMapping({"/coWorkList", "/coWorkListCardBody"})
    public String coWorkList(@ModelAttribute SearchCoWorkVO searchCoWorkVO
            , @PageableDefault(size = defaultPageSize_coWorkList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        PageDTO<CoWorkDTO> coWorkDTOs = coWorkService.findCoWorkBySearchCondition(searchCoWorkVO, pageable);

        setPagingConfig(model, coWorkDTOs);

        model.addAttribute("searchCoWorkVO", searchCoWorkVO);

        if ("coWorkList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "coWork/coWorkList/page";
        return "coWork/coWorkList/cardBody";
    }

    // region 일반 이슈

    @GetMapping({"/issueList", "/issueListCardBody"})
    public String issueList(@ModelAttribute SearchIssueVO searchIssueVO
            , @PageableDefault(size = defaultPageSize_issueList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        searchIssueVO.setProjectId(userInfo.getProjectId());
        PageDTO<IssueDTO> pageIssueDTOs = issueService.findIssueDTOsBySearchCondition(searchIssueVO, pageable);

        setPagingConfig(model, pageIssueDTOs);

        model.addAttribute("searchIssueVO", searchIssueVO);
        model.addAttribute("userInfoId", userInfo.getId());

        if ("issueList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "coWork/issueList/page";
        return "coWork/issueList/cardBody";
    }

    @GetMapping("/issueAdd")
    public String issueAdd(Model model) {

        List<SearchUserDTO> searchUserDTOs = setSearchUserDTOsIsSelected();

        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("issue", new Issue());
        model.addAttribute("modalScopeId", "divMultiUserSelect");
        model.addAttribute("formElementIdForUserIds", "issueManagersArray");
        model.addAttribute("formElementIdForModal", "");

        return "coWork/issueAdd/page";
    }

    @GetMapping({"/issueView", "/issueViewCardReportBody"})
    public String issueView(@RequestParam long issueId
            , @ModelAttribute SearchIssueVO searchIssueVO
            , @PageableDefault(size = defaultPageSize_issueList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        Issue issueItem = issueService.findById(issueId);

        model.addAttribute("issueItem", issueItem);
        model.addAttribute("pageable", pageable);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isIssueManagers", isIssueManager(issueItem));


        if ("issueView".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) {
            alertService.setIsReadTrue(issueItem.getProjectId(), userInfo.getId(), issueItem.getId(), AlertType.ISSUE);
            return "coWork/issueView/page";
        }
        return "coWork/issueView/cardReportBody";
    }

    @GetMapping("/issueUpdate")
    public String issueUpdate(@RequestParam long issueId
            , @ModelAttribute SearchIssueVO searchIssueVO
            , @PageableDefault(size = defaultPageSize_issueList) Pageable pageable
            , Model model) {

        Issue issueItem = issueService.findById(issueId);
        List<SearchUserDTO> searchUserDTOs = setSearchUserDTOsIsSelected(issueItem);

        model.addAttribute("issueItem", issueItem);
        model.addAttribute("pageable", pageable);
        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isIssueManagers", isIssueManager(issueItem));
        model.addAttribute("modalScopeId", "divMultiUserSelect");
        model.addAttribute("formElementIdForUserIds", "issueManagersArray");
        model.addAttribute("formElementIdForModal", "");

        return "coWork/issueUpdate/page";
    }

    @NotNull
    private List<SearchUserDTO> setSearchUserDTOsIsSelected() {
        List<Long> selectedIds = new ArrayList<>();
        selectedIds.add(userInfo.getId());
        return setSearchUserDTOS(selectedIds);
    }

    @NotNull
    private List<SearchUserDTO> setSearchUserDTOsIsSelected(Issue issueItem) {
        List<Long> selectedIds = issueItem.getIssueManagers().stream().map(o -> o.getManager().getId()).collect(Collectors.toList());
        return setSearchUserDTOS(selectedIds);
    }

    @NotNull
    private List<SearchUserDTO> setSearchUserDTOS(List<Long> selectedIds) {
        List<Long> fixedIds = new ArrayList<>();
        fixedIds.add(userInfo.getId());

        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserDTOs();

        searchUserDTOs.forEach(t -> {
            t.setSelected(selectedIds);
            t.setFixed(fixedIds);
        });

        return searchUserDTOs;
    }

    private boolean isIssueManager(Issue issueItem) {
        return issueItem.getIssueManagers().stream().filter(s -> s.getManager().getId() == userInfo.getId()).count() > 0;
    }

    // endregion 일반 이슈

    // region 공지사항

    @GetMapping({"/notificationList", "/notificationListCardBody"})
    public String notificationList(@ModelAttribute SearchNoticeVO searchNoticeVO
            , @PageableDefault(size = defaultPageSize_notificationList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        searchNoticeVO.setProjectId(userInfo.getProjectId());
        PageDTO<NoticeDTO> pageNoticeDTOs = noticeService.findNoticeDTOsBySearchCondition(searchNoticeVO, pageable);

        setPagingConfig(model, pageNoticeDTOs);

        model.addAttribute("searchNoticeVO", searchNoticeVO);
        model.addAttribute("userInfoId", userInfo.getId());

        if ("notificationList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "coWork/notificationList/page";
        return "coWork/notificationList/cardBody";
    }

    @GetMapping("/notificationView")
    public String notificationView(@RequestParam long noticeId
            , @ModelAttribute SearchNoticeVO searchNoticeVO
            , @PageableDefault(size = defaultPageSize_notificationList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        Notice noticeItem = noticeService.findById(noticeId);

        if (!isSameSession()) {
            lastSessionService.setNowSessionId(request.getSession().getId());
            noticeService.setViewCountPlusOne(noticeItem);
        }

        List<AccountDTO> alertConfirmUserDTOs = alertService.findConfirmUserByRefIdAndType(noticeItem.getProjectId(), noticeId, AlertType.NOTICE);

        model.addAttribute("noticeItem", noticeItem);
        model.addAttribute("noticeFileNotImg", getNoticeFile(noticeItem, false));
        model.addAttribute("noticeFileImg", getNoticeFile(noticeItem, true));
        model.addAttribute("alertConfirmUserDTOs", alertConfirmUserDTOs);
        model.addAttribute("pageable", pageable);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());

        alertService.setIsReadTrue(noticeItem.getProjectId(), userInfo.getId(), noticeItem.getId(), AlertType.NOTICE);
        return "coWork/notificationView/page";
    }

    @NotNull
    private List<NoticeFile> getNoticeFile(Notice noticeItem, boolean isImage) {
        return noticeItem.getNoticeFiles()
                .stream()
                .filter(t -> t.isImage() == isImage)
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(NoticeFile::getSortNo))
                .collect(Collectors.toList());
    }

    @GetMapping("/notificationAdd")
    @Secured({"ROLE_ADMIN_PROJECT", "ROLE_ADMIN_WORK"})
    public String notificationAdd(Model model) {

        model.addAttribute("notice", new Notice());
        model.addAttribute("works", workService.findUseWorkAll());
        model.addAttribute("hourList", new ArrayList(Arrays.asList(hourList)));
        model.addAttribute("minList", new ArrayList(Arrays.asList(minList)));
        model.addAttribute("notificationFileExtension", String.join("||", configService.findFileExtension("NOTIFICATION_FILE_EXT", true, userInfo.getProjectId())));

        return "coWork/notificationAdd/page";
    }

    @GetMapping("/notificationUpdate")
    @Secured({"ROLE_ADMIN_PROJECT", "ROLE_ADMIN_WORK"})
    public String notificationUpdate(@RequestParam(required = true, defaultValue = "0") long noticeId
            , @ModelAttribute SearchNoticeVO searchNoticeVO
            , @PageableDefault(size = defaultPageSize_notificationList) Pageable pageable
            , Model model) {

        Notice noticeItem = noticeService.findById(noticeId);

        if ((noticeItem.getWriteEmbedded().getWriter().getId() != userInfo.getId())
                && !userInfo.isRoleAdminProject()) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }

        model.addAttribute("noticeItem", noticeItem);
        model.addAttribute("pageable", pageable);
        model.addAttribute("works", workService.findUseWorkAll());
        model.addAttribute("hourList", new ArrayList(Arrays.asList(hourList)));
        model.addAttribute("minList", new ArrayList(Arrays.asList(minList)));
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("startDateDay", getNotificationPopUpStarDateDay(noticeItem));
        model.addAttribute("startDateHour", getNotificationPopUpStartDateHour(noticeItem));
        model.addAttribute("startDateMin", getNotificationPopUpStartDateMin(noticeItem));
        model.addAttribute("endDateDay", getNotificationPopUpEndDateDay(noticeItem));
        model.addAttribute("endDateHour", getNotificationPopUpEndDateHour(noticeItem));
        model.addAttribute("endDateMin", getNotificationPopUpEndDateMin(noticeItem));
        model.addAttribute("isTotalWorksSelected", Utils.isTotalWorksSelected(workService.findUseWorkAll(), noticeItem.getWorks().size()));
        model.addAttribute("notificationFileExtension", String.join("||", configService.findFileExtension("NOTIFICATION_FILE_EXT", true, userInfo.getProjectId())));

        return "coWork/notificationUpdate/page";
    }

    private String getNotificationPopUpEndDateMin(Notice noticeItem) {
        if (noticeItem.getEndDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getEndDate(), DateFormatEnum.MIN);
    }

    private String getNotificationPopUpEndDateHour(Notice noticeItem) {
        if (noticeItem.getEndDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getEndDate(), DateFormatEnum.HOUR);
    }

    private String getNotificationPopUpEndDateDay(Notice noticeItem) {
        if (noticeItem.getEndDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getEndDate(), DateFormatEnum.YEAR)
                + "-" + Utils.getDateTimeByNationAndFormatType(noticeItem.getEndDate(), DateFormatEnum.MONTH)
                + "-" + Utils.getDateTimeByNationAndFormatType(noticeItem.getEndDate(), DateFormatEnum.DAY);
    }

    private String getNotificationPopUpStartDateMin(Notice noticeItem) {
        if (noticeItem.getStartDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getStartDate(), DateFormatEnum.MIN);
    }

    private String getNotificationPopUpStartDateHour(Notice noticeItem) {
        if (noticeItem.getStartDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getStartDate(), DateFormatEnum.HOUR);
    }

    private String getNotificationPopUpStarDateDay(Notice noticeItem) {
        if (noticeItem.getStartDate() == null) return "";
        return Utils.getDateTimeByNationAndFormatType(noticeItem.getStartDate(), DateFormatEnum.YEAR)
                + "-" + Utils.getDateTimeByNationAndFormatType(noticeItem.getStartDate(), DateFormatEnum.MONTH)
                + "-" + Utils.getDateTimeByNationAndFormatType(noticeItem.getStartDate(), DateFormatEnum.DAY);
    }

    // endregion 공지사항

    // region 협업 뷰어  
    @GetMapping("/modelingView")
    public String modelingView(@RequestParam(required = true, defaultValue = "") String modelIds
    		, @RequestParam(required = false, defaultValue = "0") long mySnapShotId) {
   	
    	CoWork newCoWork;
    	
    	if(mySnapShotId > 0) {
    		newCoWork = coWorkService.postNewCoWork(modelIds, mySnapShotId);
    	}
    	else {
    		newCoWork = coWorkService.postNewCoWork(modelIds);
    	}
    	boolean isChatLogin = chatAPIService.procAuth(userInfo);
    	if (isChatLogin) {
    		boolean isSuccessCreateTeam = chatAPIService.procCreateTeam(newCoWork);
    		if (!isSuccessCreateTeam) {
    			throw new InternalServerErrorException(message.getMessage("system.co_work_controller.cannot_create_chat_team"));
    		}
    	}
    	
    	if(mySnapShotId > 0) return "redirect:/coWork/modelingView/" + newCoWork.getId() +"?tabType=snapShotArea&tabItemId="+newCoWork.getCoWorkSnapShots().get(0).getId();
    	return "redirect:/coWork/modelingView/" + newCoWork.getId();
    }

    @GetMapping("/modelingView/{coWorkId}")
    public String modelingViewByCoWorkId(@PathVariable long coWorkId
            , @RequestParam(required = false, defaultValue = "bimModelArea") String tabType
            , @RequestParam(required = false, defaultValue = "0") long tabItemId
            , Model model) throws Exception {

        CoWork coWork = coWorkService.findByCoWorkId(coWorkId);

        if (coWork.getCoWorkJoiners().stream().filter(m -> m.getJoiner().getId() == userInfo.getId()).count() == 0)
            throw new ForbiddenException(message.getMessage("system.co_work_controller.joiner_is_not_this_co_work"));

        return getCoWorkPage(model, coWork, tabType, tabItemId);
    }

    @GetMapping("/modelingViewIssue/{coWorkIssueId}")
    public String modelingViewByCoWorkIssueId(@PathVariable long coWorkIssueId) {

        CoWork coWork = coWorkService.findByCoWorkIssueId(coWorkIssueId);
        return "redirect:/coWork/modelingView/" + coWork.getId() + "?tabType=issueArea&tabItemId=" + coWorkIssueId;
    }

    @NotNull
    private String getCoWorkPage(Model model, CoWork coWork, String tabType, long tabItemId) throws Exception {
        List<CoWorkModeling> coWorkModelings = coWork.getCoWorkModelings();
        ModelingViewDTO modeling = coWorkModelings.stream().findFirst().map(m -> new ModelingViewDTO(m.getModeling())).orElseGet(ModelingViewDTO::new);

        CoWorkIssue coWorkIssue = coWork.getCoWorkIssues().stream().findFirst().orElseGet(CoWorkIssue::new);

        boolean isChatLogin = chatAPIService.procAuth(userInfo);
        if (isChatLogin) {
            model.addAttribute("rocketChatId", userInfo.getRocketChatId());
            model.addAttribute("rocketChatToken", userInfo.getRocketChatToken());
        }

        model.addAttribute("coWork", coWork);
        model.addAttribute("isGoing", coWork.isGoing());
        model.addAttribute("isWriter", coWork.getWriteEmbedded().getWriter().getId() == userInfo.getId());

        model.addAttribute("chatting", coWork.getChatting());

        model.addAttribute("issues", coWork.getCoWorkIssues());
        model.addAttribute("issue", coWorkIssue);
        model.addAttribute("issueReports", coWorkIssue.getCoWorkIssueReports());

        model.addAttribute("modelings", coWorkModelings);
        model.addAttribute("modeling", modeling);

        //snapShot 개발
        List<CoWorkSnapShot> coWorkSnapShots = coWork.getSnapShotByIdAsc();
        model.addAttribute("snapShots", coWorkSnapShots);
        model.addAttribute("snapShot", coWorkSnapShots.stream().findFirst().orElseGet(CoWorkSnapShot::new));
        model.addAttribute("snapShotFiles", coWorkSnapShots.stream().findFirst().map(m -> m.getCoWorkSnapShotFiles()).orElseGet(() -> new ArrayList<>()));
        
        model.addAttribute("tabType", tabType);
        model.addAttribute("tabItemId", tabItemId);

        model.addAttribute("modelIds", coWork.getModelIds());
        model.addAttribute("viewerType", configService.findConfig("SYSTEM","BIM_RENDERING_COWORK").getCustomValue());

        if ("issueArea".equals(tabType)) alertService.setIsReadTrue(coWork.getProjectId(), userInfo.getId(), tabItemId, AlertType.CO_WORK_ISSUE);
        else if ("chattingArea".equals(tabType)) alertService.setIsReadTrue(coWork.getProjectId(), userInfo.getId(), tabItemId, AlertType.CHATTING);
        else alertService.setIsReadTrue(coWork.getProjectId(), userInfo.getId(), coWork.getId(), AlertType.CO_WORK);

        return "coWork/modelingView/page";
    }

    @PostMapping("/saveCoWorkIssue")
    public String saveCoWorkIssue(@ModelAttribute CoWorkIssueVO coWorkIssueVO, Model model) {
        CoWorkIssue coWorkIssue = coWorkService.saveCoWorkIssue(coWorkIssueVO);

        return coWorkIssueAreaPage(model, coWorkIssue, coWorkIssue.getCoWork());
    }

    @GetMapping("/issueArea/{coWorkIssueId}")
    public String issueArea(@PathVariable long coWorkIssueId, Model model) {

        CoWorkIssue coWorkIssue = coWorkService.findByCoWorKIssueId(coWorkIssueId);

        return coWorkIssueAreaPage(model, coWorkIssue, coWorkIssue.getCoWork());
    }

    @NotNull
    private String coWorkIssueAreaPage(Model model, CoWorkIssue coWorkIssue, CoWork coWork) {
        model.addAttribute("issues", coWork.getCoWorkIssues());
        model.addAttribute("issue", coWorkIssue);
        model.addAttribute("issueReports", coWorkIssue.getCoWorkIssueReports());
        model.addAttribute("isGoing", coWork.isGoing());
        return "coWork/modelingView/issueArea";
    }

    @DeleteMapping("/deleteCoWorkIssue/{coWorkIssueId}")
    public String deleteCoWorkIssue(@PathVariable long coWorkIssueId, Model model) {
        CoWork coWork = coWorkService.deleteCoWorkIssue(coWorkIssueId);
        CoWorkIssue coWorkIssue = coWork.getCoWorkIssues().stream().findFirst().orElseGet(CoWorkIssue::new);

        return coWorkIssueAreaPage(model, coWorkIssue, coWork);
    }

    @GetMapping("/issueDetail/{coWorkIssueId}")
    public String issueDetail(@PathVariable long coWorkIssueId, Model model) {
        CoWorkIssue coWorkIssue = coWorkService.findByCoWorKIssueId(coWorkIssueId);
        model.addAttribute("issue", coWorkIssue);
        model.addAttribute("issueReports", coWorkIssue.getCoWorkIssueReports());
        model.addAttribute("isGoing", coWorkIssue.getCoWork().isGoing());

        return "coWork/modelingView/issueItem";
    }

    @GetMapping("/coWorkIssueResult/{coWorkIssueId}")
    public String coWorkIssueResult(@PathVariable long coWorkIssueId, Model model) {
        CoWorkIssue coWorkIssue = coWorkService.findByCoWorKIssueId(coWorkIssueId);
        model.addAttribute("issueReports", coWorkIssue.getCoWorkIssueReports());
        model.addAttribute("isGoing", coWorkIssue.getCoWork().isGoing());

        return "coWork/modelingView/issueResult";
    }


    @GetMapping("/bimModelDetail/{modelingId}")
    public String bimModelDetail(@PathVariable long modelingId, Model model) {
        CoWorkModeling coWorkModeling = coWorkService.findModeling(modelingId);
        model.addAttribute("modeling", new ModelingViewDTO(coWorkModeling.getModeling()));

        return "coWork/modelingView/bimModelDetail";
    }
    
    @GetMapping("/snapShotArea/{snapShotId}")
    public String snapShotArea(@PathVariable long snapShotId, Model model) {
    	CoWorkSnapShot coWorkSnapShot = coWorkService.findSnapShot(snapShotId);
    	
    	model.addAttribute("snapShots", coWorkSnapShot.getCoWork().getSnapShotByIdAsc());
    	model.addAttribute("snapShot", coWorkSnapShot);
    	model.addAttribute("snapShotFiles", coWorkSnapShot.getCoWorkSnapShotFiles());
    	model.addAttribute("isGoing", coWorkSnapShot.getCoWork().isGoing());
    	return "coWork/modelingView/snapShotArea";
    }
    
    @DeleteMapping("/deleteSnapShot/{snapShotId}")
    public String deleteSnapShot(@PathVariable long snapShotId, Model model) {
    	CoWork coWork = coWorkService.deleteSnapShot(snapShotId);
    	List<CoWorkSnapShot> coWorkSnapShots = coWork.getSnapShotByIdAsc();
    	CoWorkSnapShot coWorkSnapShot = coWorkSnapShots.stream().findFirst().orElseGet(CoWorkSnapShot::new);
    	boolean isGoing = coWorkSnapShot.getCoWork() == null ? true : coWorkSnapShot.getCoWork().isGoing();
    	
    	model.addAttribute("snapShots", coWorkSnapShots);
    	model.addAttribute("snapShot", coWorkSnapShot);
    	model.addAttribute("snapShotFiles", coWorkSnapShots.stream().findFirst().map(m -> m.getCoWorkSnapShotFiles()).orElseGet(() -> new ArrayList<>()));
    	model.addAttribute("isGoing", isGoing);
    	return "coWork/modelingView/snapShotArea";
    }

    @NotNull
    private String coWorkSnapShotItem(Model model, CoWork coWork) {
        model.addAttribute("snapShots", coWork.getSnapShots());
        model.addAttribute("isGoing", coWork.isGoing());
        return "coWork/modelingView/snapShotArea";
    }
    
    @DeleteMapping("/deleteSnapShotFile/{coWorkSnapShotFileId}")
    public String deleteSnapShotFile(@PathVariable long coWorkSnapShotFileId, Model model) {
    	CoWorkSnapShot coWorkSnapShot = coWorkService.deleteSnapShotFile(coWorkSnapShotFileId);
    	
    	return getCoWorkSnapShotItem(model, coWorkSnapShot);
    }
    
    @GetMapping("/snapShotFileList/{snapShotId}")
    public String snapShotFileList(@PathVariable long snapShotId, Model model) {
    	CoWorkSnapShot coWorkSnapShot = coWorkService.findSnapShot(snapShotId);
    	return getCoWorkSnapShotItem(model, coWorkSnapShot);
    }
    
    @NotNull
    private String getCoWorkSnapShotItem(Model model, CoWorkSnapShot coWorkSnapShot) {
    	model.addAttribute("snapShot", coWorkSnapShot);
    	model.addAttribute("snapShotFiles", coWorkSnapShot.getCoWorkSnapShotFiles());
    	model.addAttribute("isGoing", coWorkSnapShot.getCoWork().isGoing());

    	return "coWork/modelingView/snapShotFileList";
    }

    //endregion

    //region 캘린더
    @GetMapping("/calendar")
    public String calendar(Model model) {
        model.addAttribute("scheduleTypes", configService.findScheduleConfigs());

        return "coWork/calendar/page";
    }
    //endregion
}