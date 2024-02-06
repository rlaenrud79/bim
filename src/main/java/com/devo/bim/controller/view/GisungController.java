package com.devo.bim.controller.view;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import com.devo.bim.component.NumberToKorean;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.GisungStatus;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.GisungProcessItemCostDetailDTODslRepository;
import com.devo.bim.repository.dsl.ProcessItemDslRepository;
import com.devo.bim.repository.spring.GisungProcessItemCostDetailRepository;
import com.devo.bim.repository.spring.ProcessInfoRepository;
import com.devo.bim.repository.spring.ProcessItemRepository;
import com.devo.bim.service.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devo.bim.component.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/gisung")
@RequiredArgsConstructor
public class GisungController extends AbstractController {

    private final WorkService workService;
    private final GisungReportService gisungReportService;
    private final GisungService gisungService;
    private final GisungAggregationService gisungAggregationService;
    private final GisungContractManagerService gisungContractManagerService;
    private final GisungPaymentService gisungPaymentService;
    private final ProjectService projectService;
    private final WorkAmountService workAmountService;
    private final ProcessItemRepository processItemRepository;
    private final ProcessItemDslRepository processItemDslRepository;
    private final ProcessInfoRepository processInfoRepository;
    private final ProcessCostService processCostService;

    private final GisungProcessItemCostDetailRepository gisungProcessItemCostDetailRepository;
    private final GisungProcessItemCostDetailDTODslRepository gisungProcessItemCostDetailDTODslRepository;

    @GetMapping("/gisungReportList")
    public String gisungReportList(@ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_documentList) Pageable pageable
            , Model model) {

        // work
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);

        // document
        searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungReportVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

        setPagingConfig(model, pageGisungReportDTOs);

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("searchGisungReportVO", searchGisungReportVO);
        model.addAttribute("list", pageGisungReportDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungReportList/page";
    }

    @GetMapping("/gisungReportListCardBody")
    public String gisungReportListCardBody(@ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_gisungReportList) Pageable pageable
            , Model model) {
         // gisungReport
         searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
         searchGisungReportVO.setProjectId(userInfo.getProjectId());
         PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

         setPagingConfig(model, pageGisungReportDTOs);

         model.addAttribute("searchGisungReportVO", searchGisungReportVO);
         model.addAttribute("list", pageGisungReportDTOs.getPageList());
         model.addAttribute("userInfoId", userInfo.getId());
         model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
         model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungReportList/cardBody";
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }


    @GetMapping("/addGisungReport")
    public String addGisungReport(long id, Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);

        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("gisungId" , id);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/addGisungReport";
    }

    @GetMapping("/updateGisungReport")
    public String updateGisungReport(long id, Model model) {
        GisungReport gisungReport = gisungReportService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("gisungReport", gisungReport);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/updateGisungReport";
    }

    @GetMapping("/gisungList")
    public String gisungList(@ModelAttribute SearchGisungVO searchGisungVO
            , @PageableDefault(size = defaultPageSize_gisungList) Pageable pageable
            , Model model) {

        // document
        searchGisungVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungDTO> pageGisungDTOs = gisungService.findGisungDTOs(searchGisungVO, pageable);

        setPagingConfig(model, pageGisungDTOs);

        model.addAttribute("searchGisungVO", searchGisungVO);
        model.addAttribute("list", pageGisungDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungList/page";
    }

    @GetMapping("/gisungListCardBody")
    public String gisungListCardBody(@ModelAttribute SearchGisungVO searchGisungVO
            , @PageableDefault(size = defaultPageSize_gisungList) Pageable pageable
            , Model model) {
        // gisungReport
        searchGisungVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungDTO> pageGisungDTOs = gisungService.findGisungDTOs(searchGisungVO, pageable);

        setPagingConfig(model, pageGisungDTOs);

        model.addAttribute("searchReportVO", searchGisungVO);
        model.addAttribute("list", pageGisungDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungList/cardBody";
    }

    @GetMapping("/gisungForm")
    public String gisungForm(long id, Integer no, Model model) {

        Project project = projectService.findMyProject();
        Gisung gisung = gisungService.findById(id);
        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        Integer gisungItemDetailRowSpanCount = gisungService.getGisungItemDetailRowSpanCnt(gisungItem.getId());

        model.addAttribute("project", project);
        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungItem", gisungItem);
        model.addAttribute("gisungItemDetails", gisungService.findGisungItemDetailByGisungItemId(gisungItem.getId()));
        model.addAttribute("gisungItemDetails01", gisungService.getGisungItemDetailList01(gisungItem.getId()));
        model.addAttribute("gisungItemDetails02", gisungService.getGisungItemDetailList02(gisungItem.getId()));
        model.addAttribute("gisungItemDetailRowSpanCount", gisungItemDetailRowSpanCount);
        model.addAttribute("no", no);
        return "gisung/gisungList/form";
    }

    @GetMapping("/gisungListForm")
    public String gisungListForm(long id, Integer no, Model model) {
        if (no == null) {
            no = 1;
        }
        Project project = projectService.findMyProject();
        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        Integer gisungItemDetailRowSpanCount = gisungService.getGisungItemDetailRowSpanCnt(gisungItem.getId());

        model.addAttribute("project", project);
        model.addAttribute("gisungItem", gisungItem);
        model.addAttribute("gisungItemDetails", gisungService.findGisungItemDetailByGisungItemId(gisungItem.getId()));
        model.addAttribute("gisungItemDetails01", gisungService.getGisungItemDetailList01(gisungItem.getId()));
        model.addAttribute("gisungItemDetails02", gisungService.getGisungItemDetailList02(gisungItem.getId()));
        model.addAttribute("gisungItemDetailRowSpanCount", gisungItemDetailRowSpanCount);
        model.addAttribute("no", no);
        return "gisung/gisungList/document"+no+".html";
    }

    /**
     * 기성인쇄 목록
     * @param searchGisungVO
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/gisungPrintList")
    public String gisungPrintList(@ModelAttribute SearchGisungVO searchGisungVO
            , @PageableDefault(size = defaultPageSize_gisungList) Pageable pageable
            , Model model) {

        // document
        searchGisungVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungDTO> pageGisungDTOs = gisungService.findGisungDTOs(searchGisungVO, pageable);

        setPagingConfig(model, pageGisungDTOs);

        model.addAttribute("searchGisungVO", searchGisungVO);
        model.addAttribute("list", pageGisungDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/page";
    }

    @GetMapping("/gisungPrintListCardBody")
    public String gisungPrintListCardBody(@ModelAttribute SearchGisungVO searchGisungVO
            , @PageableDefault(size = defaultPageSize_gisungList) Pageable pageable
            , Model model) {
        // gisungReport
        searchGisungVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungDTO> pageGisungDTOs = gisungService.findGisungDTOs(searchGisungVO, pageable);

        setPagingConfig(model, pageGisungDTOs);

        model.addAttribute("searchReportVO", searchGisungVO);
        model.addAttribute("list", pageGisungDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/cardBody";
    }

    @GetMapping("/gisungPrintReportList")
    public String gisungPrintReportList(@ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_gisungReportList) Pageable pageable
            , Model model) {
        // gisungReport
        searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungReportVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

        setPagingConfig(model, pageGisungReportDTOs);

        model.addAttribute("searchGisungReportVO", searchGisungReportVO);
        model.addAttribute("list", pageGisungReportDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/reportList";
    }

    @GetMapping("/gisungPrintReportListCardBody")
    public String gisungPrintReportListCardBody(@ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_gisungReportList) Pageable pageable
            , Model model) {
        // gisungReport
        searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungReportVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

        setPagingConfig(model, pageGisungReportDTOs);

        model.addAttribute("searchGisungReportVO", searchGisungReportVO);
        model.addAttribute("list", pageGisungReportDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/reportCardBody";
    }

    @GetMapping("/gisungPrintForm")
    public String gisungPrintForm(long id, Integer no, @ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_gisungReportList) Pageable pageable
            , Model model) {

        Project project = projectService.findMyProject();

        switch (no) {
            case 1:
                getGisungPrintDocument1(id, no, model, false);
                break;
            case 2:
                getGisungPrintDocument2(id, no, model, false);
                break;
            case 3:
                getGisungPrintDocument3(id, no, model, false);
                break;
            case 4:
                getGisungPrintDocument4(id, no, model, false);
                break;
            case 5:
                //getGisungPrintDocument5(id, no, model);
                getGisungPrintDocument6(id, no, 1, model);
                break;
            case 6:
                getGisungPrintDocument6(id, no, 2, model);
                break;
            case 9:
                getGisungPrintDocument9(id, no, model);
                break;
            case 10:
                getGisungPrintDocument10(id, no, model);
                break;
            default:
                getGisungPrintDocument1(id, no, model, false);
                break;
        }

        model.addAttribute("project", project);
        getGisungProcessItemList(id, "", model);

        // gisungReport
        searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungReportVO.setProjectId(userInfo.getProjectId());
        searchGisungReportVO.setGisungId(id);
        PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

        setPagingConfig(model, pageGisungReportDTOs);

        model.addAttribute("searchGisungReportVO", searchGisungReportVO);
        model.addAttribute("reportList", pageGisungReportDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/form";
    }

    @GetMapping("/gisungPrintListForm")
    public String gisungPrintListForm(long id, Integer no, @ModelAttribute SearchGisungReportVO searchGisungReportVO
            , @PageableDefault(size = defaultPageSize_gisungReportList) Pageable pageable
            , Model model) {
        if (no == null) {
            no = 1;
        }
        Project project = projectService.findMyProject();

        switch (no) {
            case 1:
                getGisungPrintDocument1(id, no, model, false);
                break;
            case 2:
                getGisungPrintDocument2(id, no, model, false);
                break;
            case 3:
                getGisungPrintDocument3(id, no, model, false);
                break;
            case 4:
                getGisungPrintDocument4(id, no, model, false);
                break;
            case 5:
                //getGisungPrintDocument5(id, no, model);
                getGisungPrintDocument6(id, no, 1, model);
                break;
            case 6:
                getGisungPrintDocument6(id, no, 2, model);
                break;
            case 9:
                getGisungPrintDocument9(id, no, model);
                break;
            case 10:
                getGisungPrintDocument10(id, no, model);
                break;
            default:
                getGisungPrintDocument1(id, no, model, false);
                break;
        }

        model.addAttribute("project", project);
        getGisungProcessItemList(id, "", model);

        // gisungReport
        searchGisungReportVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungReportVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungReportDTO> pageGisungReportDTOs = gisungReportService.findGisungReportDTOs(searchGisungReportVO, pageable);

        setPagingConfig(model, pageGisungReportDTOs);

        model.addAttribute("searchGisungReportVO", searchGisungReportVO);
        model.addAttribute("list", pageGisungReportDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPrintList/document"+no+".html";
    }

    @GetMapping("/modalGisungItem")
    public String modalGisungItem(long id, Integer no, long coverTableId, Model model) {
        if (no == null) {
            no = 1;
        }
        Project project = projectService.findMyProject();

        switch (no) {
            case 1:
                getGisungPrintDocument1(id, no, model, true);
                break;
            case 2:
                getGisungPrintDocument2(id, no, model, true);
                break;
            case 3:
                getGisungPrintDocument3(id, no, model, true);
                break;
            case 4:
                getGisungPrintDocument4(id, no, model, true);
                break;
            case 5:
                //getGisungPrintDocument5(id, no, model);
                getGisungPrintDocument6(id, no, 1, model);
                break;
            case 6:
                getGisungPrintDocument6(id, no, 2, model);
                break;
            case 9:
                getGisungCover(id, no, coverTableId, model);
                break;
            case 10:
                getGisungTable(id, no, coverTableId, model);
                break;
            default:
                getGisungPrintDocument1(id, no, model, true);
                break;
        }

        model.addAttribute("project", project);

        return "gisung/modal/gisungDocument"+no+".html";
    }
    @GetMapping("/printGisungItem")
    public String printGisungItem(long id, Integer no, long coverTableId, Model model) {
        if (no == null) {
            no = 1;
        }
        Project project = projectService.findMyProject();

        switch (no) {
            case 1:
                getGisungPrintDocument1(id, no, model, true);
                break;
            case 2:
                getGisungPrintDocument2(id, no, model, true);
                break;
            case 3:
                getGisungPrintDocument3(id, no, model, true);
                break;
            case 4:
                getGisungPrintDocument4(id, no, model, true);
                break;
            case 5:
                //getGisungPrintDocument5(id, no, model);
                getGisungPrintDocument6(id, no, 1, model);
                break;
            case 6:
                getGisungPrintDocument6(id, no, 2, model);
                break;
            case 9:
                getGisungCover(id, no, coverTableId, model);
                break;
            case 10:
                getGisungTable(id, no, coverTableId, model);
                break;
            default:
                getGisungPrintDocument1(id, no, model, true);
                break;
        }

        model.addAttribute("project", project);

        return "gisung/print/printDocument"+no+".html";
    }

    /**
     * 준공검사조서
     */
    public void getGisungPrintDocument1(long id, Integer no, Model model, boolean isPrint) {
        Project project = projectService.findMyProject();
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        Boolean isWriting = (gisung.getStatus() == GisungStatus.WRITING);

        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        GisungItem gisungItem6 = gisungService.findByGisungIdAndDocumentNo(id, 6);
        if (!isWriting) {
            if (gisungItem6 == null || gisungItem6.getId() == 0) {
                isWriting = true;
            }
        }
        // 기성 계약자 정보
        List<GisungContractor> gisungContractors = gisungService.findGisungContractorByGisungIdAndDocumentNo(id, no);

        List<String> filenameLists = new ArrayList<>();
        if (gisungItem != null && gisungItem.getId() > 0) {
            if (gisungItem.getFilenameList() != null && !gisungItem.getFilenameList().equals("")) {
                filenameLists = Arrays.asList(gisungItem.getFilenameList().split("▥"));
            }
            String item03 = gisungItem.getItem03();
            String item04 = gisungItem.getItem04();
            String item05 = gisungItem.getItem05();
            String item06 = gisungItem.getItem06();
            String item08 = gisungItem.getItem08();
            if (isPrint) {
                item03 = Utils.convertDate(item03);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item08 = Utils.convertDate(item08);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    gisungItem.getItem01(),
                    gisungItem.getItem02(),
                    item03,
                    item04,
                    item05,
                    item06,
                    gisungItem.getItem07(),
                    item08,
                    gisungItem.getItem09(),
                    gisungItem.getItem10(),
                    gisungItem.getItem11(),
                    gisungItem.getItem12(),
                    gisungItem.getItem13(),
                    gisungItem.getDocumentNo(),
                    gisungItem.getFilenameList());
            model.addAttribute("gisungItem", gisungItemDTO);
        } else {
            String item03 = String.valueOf(project.getStartDesignDate()).substring(0, 10);
            String item04 = String.valueOf(project.getStartDate()).substring(0, 10);
            String item05 = String.valueOf(project.getEndDate()).substring(0, 10);
            String item06 = Utils.todayString();
            String item08 = Utils.todayString();
            if (isPrint) {
                item03 = Utils.convertDate(item03);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item08 = Utils.convertDate(item08);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    gisung.getSumItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),
                    gisung.getItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),
                    item03,
                    item04,
                    item05,
                    item06,
                    gisung.getItemPaidProgressRate().toString(),
                    item08,
                    "",
                    "",
                    "",
                    "",
                    "",
                    no,
                    "");
            model.addAttribute("gisungItem", gisungItemDTO);
        }

        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungContractors", gisungContractors);
        model.addAttribute("filenameLists", filenameLists);
        model.addAttribute("no", no);
        model.addAttribute("isWriting", isWriting);
    }

    /**
     * 준공계
     */
    public void getGisungPrintDocument2(long id, Integer no, Model model, boolean isPrint) {
        Project project = projectService.findMyProject();

        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        Boolean isWriting = (gisung.getStatus() == GisungStatus.WRITING);

        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        GisungItem gisungItem6 = gisungService.findByGisungIdAndDocumentNo(id, 6);
        if (!isWriting) {
            if (gisungItem6 == null || gisungItem6.getId() == 0) {
                isWriting = true;
            }
        }

        // 기성 계약자 정보
        List<GisungContractor> gisungContractors = gisungService.findGisungContractorByGisungIdAndDocumentNo(id, no);

        String contactAmountStr = "";
        if (gisung.getContractAmount() > 0) {
            contactAmountStr = NumberToKorean.convertToKorean(gisung.getContractAmount());
        }

        HashMap<String, String> filenameLists = new HashMap<String, String>();
        if (gisungItem != null && gisungItem.getId() > 0) {
            if (gisungItem.getFilenameList() != null && !gisungItem.getFilenameList().equals("")) {
                String inputString = gisungItem.getFilenameList();
                String[] pairs = inputString.split("▥");
                if (pairs.length > 1) {
                    for (String pair : pairs) {
                        String[] keyAndValue = pair.split("\\|\\|");
                        if (filenameLists.size() > 1) {
                            filenameLists.put(keyAndValue[0], keyAndValue[1]);
                        }
                    }
                } else {
                    String[] keyAndValue = inputString.split("\\|\\|");
                    if (filenameLists.size() > 1) {
                        filenameLists.put(keyAndValue[0], keyAndValue[1]);
                    }
                }
            }
            String item01 = gisungItem.getItem01();
            String item04 = gisungItem.getItem04();
            String item05 = gisungItem.getItem05();
            String item06 = gisungItem.getItem06();
            String item07 = gisungItem.getItem07();
            String item08 = gisungItem.getItem08();
            if (isPrint) {
                item01 = Utils.convertDate2(item01);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item07 = Utils.convertDate(item07);
                item08 = Utils.convertDate(item08);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    item01,
                    gisungItem.getItem02(),
                    gisungItem.getItem03(),
                    item04,
                    item05,
                    item06,
                    item07,
                    item08,
                    gisungItem.getItem09(),
                    gisungItem.getItem10(),
                    gisungItem.getItem11(),
                    gisungItem.getItem12(),
                    gisungItem.getItem13(),
                    gisungItem.getDocumentNo(),
                    gisungItem.getFilenameList());
            model.addAttribute("gisungItem", gisungItemDTO);
        } else {
            String item01 = Utils.todayString();
            String item04 = String.valueOf(project.getStartDesignDate()).substring(0, 10);
            String item05 = String.valueOf(project.getStartDate()).substring(0, 10);
            String item06 = String.valueOf(project.getEndDate()).substring(0, 10);
            String item07 = Utils.todayString();
            String item08 = Utils.todayString();
            if (isPrint) {
                item01 = Utils.convertDate2(item01);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item07 = Utils.convertDate(item07);
                item08 = Utils.convertDate(item08);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    item01,
                    contactAmountStr,
                    String.valueOf(gisung.getContractAmount()),
                    item04,
                    item05,
                    item06,
                    item07,
                    item08,
                    "한국도로공사 화도이천건설사업단장",
                    "",
                    "",
                    "",
                    "",
                    no,
                    "");
            model.addAttribute("gisungItem", gisungItemDTO);
        }

        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungContractors", gisungContractors);
        model.addAttribute("filenameLists", filenameLists);
        model.addAttribute("contactAmountStr", contactAmountStr);
        model.addAttribute("no", no);
        model.addAttribute("isWriting", isWriting);
    }

    /**
     * 기성검사조서
     */
    public void getGisungPrintDocument3(long id, Integer no, Model model, boolean isPrint) {
        Project project = projectService.findMyProject();
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        Boolean isWriting = (gisung.getStatus() == GisungStatus.WRITING);

        // 총계 당해년도
        WorkAmountDTO workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), Utils.todayString(), 0, 0);

        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        GisungItem gisungItem6 = gisungService.findByGisungIdAndDocumentNo(id, 6);
        if (!isWriting) {
            if (gisungItem6 == null || gisungItem6.getId() == 0) {
                isWriting = true;
            }
        }
        // 기성 계약자 정보
        List<GisungContractor> gisungContractors = gisungService.findGisungContractorByGisungIdAndDocumentNo(id, no);

        List<String> filenameLists = new ArrayList<>();
        if (gisungItem != null && gisungItem.getId() > 0) {
            if (gisungItem.getFilenameList() != null && !gisungItem.getFilenameList().equals("")) {
                filenameLists = Arrays.asList(gisungItem.getFilenameList().split("▥"));
            }
            String item03 = gisungItem.getItem03();
            String item04 = gisungItem.getItem04();
            String item05 = gisungItem.getItem05();
            String item06 = gisungItem.getItem06();
            String item09 = gisungItem.getItem09();
            if (isPrint) {
                item03 = Utils.convertDate(item03);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item09 = Utils.convertDate(item09);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    gisungItem.getTitle(),
                    gisung.getProjectId(),
                    gisungItem.getItem01(),
                    gisungItem.getItem02(),
                    item03,
                    item04,
                    item05,
                    item06,
                    gisungItem.getItem07(),
                    gisungItem.getItem08(),
                    item09,
                    gisungItem.getItem10(),
                    gisungItem.getItem11(),
                    gisungItem.getItem12(),
                    gisungItem.getItem13(),
                    gisungItem.getDocumentNo(),
                    gisungItem.getFilenameList());
            model.addAttribute("gisungItem", gisungItemDTO);
        } else {
            String item03 = String.valueOf(project.getStartDesignDate()).substring(0, 10);
            String item04 = String.valueOf(project.getStartDate()).substring(0, 10);
            String item05 = String.valueOf(project.getEndDate()).substring(0, 10);
            String item06 = Utils.todayString();
            String item09 = Utils.todayString();
            String item08 = "";
            String item07 = "";
            if (workAmountTotal.getTotalAmount() > 0) {
                item07 = gisung.getItemPaidCost().divide(new BigDecimal(workAmountTotal.getTotalAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();
            }
            if (workAmountTotal.getTotalAmount() > 0) {
                item08 = gisung.getSumItemPaidCost().divide(new BigDecimal(workAmountTotal.getTotalAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();
            }
            if (isPrint) {
                item03 = Utils.convertDate(item03);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item09 = Utils.convertDate(item09);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    gisung.getSumItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),
                    gisung.getItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),
                    item03,
                    item04,
                    item05,
                    item06,
                    item07,
                    item08,
                    item09,
                    "",
                    gisung.getGisungNo().toString(),
                    "",
                    "",
                    no,
                    "");
            /**
            GisungItemVO gisungItemVO = new GisungItemVO();
            gisungItemVO.setTitle(project.getName());
            gisungItemVO.setItem01(gisung.getSumItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString());
            gisungItemVO.setItem02(gisung.getItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString());
            gisungItemVO.setItem03(Utils.convertDate(String.valueOf(project.getStartDesignDate())));
            gisungItemVO.setItem04(Utils.convertDate(String.valueOf(project.getStartDate())));
            gisungItemVO.setItem05(Utils.convertDate(String.valueOf(project.getEndDate())));
            gisungItemVO.setItem06(Utils.convertDate(Utils.todayString()));
            gisungItemVO.setItem07(gisung.getItemPaidProgressRate().toString());
            gisungItemVO.setItem08(gisung.getItemPaidCost().divide(new BigDecimal(gisung.getContractAmount()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString());
            gisungItemVO.setItem09(Utils.convertDate(Utils.todayString()));
            gisungItemVO.setDocumentNo(no);
            gisungItemVO.setGisungNo(gisung.getGisungNo());
             **/
            model.addAttribute("gisungItem", gisungItemDTO);
        }

        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungContractors", gisungContractors);
        model.addAttribute("filenameLists", filenameLists);
        model.addAttribute("no", no);
        model.addAttribute("isWriting", isWriting);
    }

    /**
     * 기성부분 검사원
     */
    public void getGisungPrintDocument4(long id, Integer no, Model model, boolean isPrint) {
        Project project = projectService.findMyProject();

        // 기성 정보
        Gisung gisung = gisungService.findById(id);
        Boolean isWriting = (gisung.getStatus() == GisungStatus.WRITING);

        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        GisungItem gisungItem6 = gisungService.findByGisungIdAndDocumentNo(id, 6);
        if (!isWriting) {
            if (gisungItem6 == null || gisungItem6.getId() == 0) {
                isWriting = true;
            }
        }
        // 기성 계약자 정보 관리 리스트
        List<GisungContractManager> gisungContractManagers = gisungContractManagerService.findGisungContractManagerByProjectId(project.getId());
        // 기성 계약자 정보
        List<GisungContractor> gisungContractors = gisungService.findGisungContractorByGisungIdAndDocumentNo(id, no);

        String contactAmountStr = "";
        if (gisung.getContractAmount() > 0) {
            contactAmountStr = NumberToKorean.convertToKorean(gisung.getContractAmount());
        }

        HashMap<String, String> filenameLists = new HashMap<String, String>();
        if (gisungItem != null && gisungItem.getId() > 0) {
            if (gisungItem.getFilenameList() != null && !gisungItem.getFilenameList().equals("")) {
                String inputString = gisungItem.getFilenameList();
                String[] pairs = inputString.split("▥");
                if (pairs.length > 1) {
                    for (String pair : pairs) {
                        String[] keyAndValue = pair.split("\\|\\|");
                        if (keyAndValue.length > 1) {
                            filenameLists.put(keyAndValue[0], keyAndValue[1]);
                        }
                    }
                } else {
                    String[] keyAndValue = inputString.split("\\|\\|");
                    if (keyAndValue.length > 1) {
                        filenameLists.put(keyAndValue[0], keyAndValue[1]);
                    }
                }
            }
            String item01 = gisungItem.getItem01();
            String item04 = gisungItem.getItem04();
            String item05 = gisungItem.getItem05();
            String item06 = gisungItem.getItem06();
            String item11 = gisungItem.getItem11();
            if (isPrint) {
                item01 = Utils.convertDate2(item01);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item11 = Utils.convertDate(item11);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    item01,
                    gisungItem.getItem02(),
                    gisungItem.getItem03(),
                    item04,
                    item05,
                    item06,
                    gisungItem.getItem07(),
                    gisungItem.getItem08(),
                    gisungItem.getItem09(),     // 기성율
                    gisungItem.getItem10(),     // 누계 기성율
                    item11,
                    gisungItem.getItem12(),
                    gisungItem.getItem13(),
                    gisungItem.getDocumentNo(),
                    gisungItem.getFilenameList());
            model.addAttribute("gisungItem", gisungItemDTO);
        } else {
            String item01 = Utils.todayString();
            String item04 = String.valueOf(project.getStartDesignDate()).substring(0, 10);
            String item05 = String.valueOf(project.getStartDate()).substring(0, 10);
            String item06 = String.valueOf(project.getEndDate()).substring(0, 10);
            String item11 = Utils.todayString();
            String item09 = "";
            String item10 = "";
            if (gisung.getContractAmount() > 0) {
                item09 = gisung.getItemPaidCost().divide(new BigDecimal(gisung.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();
            }
            if (gisung.getContractAmount() > 0) {
                item10 = gisung.getSumItemPaidCost().divide(new BigDecimal(gisung.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString();
            }
            if (isPrint) {
                item01 = Utils.convertDate2(item01);
                item04 = Utils.convertDate(item04);
                item05 = Utils.convertDate(item05);
                item06 = Utils.convertDate(item06);
                item11 = Utils.convertDate(item11);
            }
            GisungItemDTO gisungItemDTO = new GisungItemDTO(gisungItem.getId(),
                    gisung,
                    project.getName(),
                    gisung.getProjectId(),
                    item01, // 일자
                    String.valueOf(gisung.getContractAmount()), // 계약금액
                    contactAmountStr,   // 계약금액 한글
                    item04, // 계약년월일
                    item05, // 착공년원일
                    item06, // 준공예정일
                    gisung.getItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),  // 금회 기성금(기성부분액)
                    gisung.getSumItemPaidCost().setScale(0, RoundingMode.HALF_UP).toString(),   // 누계 기성금(누계금액)
                    item09,    // 금회 기성률
                    item10, // 누적 기성률
                    item11,
                    "한국도로공사 화도이천건설사업단장",
                    gisung.getGisungNo().toString(),
                    no,
                    "");
            model.addAttribute("gisungItem", gisungItemDTO);
        }

        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungContractManagers", gisungContractManagers);
        model.addAttribute("gisungContractors", gisungContractors);
        model.addAttribute("filenameLists", filenameLists);
        model.addAttribute("contactAmountStr", contactAmountStr);
        model.addAttribute("no", no);
        model.addAttribute("isWriting", isWriting);
    }

    public List<GisungAggregationVO> setGisungItemDetail(List<GisungAggregation> gisungAggregations, BigDecimal prevTotalCost, long prevGisungId, String gtype, Integer documentNo, Boolean prevCheck) {
        List<String> getPrevGisungItemDetailList = gisungService.getGisungItemDetailGisungIdGtypeListData(prevGisungId, gtype, documentNo);
        Integer index = 0;
        List<GisungAggregationVO> gisungAggregationVOs = new ArrayList<>();
        for (GisungAggregation gisungAggregation : gisungAggregations) {
            BigDecimal cost = BigDecimal.valueOf(gisungAggregation.getCost());
            long prevCost = 0;
            BigDecimal todayCost = new BigDecimal(BigInteger.ZERO);
            BigDecimal sumRate = BigDecimal.ZERO;

            if (prevCheck && getPrevGisungItemDetailList.size() > 0 && getPrevGisungItemDetailList.size() > index && getPrevGisungItemDetailList.get(index) != null && !getPrevGisungItemDetailList.get(index).equals("")) {
                prevCost = Long.parseLong(getPrevGisungItemDetailList.get(index).trim());
            }
            if (prevTotalCost.compareTo(BigDecimal.ZERO) > 0) {
                todayCost = prevTotalCost.multiply(gisungAggregation.getPercent()).multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP);
                //System.out.println("gtype : " + gtype);
                //System.out.println("gisungAggregation.getTitle() : " + gisungAggregation.getTitle());
                //System.out.println("prevTotalCost : " + prevTotalCost);
                //System.out.println("gisungAggregation.getPercent() : " + gisungAggregation.getPercent());
                //System.out.println("todayCost : " + todayCost);
            }
            BigDecimal sumCost = todayCost.add(new BigDecimal(prevCost));
            if (cost.compareTo(BigDecimal.ZERO) > 0) {
                sumRate = sumCost.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            }
            /**
             System.out.println("prevCost : " + prevCost);
             System.out.println("todayCost : " + todayCost);
             System.out.println("gisungAggregation.getCost() : " + gisungAggregation.getCost());
             System.out.println("gisungAggregation.getPercent() : " + gisungAggregation.getPercent());
             System.out.println("sumCost : " + sumCost);
             System.out.println("sumRate : " + sumRate);
             **/
            GisungAggregationVO savedGisungAggregationVO = new GisungAggregationVO();
            savedGisungAggregationVO.setTitle(gisungAggregation.getTitle());
            savedGisungAggregationVO.setCost(gisungAggregation.getCost());
            savedGisungAggregationVO.setPrevCost(new BigDecimal(prevCost));
            savedGisungAggregationVO.setTodayCost(todayCost);
            savedGisungAggregationVO.setPercent(gisungAggregation.getPercent());
            savedGisungAggregationVO.setSumCost(sumCost);
            savedGisungAggregationVO.setSumRate(sumRate);
            gisungAggregationVOs.add(savedGisungAggregationVO);
            index++;
        }

        return gisungAggregationVOs;
    }

    /**
     * 기성부분집계표(연차)
     */
    public void getGisungPrintDocument6(long id, Integer no, Integer documentNo, Model model) {
        String html = "";
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        Integer yearCount = gisungService.getGisungYearCount(gisung.getYear(), id);
        Boolean prevCheck = true;
        if (no == 6) {  // 첫 연차별 집계표에서는 전회가 0
            if (yearCount == 0) {
                prevCheck = false;
            }
        }

        // 총계
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<VmGisungWorkCostDTO> gisungWorkCosts = gisungService.findGisungWorkCostByGisungIdListDTOs(id, gisung.getYear(), searchWorkVO);
        long workUpIdCount = gisungWorkCosts.stream().filter(m -> m.getUpId() > 0L).count();
        List<VmGisungWorkCostVO> gisungWorkCostVOS = new ArrayList<>();
        int depth = 0;
        int number = 1;
        for (VmGisungWorkCostDTO gisungWorkCostDTO : gisungWorkCosts) {
            VmGisungWorkCostVO savedGisungWorkCostVO = new VmGisungWorkCostVO();
            /**
            System.out.println("gisungWorkCostDTO.getWorkNameLocale() : " + gisungWorkCostDTO.getWorkNameLocale());
            System.out.println("gisungWorkCostDTO.getWorkTotalAmount() : " + gisungWorkCostDTO.getWorkTotalAmount());
            System.out.println("gisungWorkCostDTO.getWorkAmount() : " + gisungWorkCostDTO.getWorkAmount());
            System.out.println("gisungWorkCostDTO.getTotalPaidCost() : " + gisungWorkCostDTO.getTotalPaidCost());
            System.out.println("gisungWorkCostDTO.getPrevPaidCost() : " + gisungWorkCostDTO.getPrevPaidCost());
            System.out.println("gisungWorkCostDTO.getPaidCost() : " + gisungWorkCostDTO.getPaidCost());
            System.out.println("gisungWorkCostDTO.getYear() : " + gisungWorkCostDTO.getYear());
            System.out.println("gisungWorkCostDTO.getGisungId() : " + gisungWorkCostDTO.getGisungId());
            **/
            if (workUpIdCount > 0) {
                if (gisungWorkCostDTO.getUpId() == 0) {
                    savedGisungWorkCostVO.setWorkNameLocale(Utils.numberToLomaNumber(depth) + ". " + gisungWorkCostDTO.getWorkNameLocale());
                    depth = depth + 1;
                    number = 1;
                } else {
                    savedGisungWorkCostVO.setWorkNameLocale(Integer.toString(number) + ". " + gisungWorkCostDTO.getWorkNameLocale());
                    number = number + 1;
                }
            } else {
                savedGisungWorkCostVO.setWorkNameLocale(Integer.toString(number) + ". " + gisungWorkCostDTO.getWorkNameLocale());
                number = number + 1;
            }
            if (documentNo == 1) {  // 기성부분집계표(전체)
                savedGisungWorkCostVO.setWorkAmount(gisungWorkCostDTO.getWorkTotalAmount());
                if (gisungWorkCostDTO.getWorkTotalAmount() > 0) {
                    savedGisungWorkCostVO.setTotalPaidRate(gisungWorkCostDTO.getTotalPaidCost().divide(new BigDecimal(gisungWorkCostDTO.getWorkTotalAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                    savedGisungWorkCostVO.setPrevPaidCost(gisungWorkCostDTO.getPrevPaidCost());
                    savedGisungWorkCostVO.setTotalPaidCost(gisungWorkCostDTO.getTotalPaidCost());
                }
            } else {                // 기성부분집계표(연차)
                savedGisungWorkCostVO.setWorkAmount(gisungWorkCostDTO.getWorkAmount());
                if (gisungWorkCostDTO.getWorkAmount() > 0) {
                    if (prevCheck) {
                        savedGisungWorkCostVO.setPrevPaidCost(gisungService.getPrevPaidCost(gisung.getProjectId(), gisung.getYear(), gisungWorkCostDTO.getWorkId(), id));
                        savedGisungWorkCostVO.setTotalPaidCost(gisungWorkCostDTO.getTotalPaidCost());
                        savedGisungWorkCostVO.setTotalPaidRate(gisungWorkCostDTO.getTotalPaidCost().divide(new BigDecimal(gisungWorkCostDTO.getWorkAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                    } else {
                        savedGisungWorkCostVO.setTotalPaidCost(gisungWorkCostDTO.getPaidCost());
                        savedGisungWorkCostVO.setTotalPaidRate(gisungWorkCostDTO.getPaidCost().divide(new BigDecimal(gisungWorkCostDTO.getWorkAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            }
            savedGisungWorkCostVO.setPaidCost(gisungWorkCostDTO.getPaidCost());

            gisungWorkCostVOS.add(savedGisungWorkCostVO);
        }
        BigDecimal sumWorkAmount = new BigDecimal(BigInteger.ZERO); // 도급액
        if (documentNo == 1) {      // 전체
            sumWorkAmount = BigDecimal.valueOf(gisungWorkCosts.stream().mapToLong(t -> t.getWorkTotalAmount()).sum());
        } else {
            sumWorkAmount = BigDecimal.valueOf(gisungWorkCosts.stream().mapToLong(t -> t.getWorkAmount()).sum());
        }
        BigDecimal sumWorkCostPrevPaidCost = gisungWorkCostVOS.stream().map(t -> t.getPrevPaidCost()).reduce(BigDecimal.ZERO, BigDecimal::add); // 전회까지
        if (!prevCheck) {
            sumWorkCostPrevPaidCost = new BigDecimal(BigInteger.ZERO);
        }
        BigDecimal sumWorkCostPaidCost = gisungWorkCosts.stream().map(t -> t.getPaidCost()).reduce(BigDecimal.ZERO, BigDecimal::add); //금회
        BigDecimal sumWorkCostTotalPaidCost = sumWorkCostPrevPaidCost.add(sumWorkCostPaidCost); // 누계
        BigDecimal sumWorkCostRate = new BigDecimal(BigInteger.ZERO);   // 기성율
        if (sumWorkAmount.compareTo(BigDecimal.ZERO) > 0) {
            sumWorkCostRate = sumWorkCostTotalPaidCost.divide(sumWorkAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }

        GisungProcessItemVO gisungProcessItemVO = new GisungProcessItemVO();
        gisungProcessItemVO.setGisungId(id);

        //GisungProcessItem newGisungProcessItem = gisungProcessItemRepository.findByGisungIdAndPhasingCode(gisungProcessItemVO.getGisungId(), gisungProcessItem.getPhasingCode()).orElseGet(GisungProcessItem::new);
        long prevGisungId = gisungService.getGisungPrevId(id);
        BigDecimal totalSumPercent1 = BigDecimal.ZERO;
        BigDecimal totalSumPercent2 = BigDecimal.ZERO;
        BigDecimal totalSumPercent3 = BigDecimal.ZERO;
        BigDecimal totalSumPercent4 = BigDecimal.ZERO;

        //List<GisungAggregation> gisungAggregations1 = gisungAggregationService.findByYearAndGtype(gisung.getYear(), "1", 2);    //집계표항목설정(연차)
        String gtype = "1";
        //BigDecimal totalCost1 = sumWorkAmount;
        //BigDecimal totalPrevCost1 = sumWorkCostPrevPaidCost;
        //BigDecimal totalTodayCost1 = sumWorkCostPaidCost;
        //BigDecimal totalSumCost1 = sumWorkCostTotalPaidCost;
        List<GisungAggregationVO> gisungAggregations1 = setGisungItemDetail(gisungAggregationService.findByYearAndGtype(gisung.getYear(), gtype, documentNo), BigDecimal.ZERO, prevGisungId, gtype, no, prevCheck);    //집계표항목설정(연차)
        /**
        for (GisungAggregationVO gisungAggregationVO : gisungAggregations1) {
            totalCost1 = totalCost1.add(new BigDecimal(gisungAggregationVO.getCost()));
            totalPrevCost1 = totalPrevCost1.add(gisungAggregationVO.getPrevCost());
            totalTodayCost1 = totalTodayCost1.add(gisungAggregationVO.getTodayCost());
            totalSumCost1 = totalSumCost1.add(gisungAggregationVO.getSumCost());
        }
        totalCost1 = sumWorkAmount.add(BigDecimal.valueOf(gisungAggregations1.stream().mapToLong(t -> t.getCost()).sum()));
         **/
        BigDecimal totalCost1 = sumWorkAmount.add(gisungAggregations1.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add)); // sumWorkAmount.add(BigDecimal.valueOf(gisungAggregations1.stream().mapToLong(GisungAggregationVO::getCost).sum()));   // 도급액
        BigDecimal totalPrevCost1 = sumWorkCostPrevPaidCost.add(gisungAggregations1.stream().map(t -> t.getPrevCost()).reduce(BigDecimal.ZERO, BigDecimal::add));  // 전회까지
        BigDecimal totalTodayCost1 = sumWorkCostPaidCost.add(gisungAggregations1.stream().map(t -> t.getTodayCost()).reduce(BigDecimal.ZERO, BigDecimal::add));   //금일
        BigDecimal totalSumCost1 = sumWorkCostTotalPaidCost.add(gisungAggregations1.stream().map(t -> t.getSumCost()).reduce(BigDecimal.ZERO, BigDecimal::add));  // 누계
        System.out.println("totalCost1 "+ totalCost1 + "-------" + totalPrevCost1 + "-------" + totalTodayCost1 + "-------" + totalSumCost1);
        if (totalCost1.compareTo(BigDecimal.ZERO) > 0) {       // 기성율
            totalSumPercent1 = totalSumCost1.divide(totalCost1, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }

        gtype = "2";
        //BigDecimal totalCost2 = totalCost1;
        //BigDecimal totalPrevCost2 = totalPrevCost1;
        //BigDecimal totalTodayCost2 = totalTodayCost1;
        //BigDecimal totalSumCost2 = totalSumCost1;
        List<GisungAggregationVO> gisungAggregations2 = setGisungItemDetail(gisungAggregationService.findByYearAndGtype(gisung.getYear(), gtype, documentNo), totalTodayCost1, prevGisungId, gtype, no, prevCheck);    //집계표항목설정(연차)
        /**
        for (GisungAggregationVO gisungAggregationVO : gisungAggregations2) {
            totalCost2 = totalCost2.add(new BigDecimal(gisungAggregationVO.getCost()));
            totalPrevCost2 = totalPrevCost2.add(gisungAggregationVO.getPrevCost());
            totalTodayCost2 = totalTodayCost2.add(gisungAggregationVO.getTodayCost());
            totalSumCost2 = totalSumCost2.add(gisungAggregationVO.getSumCost());
        }
        System.out.println(totalCost2 + "-------" + totalPrevCost2 + "-------" + totalTodayCost2 + "-------" + totalSumCost2);
         **/
        BigDecimal totalCost2 = totalCost1.add(gisungAggregations2.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add));
        //totalCost1.add(new BigDecimal(gisungAggregations2.stream().map(t -> t.getCost()).reduce(0L, Long::sum))); // totalCost1.add(BigDecimal.valueOf(gisungAggregations2.stream().mapToLong(GisungAggregationVO::getCost).sum()));
        //totalCost2 = totalCost1.add(BigDecimal.valueOf(gisungAggregations2.stream().mapToLong(t -> t.getCost()).sum()));
        BigDecimal totalPrevCost2 = totalPrevCost1.add(gisungAggregations2.stream().map(t -> t.getPrevCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalTodayCost2 = totalTodayCost1.add(gisungAggregations2.stream().map(t -> t.getTodayCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalSumCost2 = totalSumCost1.add(gisungAggregations2.stream().map(t -> t.getSumCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        if (totalCost2.compareTo(BigDecimal.ZERO) > 0) {       // 기성율
            totalSumPercent2 = totalSumCost2.divide(totalCost2, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }
        System.out.println(totalCost2 + "-------" + totalPrevCost2 + "-------" + totalTodayCost2 + "-------" + totalSumCost2);

        gtype = "3";
        //BigDecimal totalCost3 = BigDecimal.ZERO;
        //totalCost3 = totalCost3.add(totalCost2);
        //BigDecimal totalPrevCost3 = totalPrevCost2;
        //BigDecimal totalTodayCost3 = totalTodayCost2;
        //BigDecimal totalSumCost3 = totalSumCost2;
        List<GisungAggregationVO> gisungAggregations3 = setGisungItemDetail(gisungAggregationService.findByYearAndGtype(gisung.getYear(), gtype, documentNo), totalTodayCost1, prevGisungId, gtype, no, prevCheck);    //집계표항목설정(연차)
        /**
        System.out.println(totalCost3 + "-------" + totalPrevCost3 + "-------" + totalTodayCost3 + "-------" + totalSumCost3);
        for (GisungAggregationVO gisungAggregationVO : gisungAggregations3) {
            totalCost3 = totalCost3.add(new BigDecimal(gisungAggregationVO.getCost()));
            totalPrevCost3 = totalPrevCost3.add(gisungAggregationVO.getPrevCost());
            totalTodayCost3 = totalTodayCost3.add(gisungAggregationVO.getTodayCost());
            totalSumCost3 = totalSumCost3.add(gisungAggregationVO.getSumCost());
            System.out.println(gisungAggregationVO.getCost() + "------" + totalCost3 + "-------" + totalPrevCost3 + "-------" + totalTodayCost3 + "-------" + totalSumCost3);
        }
         **/
        BigDecimal totalCost3 = totalCost2.add(gisungAggregations3.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add));
        //BigDecimal totalCost3 = totalCost2.add(new BigDecimal(gisungAggregations3.stream().map(t -> t.getCost()).reduce(0L, Long::sum)));
        BigDecimal totalPrevCost3 = totalPrevCost2.add(gisungAggregations3.stream().map(t -> t.getPrevCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalTodayCost3 = totalTodayCost2.add(gisungAggregations3.stream().map(t -> t.getTodayCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalSumCost3 = totalSumCost2.add(gisungAggregations3.stream().map(t -> t.getSumCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        if (totalCost3.compareTo(BigDecimal.ZERO) > 0) {       // 기성율
            totalSumPercent3 = totalSumCost3.divide(totalCost3, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }
        System.out.println(totalCost3 + "-------" + totalPrevCost3 + "-------" + totalTodayCost3 + "-------" + totalSumCost3);

        // 부가가치세
        BigDecimal totalSumPercentTax = BigDecimal.ZERO;
        BigDecimal totalTax = totalCost3.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalPrevTax = totalPrevCost3.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalTodayTax = totalTodayCost3.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalSumTax = totalSumCost3.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
        if (totalTax.compareTo(BigDecimal.ZERO) > 0) {       // 기성율
            totalSumPercentTax = totalSumTax.divide(totalTax, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }

        gtype = "4";
        //BigDecimal totalCost4 = totalCost3;
        //BigDecimal totalPrevCost4 = totalPrevCost3;
        //BigDecimal totalTodayCost4 = totalTodayCost3;
        //BigDecimal totalSumCost4 = totalSumCost3;
        List<GisungAggregationVO> gisungAggregations4 = setGisungItemDetail(gisungAggregationService.findByYearAndGtype(gisung.getYear(), gtype, documentNo), totalTodayCost1, prevGisungId, gtype, no, prevCheck);    //집계표항목설정(연차)
        /**
        for (GisungAggregationVO gisungAggregationVO : gisungAggregations4) {
            totalCost4 = totalCost4.add(new BigDecimal(gisungAggregationVO.getCost()));
            totalPrevCost4 = totalPrevCost4.add(gisungAggregationVO.getPrevCost());
            totalTodayCost4 = totalTodayCost4.add(gisungAggregationVO.getTodayCost());
            totalSumCost4 = totalSumCost4.add(gisungAggregationVO.getSumCost());
        }
        totalCost4 = totalCost4.add(totalTax);
        totalPrevCost4 = totalPrevCost4.add(totalPrevTax);
        totalTodayCost4 = totalTodayCost4.add(totalTodayTax);
        totalSumCost4 = totalSumCost4.add(totalSumTax);
         **/
        BigDecimal totalCost4 = totalCost3.add(totalTax).add(gisungAggregations4.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add));
        //BigDecimal totalCost4 = totalCost3.add(totalTax).add(new BigDecimal(gisungAggregations4.stream().map(t -> t.getCost()).reduce(0L, Long::sum))); // totalCost3.add(totalTax).add(BigDecimal.valueOf(gisungAggregations4.stream().mapToLong(GisungAggregationVO::getCost).sum()));
        BigDecimal totalPrevCost4 = totalPrevCost3.add(totalPrevTax).add(gisungAggregations4.stream().map(t -> t.getPrevCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalTodayCost4 = totalTodayCost3.add(totalTodayTax).add(gisungAggregations4.stream().map(t -> t.getTodayCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalSumCost4 = totalSumCost3.add(totalSumTax).add(gisungAggregations4.stream().map(t -> t.getSumCost()).reduce(BigDecimal.ZERO, BigDecimal::add));
        if (totalCost4.compareTo(BigDecimal.ZERO) > 0) {       // 기성율
            totalSumPercent4 = totalSumCost4.divide(totalCost4, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }

        GisungItem gisungItem = gisungService.findByGisungIdAndDocumentNo(id, no);
        GisungItem prevGisungItem = gisungService.findByGisungIdAndDocumentNo(prevGisungId, no);
        Integer gisungItemDetailRowSpanCount = gisungService.getGisungItemDetailRowSpanCnt(gisungItem.getId());

        model.addAttribute("html", html);
        model.addAttribute("list1", gisungAggregations1);
        model.addAttribute("totalCost1", totalCost1);
        model.addAttribute("totalPrevCost1", totalPrevCost1);
        model.addAttribute("totalTodayCost1", totalTodayCost1);
        model.addAttribute("totalSumCost1", totalSumCost1);
        model.addAttribute("totalSumPercent1", totalSumPercent1);
        model.addAttribute("list2", gisungAggregations2);
        model.addAttribute("totalCost2", totalCost2);
        model.addAttribute("totalPrevCost2", totalPrevCost2);
        model.addAttribute("totalTodayCost2", totalTodayCost2);
        model.addAttribute("totalSumCost2", totalSumCost2);
        model.addAttribute("totalSumPercent2", totalSumPercent2);
        model.addAttribute("list3", gisungAggregations3);
        model.addAttribute("totalCost3", totalCost3);
        model.addAttribute("totalPrevCost3", totalPrevCost3);
        model.addAttribute("totalTodayCost3", totalTodayCost3);
        model.addAttribute("totalSumCost3", totalSumCost3);
        model.addAttribute("totalSumPercent3", totalSumPercent3);
        model.addAttribute("list4", gisungAggregations4);
        model.addAttribute("totalCost4", totalCost4);
        model.addAttribute("totalPrevCost4", totalPrevCost4);
        model.addAttribute("totalTodayCost4", totalTodayCost4);
        model.addAttribute("totalSumCost4", totalSumCost4);
        model.addAttribute("totalSumPercent4", totalSumPercent4);
        model.addAttribute("totalTax", totalTax);
        model.addAttribute("totalPrevTax", totalPrevTax);
        model.addAttribute("totalTodayTax", totalTodayTax);
        model.addAttribute("totalSumTax", totalSumTax);
        model.addAttribute("totalSumPercentTax", totalSumPercentTax);
        model.addAttribute("sumWorkCostTotalPaidCost", sumWorkCostTotalPaidCost);
        model.addAttribute("sumWorkCostPrevPaidCost", sumWorkCostPrevPaidCost);
        model.addAttribute("sumWorkCostPaidCost", sumWorkCostPaidCost);
        model.addAttribute("sumWorkAmount", sumWorkAmount);
        model.addAttribute("sumWorkCostRate", sumWorkCostRate);
        model.addAttribute("gisungWorkCosts", gisungWorkCostVOS);
        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungItem", gisungItem);
        model.addAttribute("gisungItemDetails", gisungService.findGisungItemDetailByGisungItemId(gisungItem.getId()));
        model.addAttribute("gisungItemDetails01", gisungService.getGisungItemDetailList01(gisungItem.getId()));
        model.addAttribute("gisungItemDetails02", gisungService.getGisungItemDetailList02(gisungItem.getId()));
        model.addAttribute("prevGisungItemDetails", gisungService.findGisungItemDetailByGisungItemId(prevGisungItem.getId()));
        model.addAttribute("prevGisungItemDetails01", gisungService.getGisungItemDetailList01(prevGisungItem.getId()));
        model.addAttribute("prevGisungItemDetails02", gisungService.getGisungItemDetailList02(prevGisungItem.getId()));
        model.addAttribute("gisungItemDetailRowSpanCount", gisungItemDetailRowSpanCount);
        model.addAttribute("no", no);
        model.addAttribute("isWriting", (gisung.getStatus() == GisungStatus.WRITING));
    }

    /**
     * 표지
     */
    public void getGisungPrintDocument9(long id, Integer no, Model model) {
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        List<GisungCover> gisungCover = gisungService.findGisungCoverByGisungId(id);

        model.addAttribute("gisungCover", gisungCover);
        model.addAttribute("gisung", gisung);
        model.addAttribute("no", no);
    }

    public void getGisungCover(long id, Integer no, long coverId, Model model) {
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        // 표지 정보
        GisungCoverDTO gisungCoverDTO = gisungService.findGisungCoverById(coverId);

        model.addAttribute("gisungCover", gisungCoverDTO);
        model.addAttribute("gisung", gisung);
        model.addAttribute("no", no);
        model.addAttribute("coverId", coverId);
    }

    /**
     * 목록
     */
    public void getGisungPrintDocument10(long id, Integer no, Model model) {
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        List<GisungTable> gisungTables = gisungService.findGisungTableByGisungId(id);
        List<GisungTableVO> gisungTableVOs = new ArrayList<>();
        for (GisungTable gisungTable : gisungTables) {
            GisungTableVO gisungTableVO = new GisungTableVO();
            if (gisungTable.getContents() != null && !gisungTable.getContents().equals("")) {
                gisungTableVO.setId(gisungTable.getId());
                gisungTableVO.setContents(gisungTable.getContents());
                gisungTableVO.setContentsList(Arrays.asList(gisungTable.getContents().split("▥")));
                gisungTableVOs.add(gisungTableVO);
            }
        }

        model.addAttribute("gisungTable", gisungTableVOs);
        model.addAttribute("gisung", gisung);
        model.addAttribute("no", no);
    }

    public void getGisungTable(long id, Integer no, long tableId, Model model) {
        // 기성 정보
        Gisung gisung = gisungService.findById(id);

        // 표지 정보
        GisungTableDTO gisungTableDTO = gisungService.findGisungTableById(tableId);
        List<String> contentsLists = Arrays.asList(gisungTableDTO.getContents().split("▥"));

        model.addAttribute("contentsLists", contentsLists);
        model.addAttribute("gisung", gisung);
        model.addAttribute("no", no);
        model.addAttribute("coverId", tableId);
    }

    @GetMapping("/jobSheetList")
    public String jobSheetList(long id, Model model) {

        getGisungProcessItemList(id, "", model);
        return "gisung/gisungList/jobSheetList.html";
    }

    @GetMapping("/jobSheetForm")
    public String jobSheetForm(long id, Model model) {
        getGisungProcessItemList(id, "", model);
        return "gisung/gisungList/jobSheetForm.html";
    }

    private void getGisungProcessItemList(long id, String searchCompareResult, Model model) {
        long parentId = 0;
        Project project = projectService.findMyProject();
        Gisung gisung = gisungService.findById(id);

        // 공정현황
        // 총계 당해년도
        List<WorkAmountDTO> workAmounts = workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), Utils.todayString(), 0);
        WorkAmountDTO workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), Utils.todayString(), 0, 0);
        // 총계 누적
        List<WorkAmountDTO> workAmountSums = workAmountService.findAllWorkAmountList(project.getId(), configService.defaultSelectedLanguageConfigs());  // 누적
        WorkAmountDTO workAmountTotalSum = workAmountService.findSumWorkAmountList(project.getId());    // 누적

        List<VmGisungProcessItemDTO> vmGisungProcessItems = gisungService.findVmGisungProcessItemByGisungIdListDTOs(id, searchCompareResult);
        BigDecimal sumTaskCost = vmGisungProcessItems.stream().map(t -> t.getTaskCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumTodayProgressRate = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumPaidProgressRate = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumPaidCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumProgressRate = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumCompareCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumCompareProgressRate = new BigDecimal(BigInteger.ZERO);
        long sumTodayProgressAmount = 0;
        if (vmGisungProcessItems.size() > 0) {
            sumTodayProgressAmount = vmGisungProcessItems.stream().mapToLong(t -> t.getTodayProgressAmount()).sum();
            //sumTodayProgressRate = vmGisungProcessItems.stream().map(t -> t.getTodayProgressRate()).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(vmGisungProcessItems.size()), 2, RoundingMode.HALF_UP);
            //sumPaidProgressRate = vmGisungProcessItems.stream().map(t -> t.getPaidProgressRate()).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(vmGisungProcessItems.size()), 2, RoundingMode.HALF_UP);
            //sumProgressRate = vmGisungProcessItems.stream().map(t -> t.getProgressRate()).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(vmGisungProcessItems.size()), 2, RoundingMode.HALF_UP);
            sumPaidCost = vmGisungProcessItems.stream().map(t -> t.getPaidCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            sumCost = vmGisungProcessItems.stream().map(t -> t.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            //if (vmGisungProcessItems.stream().map(t -> t.getCompareCost()) != null) {
            //    sumCompareCost = vmGisungProcessItems.stream().map(t -> t.getCompareCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            //}
            sumCompareCost = vmGisungProcessItems.stream()
                    .filter(Objects::nonNull) // filter out null elements
                    .map(t -> {
                        BigDecimal compareCost = t.getCompareCost();
                        return compareCost != null ? compareCost : BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            sumTodayProgressRate = BigDecimal.valueOf(sumTodayProgressAmount).divide(sumTaskCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            if (sumPaidCost.compareTo(BigDecimal.ZERO) > 0) {
                sumPaidProgressRate = sumPaidCost.divide(sumTaskCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            }
            if (sumCost.compareTo(BigDecimal.ZERO) > 0) {
                sumProgressRate = sumCost.divide(sumTaskCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            }
            if (sumCompareCost.compareTo(BigDecimal.ZERO) > 0) {
                sumCompareProgressRate = sumCompareCost.divide(sumTaskCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            }
        }

        String compareResult = "";
        List<VmGisungProcessItemDTO> savedVmGisungProcessItemLists = new ArrayList<>();
        if (vmGisungProcessItems.size() > 0) {
            String prevCate1 = "";
            String prevCate2 = "";
            String prevCate3 = "";
            String prevCate4 = "";
            String prevCate5 = "";
            Integer i = 0;
            for (VmGisungProcessItemDTO vmGisungProcessItem : vmGisungProcessItems) {
                if ((vmGisungProcessItem.getCate1() != null && !prevCate1.equals(vmGisungProcessItem.getCate1()))
                        || (vmGisungProcessItem.getCate2() != null && !prevCate2.equals(vmGisungProcessItem.getCate2()))
                        || (vmGisungProcessItem.getCate3() != null && !prevCate3.equals(vmGisungProcessItem.getCate3()))
                        || (vmGisungProcessItem.getCate4() != null && !prevCate4.equals(vmGisungProcessItem.getCate4()))
                        || (vmGisungProcessItem.getCate5() != null && !prevCate5.equals(vmGisungProcessItem.getCate5()))) {
                    VmGisungProcessItemDTO savedVmGisungProcessItem = new VmGisungProcessItemDTO(vmGisungProcessItem.getId()
                            , vmGisungProcessItem.getGisungId()
                            , vmGisungProcessItem.getWorkId()
                            , ""
                            , vmGisungProcessItem.getParentTaskFullPath()
                            , vmGisungProcessItem.getComplexUnitPrice()
                            , vmGisungProcessItem.getTaskCost()
                            , vmGisungProcessItem.getPaidCost()
                            , vmGisungProcessItem.getPaidProgressRate()
                            , vmGisungProcessItem.getBeforeProgressRate()
                            , vmGisungProcessItem.getAfterProgressRate()
                            , vmGisungProcessItem.getTodayProgressRate()
                            , vmGisungProcessItem.getBeforeProgressAmount()
                            , vmGisungProcessItem.getAfterProgressAmount()
                            , vmGisungProcessItem.getTodayProgressAmount()
                            , vmGisungProcessItem.getCost()
                            , vmGisungProcessItem.getProgressRate()
                            , vmGisungProcessItem.getCompareCost()
                            , vmGisungProcessItem.getCompareProgressRate()
                            , vmGisungProcessItem.getCompareResult()
                            , vmGisungProcessItem.getAddItem()
                            , vmGisungProcessItem.getCate1()
                            , vmGisungProcessItem.getCate2()
                            , vmGisungProcessItem.getCate3()
                            , vmGisungProcessItem.getCate4()
                            , vmGisungProcessItem.getCate5()
                            , vmGisungProcessItem.getCate6()
                            , vmGisungProcessItem.getCate1Name()
                            , vmGisungProcessItem.getCate2Name()
                            , vmGisungProcessItem.getCate3Name()
                            , vmGisungProcessItem.getCate4Name()
                            , vmGisungProcessItem.getCate5Name()
                            , vmGisungProcessItem.getCate6Name());
                    savedVmGisungProcessItemLists.add(savedVmGisungProcessItem);
                    i = i + 1;
                }

                if (vmGisungProcessItem.getCompareResult() != null && vmGisungProcessItem.getCompareResult().equals("AP")) {
                    compareResult = "AP";
                }
                VmGisungProcessItemDTO savedVmGisungProcessItem = new VmGisungProcessItemDTO(vmGisungProcessItem.getId()
                        , vmGisungProcessItem.getGisungId()
                        , vmGisungProcessItem.getWorkId()
                        , vmGisungProcessItem.getPhasingCode()
                        , vmGisungProcessItem.getTaskName()
                        , vmGisungProcessItem.getComplexUnitPrice()
                        , vmGisungProcessItem.getTaskCost()
                        , vmGisungProcessItem.getPaidCost()
                        , vmGisungProcessItem.getPaidProgressRate()
                        , vmGisungProcessItem.getBeforeProgressRate()
                        , vmGisungProcessItem.getAfterProgressRate()
                        , vmGisungProcessItem.getTodayProgressRate()
                        , vmGisungProcessItem.getBeforeProgressAmount()
                        , vmGisungProcessItem.getAfterProgressAmount()
                        , vmGisungProcessItem.getTodayProgressAmount()
                        , vmGisungProcessItem.getCost()
                        , vmGisungProcessItem.getProgressRate()
                        , vmGisungProcessItem.getCompareCost()
                        , vmGisungProcessItem.getCompareProgressRate()
                        , vmGisungProcessItem.getCompareResult()
                        , vmGisungProcessItem.getAddItem()
                        , vmGisungProcessItem.getCate1()
                        , vmGisungProcessItem.getCate2()
                        , vmGisungProcessItem.getCate3()
                        , vmGisungProcessItem.getCate4()
                        , vmGisungProcessItem.getCate5()
                        , vmGisungProcessItem.getCate6()
                        , vmGisungProcessItem.getCate1Name()
                        , vmGisungProcessItem.getCate2Name()
                        , vmGisungProcessItem.getCate3Name()
                        , vmGisungProcessItem.getCate4Name()
                        , vmGisungProcessItem.getCate5Name()
                        , vmGisungProcessItem.getCate6Name());
                savedVmGisungProcessItemLists.add(savedVmGisungProcessItem);
                prevCate1 = vmGisungProcessItem.getCate1();
                prevCate2 = vmGisungProcessItem.getCate2();
                prevCate3 = vmGisungProcessItem.getCate3();
                prevCate4 = vmGisungProcessItem.getCate4();
                prevCate5 = vmGisungProcessItem.getCate5();
            }
        }

        GisungListExcelFile listExcelFileInfo = gisungService.findGisungListExcelFileByGisungId(id);

        model.addAttribute("project", project);
        model.addAttribute("gisungProcessItemlist", savedVmGisungProcessItemLists);
        model.addAttribute("sumTaskCost", sumTaskCost);
        model.addAttribute("sumTodayProgressRate", sumTodayProgressRate);
        model.addAttribute("sumTodayProgressAmount", sumTodayProgressAmount);
        model.addAttribute("sumPaidProgressRate", sumPaidProgressRate);
        model.addAttribute("sumPaidCost", sumPaidCost);
        model.addAttribute("sumProgressRate", sumProgressRate);
        model.addAttribute("sumCost", sumCost);
        model.addAttribute("sumCompareCost", sumCompareCost);
        model.addAttribute("sumCompareProgressRate", sumCompareProgressRate);
        model.addAttribute("gisung", gisung);
        model.addAttribute("listExcelFileInfo", listExcelFileInfo);
        model.addAttribute("isWriting", (gisung.getStatus() == GisungStatus.WRITING));
        model.addAttribute("isComplete", (gisung.getStatus() == GisungStatus.COMPLETE));
        model.addAttribute("compareResult", compareResult);
    }

    @GetMapping("/gisungBasicInfo")
    public String gisungBasicInfo(Model model) {

        Project project = projectService.findMyProject();

        model.addAttribute("project", project);
        return "gisung/gisungBasicInfo/page.html";
    }

    @GetMapping("/gisungBasicInfoCardBody")
    public String gisungBasicInfoCardBody(Integer no, Model model) {
        if (no == null) {
            no = 1;
        }
        Project project = projectService.findMyProject();

        model.addAttribute("project", project);
        model.addAttribute("no", no);
        return "gisung/gisungBasicInfo/basicDocument"+no+".html";
    }

    @GetMapping("/addGisung")
    public String addGisung(Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }

        List<String> months = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            String monthValue = String.format("%02d", i);
            months.add(monthValue);
        }

        Gisung gisung = new Gisung();
        Integer gisungNo = gisungService.getGisungMaxGisungNo();
        if (gisungNo == 0 || gisungNo == 1) {
            gisungNo = 1;
        } else {
            gisungNo = gisungNo + 1;
        }

        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("gisung", gisung);
        model.addAttribute("gisungNo", gisungNo);

        return "gisung/modal/addGisung";
    }

    @GetMapping("/updateGisung")
    public String addGisung(long id, Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }

        List<String> months = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            String monthValue = String.format("%02d", i);
            months.add(monthValue);
        }

        Gisung gisung = gisungService.findById(id);

        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("gisung", gisung);

        return "gisung/modal/addGisung";
    }

    @GetMapping("/gisungAggregationList")
    public String gisungAggregationList(Model model) {

        List<Map<String, Object>> gisungAggregations = gisungAggregationService.findGisungAggregationByYear(userInfo.getProjectId());

        model.addAttribute("list", gisungAggregations);
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungAggregationList/page";
    }

    @GetMapping("/gisungAggregationForm")
    public String gisungAggregationForm(String year, Integer no, Model model) {

        if (no == null) {
            no = 1;
        }
        if (year == null || year.equals("")) {
            year = Utils.todayString().substring(0, 4);
        }

        List<Integer> years = new ArrayList<>();
        for (int selYear = 2019; selYear <= LocalDate.now().getYear()+1; selYear++) {
            years.add(selYear);
        }

        Project project = projectService.findMyProject();
        String workYear = year;
        if (workYear == null || workYear.equals("")) {
            workYear = Utils.todayString().substring(0, 4);
        }

        // 총계
        List<WorkAmountDTO> workAmounts;
        WorkAmountDTO workAmountTotal;
        if (no == 1) {
            workAmounts = workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), workYear + "-12-31", 0);
            //workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), workYear + "-12-31", 0);
        } else {
            workAmounts = workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), workYear + "-12-31", 0);
            //workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), workYear + "-12-31", 0);
        }

        long workUpIdCount = workAmounts.stream().filter(m -> m.getUpId() > 0).count();
        int depth = 0;
        int number = 1;
        for (WorkAmountDTO workAmount : workAmounts) {
            if (workUpIdCount > 0) {
                if (workAmount.getUpId() == 0) {
                    workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), workYear + "-12-31", 0, (int) workAmount.getWorkId());
                    workAmount.setTotalAmount(workAmountTotal.getTotalAmount());
                    workAmount.setPrevAmount(workAmountTotal.getPrevAmount());
                    workAmount.setAmount(workAmountTotal.getAmount());
                    workAmount.setTodayAmount(workAmountTotal.getTodayAmount());
                    workAmount.setPaidCost(workAmountTotal.getPaidCost());
                    workAmount.setWorkNameLocale(Utils.numberToLomaNumber(depth) + ". " + workAmount.getWorkNameLocale());
                    depth = depth + 1;
                    number = 1;
                } else {
                    workAmount.setWorkNameLocale(Integer.toString(number) + ". " + workAmount.getWorkNameLocale());
                    number = number + 1;
                }
            } else {
                workAmount.setWorkNameLocale(Integer.toString(number) + ". " + workAmount.getWorkNameLocale());
                number = number + 1;
            }
        }

        List<GisungAggregation> gisungAggregations1 = gisungAggregationService.findByYearAndGtype(year, "1", no);
        List<GisungAggregation> gisungAggregations2 = gisungAggregationService.findByYearAndGtype(year, "2", no);
        List<GisungAggregation> gisungAggregations3 = gisungAggregationService.findByYearAndGtype(year, "3", no);
        List<GisungAggregation> gisungAggregations4 = gisungAggregationService.findByYearAndGtype(year, "4", no);
        List<GisungAggregation> gisungAggregations = gisungAggregationService.findByYearAndDocumentNo(year, no);
        //Integer gisungItemDetailRowSpanCount = gisungService.getGisungItemDetailRowSpanCnt(gisungItem.getId());

        model.addAttribute("years", years);
        model.addAttribute("selYear", year);
        model.addAttribute("project", project);
        model.addAttribute("list1", gisungAggregations1);
        model.addAttribute("list2", gisungAggregations2);
        model.addAttribute("list3", gisungAggregations3);
        model.addAttribute("list4", gisungAggregations4);
        model.addAttribute("list", gisungAggregations);
        //model.addAttribute("workAmountTotal", workAmountTotal);
        model.addAttribute("workAmounts", workAmounts);
        model.addAttribute("no", no);
        return "gisung/gisungAggregationList/form";
    }

    @GetMapping("/costDetail/{gisungProcessItemId}")
    public String costDetail(@PathVariable Long gisungProcessItemId, Model model) {
        getGisungProcessItemCostDetail(gisungProcessItemId, model);
        return "gisung/modal/costDetail";
    }

    private void getGisungProcessItemCostDetail(long gisungProcessItemId, Model model) {
        GisungProcessItem gisungProcessItem = gisungService.findGisungProcessItemById(gisungProcessItemId);
        Gisung gisung = gisungService.findById(gisungProcessItem.getGisung().getId());
        ProcessItem processItem = processItemRepository.findById(gisungProcessItem.getProcessItem().getId()).orElseGet(ProcessItem::new);
        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungService.findGisungProcessItemCostDetail(gisungProcessItemId);
        List<GisungProcessItemCostDetailDTO> savedProcessItemCostDetailLists = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        BigDecimal totalJobSheetProgressCount = new BigDecimal(0);
        long totalJobSheetProgressAmount = 0;
        BigDecimal totalPaidProgressCount = new BigDecimal(0);
        BigDecimal totalPaidCost = new BigDecimal(0);
        BigDecimal totalProgressCount = new BigDecimal(0);
        BigDecimal totalProgressCost = new BigDecimal(0);
        BigDecimal totalSumProgressCount = new BigDecimal(0);
        BigDecimal totalSumProgressCost = new BigDecimal(0);
        BigDecimal totalRemindProgressCount = new BigDecimal(0);
        BigDecimal totalRemindProgressCost = new BigDecimal(0);
        BigDecimal totalCompareProgressCount = new BigDecimal(0);
        BigDecimal totalCompareProgressCost = new BigDecimal(0);
        BigDecimal totalSumCompareProgressCount = new BigDecimal(0);
        BigDecimal totalSumCompareProgressCost = new BigDecimal(0);
        BigDecimal totalRemindCompareProgressCount = new BigDecimal(0);
        BigDecimal totalRemindCompareProgressCost = new BigDecimal(0);
        Integer firstRowCnt = 0;
        long complexUnitPrice = 0;
        String resultText = "";
        Integer totalCount = gisungProcessItemCostDetailDTOs.size();
        for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
            BigDecimal count = gisungProcessItemCostDetailDTO.getCount();
            BigDecimal cost = gisungProcessItemCostDetailDTO.getCost();
            BigDecimal unitPrice = gisungProcessItemCostDetailDTO.getUnitPrice();
            BigDecimal progressCount = gisungProcessItemCostDetailDTO.getProgressCount();
            BigDecimal progressCost = gisungProcessItemCostDetailDTO.getProgressCost();
            progressCost = progressCount.multiply(unitPrice);
            BigDecimal paidProgressCount = gisungProcessItemCostDetailDTO.getPaidProgressCount();
            BigDecimal paidCost = gisungProcessItemCostDetailDTO.getPaidCost();
            BigDecimal sumProgressCount = paidProgressCount.add(progressCount);
            BigDecimal sumProgressCost = paidCost.add(progressCost);
            BigDecimal remindProgressCount = count.subtract(sumProgressCount);
            BigDecimal remindProgressCost = cost.subtract(sumProgressCost);
            BigDecimal compareProgressCount = gisungProcessItemCostDetailDTO.getCompareProgressCount();
            BigDecimal compareProgressCost = gisungProcessItemCostDetailDTO.getCompareProgressCost();
            if (compareProgressCount != null) {
                compareProgressCost = compareProgressCount.multiply(unitPrice);
            }
            BigDecimal sumCompareProgressCount = new BigDecimal(0);
            BigDecimal remindCompareProgressCount = new BigDecimal(0);
            if (compareProgressCount != null) {
                sumCompareProgressCount = paidProgressCount.add(compareProgressCount);
                remindCompareProgressCount = count.subtract(sumCompareProgressCount);
            }
            BigDecimal sumCompareProgressCost = new BigDecimal(0);
            BigDecimal remindCompareProgressCost = new BigDecimal(0);
            if (compareProgressCost != null) {
                sumCompareProgressCost = paidCost.add(compareProgressCost);
                remindCompareProgressCost = cost.subtract(sumCompareProgressCost);
            }
            totalCost = totalCost.add(cost);
            totalJobSheetProgressCount = totalJobSheetProgressCount.add(gisungProcessItemCostDetailDTO.getJobSheetProgressCount());
            totalJobSheetProgressAmount = totalJobSheetProgressAmount + gisungProcessItemCostDetailDTO.getJobSheetProgressAmount();
            totalPaidProgressCount = totalPaidProgressCount.add(paidProgressCount);
            totalPaidCost = totalPaidCost.add(paidCost);
            totalProgressCount = totalProgressCount.add(progressCount);
            totalProgressCost = totalProgressCost.add(progressCost);
            totalSumProgressCount = totalSumProgressCount.add(sumProgressCount);
            totalSumProgressCost = totalSumProgressCost.add(sumProgressCost);
            totalRemindProgressCount = totalRemindProgressCount.add(remindProgressCount);
            totalRemindProgressCost = totalRemindProgressCost.add(remindProgressCost);
            if (compareProgressCount != null) {
                totalCompareProgressCount = totalCompareProgressCount.add(compareProgressCount);
            }
            if (compareProgressCost != null) {
                totalCompareProgressCost = totalCompareProgressCost.add(compareProgressCost);
            }
            totalSumCompareProgressCount = totalSumCompareProgressCount.add(sumCompareProgressCount);
            totalSumCompareProgressCost = totalSumCompareProgressCost.add(sumCompareProgressCost);
            totalRemindCompareProgressCount = totalRemindCompareProgressCount.add(remindCompareProgressCount);
            totalRemindCompareProgressCost = totalRemindCompareProgressCost.add(remindCompareProgressCost);
            if (gisungProcessItemCostDetailDTO.isFirst()) {
                firstRowCnt = firstRowCnt + 1;
            }
            GisungProcessItemCostDetailDTO savedGisungProcessItemCostDetailDTO = new GisungProcessItemCostDetailDTO(gisungProcessItemCostDetailDTO.getId()
                    , gisungProcessItemCostDetailDTO.getCode()
                    , gisungProcessItemCostDetailDTO.getName()
                    , count
                    , gisungProcessItemCostDetailDTO.getUnit()
                    , gisungProcessItemCostDetailDTO.getUnitPrice()
                    , cost
                    , gisungProcessItemCostDetailDTO.isFirst()
                    , gisungProcessItemCostDetailDTO.getJobSheetProgressCount()
                    , gisungProcessItemCostDetailDTO.getJobSheetProgressAmount()
                    , paidProgressCount
                    , paidCost
                    , progressCount
                    , progressCost
                    , sumProgressCount
                    , sumProgressCost
                    , remindProgressCount
                    , remindProgressCost
                    , compareProgressCount
                    , compareProgressCost
                    , gisungProcessItemCostDetailDTO.getCompareResult()
                    , sumCompareProgressCount
                    , sumCompareProgressCost
                    , remindCompareProgressCount
                    , remindCompareProgressCost);
            savedProcessItemCostDetailLists.add(savedGisungProcessItemCostDetailDTO);
        }
        if (firstRowCnt > 0) {
            complexUnitPrice = totalCost.divide(new BigDecimal(firstRowCnt)).setScale(0, RoundingMode.HALF_UP).longValue();
            resultText = Utils.num2String(complexUnitPrice) + " = " + Utils.num2String(totalCost.longValue()) + " / " + firstRowCnt.toString();
        }

        totalJobSheetProgressCount = new BigDecimal(totalJobSheetProgressAmount).divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalPaidProgressCount = totalPaidCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalProgressCount = totalProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalSumProgressCount = totalSumProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalRemindProgressCount = totalRemindProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalCompareProgressCount = totalCompareProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalSumCompareProgressCount = totalSumCompareProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalRemindCompareProgressCount = totalRemindCompareProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        model.addAttribute("gisungProcessItemId", gisungProcessItemId);
        model.addAttribute("list", savedProcessItemCostDetailLists);
        model.addAttribute("complexUnitPrice", complexUnitPrice);
        model.addAttribute("resultText", resultText);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalCost", totalCost);     // 공사비 총합
        model.addAttribute("totalJobSheetProgressCount", totalJobSheetProgressCount);     // 실적 수량 총합
        model.addAttribute("totalJobSheetProgressAmount", totalJobSheetProgressAmount);     // 실적 금액 총합
        model.addAttribute("totalPaidProgressCount", totalPaidProgressCount);     // 전회기성 수량 총합
        model.addAttribute("totalPaidCost", totalPaidCost);     // 전회기성 금액 총합
        model.addAttribute("totalProgressCount", totalProgressCount);     // 금회기성 수량 총합
        model.addAttribute("totalProgressCost", totalProgressCost);     // 금회기성 금액 총합
        model.addAttribute("totalSumProgressCount", totalSumProgressCount);     // 누적기성 수량 총합
        model.addAttribute("totalSumProgressCost", totalSumProgressCost);     // 누적기성 금액 총합
        model.addAttribute("totalRemindProgressCount", totalRemindProgressCount);     // 잔여기성 수량 총합
        model.addAttribute("totalRemindProgressCost", totalRemindProgressCost);     // 잔여기성 금액 총합
        model.addAttribute("totalCompareProgressCount", totalCompareProgressCount);     // 검증/비교 금회기성 수량 총합
        model.addAttribute("totalCompareProgressCost", totalCompareProgressCost);     // 검증/비교 금회기성 금액 총합
        model.addAttribute("totalSumCompareProgressCount", totalSumCompareProgressCount);     // 검증/비교 누적기성 수량 총합
        model.addAttribute("totalSumCompareProgressCost", totalSumCompareProgressCost);     // 검증/비교 누적기성 금액 총합
        model.addAttribute("totalRemindCompareProgressCount", totalRemindCompareProgressCount);     // 검증/비교 잔여기성 수량 총합
        model.addAttribute("totalRemindCompareProgressCost", totalRemindCompareProgressCost);     // 검증/비교 잔여기성 금액 총합
        model.addAttribute("gisung", gisung);
        model.addAttribute("processItem", processItem);
        model.addAttribute("isWriting", (gisung.getStatus() == GisungStatus.WRITING));
    }

    @GetMapping("/processCostDetail/{processItemId}/{rowState}")
    public String processCostDetail(@PathVariable Long processItemId, @PathVariable String rowState, Model model) {
        getProcessItemCostDetail(processItemId, rowState, model);
        return "gisung/modal/costDetail";
    }

    private void getProcessItemCostDetail(long processItemId, String rowState, Model model) {
        ProcessItem processItem = processCostService.findProcessItemById(processItemId);
        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = processCostService.findProcessItemCostDetail(processItemId, rowState);
        List<ProcessItemCostDetailDTO> savedProcessItemCostDetailLists = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        BigDecimal totalJobSheetProgressCount = new BigDecimal(0);
        long totalJobSheetProgressAmount = 0;
        BigDecimal totalPaidProgressCount = new BigDecimal(0);
        BigDecimal totalPaidCost = new BigDecimal(0);
        BigDecimal totalProgressCount = new BigDecimal(0);
        BigDecimal totalProgressCost = new BigDecimal(0);
        BigDecimal totalSumProgressCount = new BigDecimal(0);
        BigDecimal totalSumProgressCost = new BigDecimal(0);
        BigDecimal totalRemindProgressCount = new BigDecimal(0);
        BigDecimal totalRemindProgressCost = new BigDecimal(0);
        Integer firstRowCnt = 0;
        long complexUnitPrice = 0;
        String resultText = "";
        Integer totalCount = processItemCostDetailDTOs.size();
        for (ProcessItemCostDetailDTO processItemCostDetailDTO : processItemCostDetailDTOs) {
            BigDecimal count = processItemCostDetailDTO.getCount();
            BigDecimal cost = processItemCostDetailDTO.getCost();
            BigDecimal unitPrice = processItemCostDetailDTO.getUnitPrice();
            BigDecimal progressCount = processItemCostDetailDTO.getProgressCount();
            BigDecimal progressCost = processItemCostDetailDTO.getProgressCost();
            progressCost = progressCount.multiply(unitPrice);
            BigDecimal paidProgressCount = processItemCostDetailDTO.getPaidProgressCount();
            BigDecimal paidCost = processItemCostDetailDTO.getPaidCost();
            BigDecimal sumProgressCount = paidProgressCount.add(progressCount);
            BigDecimal sumProgressCost = paidCost.add(progressCost);
            BigDecimal remindProgressCount = count.subtract(sumProgressCount);
            BigDecimal remindProgressCost = cost.subtract(sumProgressCost);
            totalCost = totalCost.add(cost);
            totalJobSheetProgressCount = totalJobSheetProgressCount.add(processItemCostDetailDTO.getJobSheetProgressCount());
            totalJobSheetProgressAmount = totalJobSheetProgressAmount + processItemCostDetailDTO.getJobSheetProgressAmount();
            totalPaidProgressCount = totalPaidProgressCount.add(paidProgressCount);
            totalPaidCost = totalPaidCost.add(paidCost);
            totalProgressCount = totalProgressCount.add(progressCount);
            totalProgressCost = totalProgressCost.add(progressCost);
            totalSumProgressCount = totalSumProgressCount.add(sumProgressCount);
            totalSumProgressCost = totalSumProgressCost.add(sumProgressCost);
            totalRemindProgressCount = totalRemindProgressCount.add(remindProgressCount);
            totalRemindProgressCost = totalRemindProgressCost.add(remindProgressCost);
            if (processItemCostDetailDTO.isFirst()) {
                firstRowCnt = firstRowCnt + 1;
            }
            ProcessItemCostDetailDTO savedProcessItemCostDetailDTO = new ProcessItemCostDetailDTO(processItemCostDetailDTO.getId()
                    , processItemCostDetailDTO.getCode()
                    , processItemCostDetailDTO.getName()
                    , count
                    , processItemCostDetailDTO.getUnit()
                    , processItemCostDetailDTO.getUnitPrice()
                    , cost
                    , processItemCostDetailDTO.isFirst()
                    , processItemCostDetailDTO.getJobSheetProgressCount()
                    , processItemCostDetailDTO.getJobSheetProgressAmount()
                    , paidProgressCount
                    , paidCost
                    , progressCount
                    , progressCost
                    , sumProgressCount
                    , sumProgressCost
                    , remindProgressCount
                    , remindProgressCost);
            savedProcessItemCostDetailLists.add(savedProcessItemCostDetailDTO);
        }
        if (firstRowCnt > 0) {
            complexUnitPrice = totalCost.divide(new BigDecimal(firstRowCnt)).setScale(0, RoundingMode.HALF_UP).longValue();
            resultText = Utils.num2String(complexUnitPrice) + " = " + Utils.num2String(totalCost.longValue()) + " / " + firstRowCnt.toString();
        }

        totalJobSheetProgressCount = new BigDecimal(totalJobSheetProgressAmount).divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalPaidProgressCount = totalPaidCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalProgressCount = totalProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalSumProgressCount = totalSumProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        totalRemindProgressCount = totalRemindProgressCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        model.addAttribute("processItemId", processItemId);
        model.addAttribute("list", savedProcessItemCostDetailLists);
        model.addAttribute("complexUnitPrice", complexUnitPrice);
        model.addAttribute("resultText", resultText);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalCost", totalCost);     // 공사비 총합
        model.addAttribute("totalJobSheetProgressCount", totalJobSheetProgressCount);     // 실적 수량 총합
        model.addAttribute("totalJobSheetProgressAmount", totalJobSheetProgressAmount);     // 실적 금액 총합
        model.addAttribute("totalPaidProgressCount", totalPaidProgressCount);     // 전회기성 수량 총합
        model.addAttribute("totalPaidCost", totalPaidCost);     // 전회기성 금액 총합
        model.addAttribute("totalProgressCount", totalProgressCount);     // 금회기성 수량 총합
        model.addAttribute("totalProgressCost", totalProgressCost);     // 금회기성 금액 총합
        model.addAttribute("totalSumProgressCount", totalSumProgressCount);     // 누적기성 수량 총합
        model.addAttribute("totalSumProgressCost", totalSumProgressCost);     // 누적기성 금액 총합
        model.addAttribute("totalRemindProgressCount", totalRemindProgressCount);     // 잔여기성 수량 총합
        model.addAttribute("totalRemindProgressCost", totalRemindProgressCost);     // 잔여기성 금액 총합
        model.addAttribute("processItem", processItem);
    }

    @GetMapping("/gisungContractManagerList")
    public String gisungContractManagerList(@ModelAttribute SearchGisungContractManagerVO searchGisungContractManagerVO
            , @PageableDefault(size = defaultPageSize_documentList) Pageable pageable
            , Model model) {

        // document
        searchGisungContractManagerVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungContractManagerVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungContractManagerDTO> pageGisungContractManagerDTOs = gisungContractManagerService.findGisungContractManagerDTOs(searchGisungContractManagerVO, pageable);

        setPagingConfig(model, pageGisungContractManagerDTOs);

        model.addAttribute("searchGisungReportVO", searchGisungContractManagerVO);
        model.addAttribute("list", pageGisungContractManagerDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "gisung/gisungContractManagerList/page";
    }

    @GetMapping("/gisungContractManagerListCardBody")
    public String gisungContractManagerListCardBody(@ModelAttribute SearchGisungContractManagerVO searchGisungContractManagerVO
            , @PageableDefault(size = defaultPageSize_documentList) Pageable pageable
            , Model model) {
        // gisungReport
        searchGisungContractManagerVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungContractManagerVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungContractManagerDTO> pageGisungContractManagerDTOs = gisungContractManagerService.findGisungContractManagerDTOs(searchGisungContractManagerVO, pageable);

        setPagingConfig(model, pageGisungContractManagerDTOs);

        model.addAttribute("searchGisungContractManagerVO", searchGisungContractManagerVO);
        model.addAttribute("list", pageGisungContractManagerDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "gisung/gisungContractManagerList/cardBody";
    }

    @GetMapping("/addGisungContractManager")
    public String addGisungRContractManager(Model model) {
        GisungContractManager gisungContractManager = new GisungContractManager();

        model.addAttribute("gisungContractManager", gisungContractManager);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("IMAGE_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/addGisungContractManager";
    }

    @GetMapping("/updateGisungContractManager")
    public String updateGisungContractManager(long id, Model model) {
        GisungContractManager gisungContractManager = gisungContractManagerService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("gisungContractManager", gisungContractManager);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("IMAGE_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/addGisungContractManager";
    }

    @GetMapping("/gisungPaymentList")
    public String gisungPaymentList(@ModelAttribute SearchGisungPaymentVO searchGisungPaymentVO
            , @PageableDefault(size = defaultPageSize_gisungPaymentList) Pageable pageable
            , Model model) {
        // payment
        searchGisungPaymentVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungPaymentVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungPaymentDTO> pageGisungPaymentDTOs = gisungPaymentService.findGisungPaymentDTOs(searchGisungPaymentVO, pageable);

        setPagingConfig(model, pageGisungPaymentDTOs);

        model.addAttribute("searchGisungPaymentVO", searchGisungPaymentVO);
        model.addAttribute("list", pageGisungPaymentDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPaymentList/page";
    }

    @GetMapping("/gisungPaymentListCardBody")
    public String gisungPaymentListCardBody(@ModelAttribute SearchGisungPaymentVO searchGisungPaymentVO
            , @PageableDefault(size = defaultPageSize_gisungPaymentList) Pageable pageable
            , Model model) {
        // GisungPayment
        searchGisungPaymentVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchGisungPaymentVO.setProjectId(userInfo.getProjectId());
        PageDTO<GisungPaymentDTO> pageGisungPaymentDTOs = gisungPaymentService.findGisungPaymentDTOs(searchGisungPaymentVO, pageable);

        setPagingConfig(model, pageGisungPaymentDTOs);

        model.addAttribute("searchGisungPaymentVO", searchGisungPaymentVO);
        model.addAttribute("list", pageGisungPaymentDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "gisung/gisungPaymentList/cardBody";
    }

    @GetMapping("/addGisungPayment")
    public String addGisungPayment(Model model) {
        model.addAttribute("gisungPaymentFileExtension", String.join("||", configService.findFileExtension("ISSUE_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/addGisungPayment";
    }

    @GetMapping("/updateGisungPayment")
    public String updateGisungPayment(long id, Model model) {
        GisungPayment gisungPayment = gisungPaymentService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("gisungPayment", gisungPayment);
        model.addAttribute("gisungPaymentFileExtension", String.join("||", configService.findFileExtension("ISSUE_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/updateGisungPayment";
    }

    @GetMapping("/addListExcelUpload")
    public String addListExcelUpload(long id, Model model) {

        model.addAttribute("gisungId", id);
        model.addAttribute("gisungFileExtension", String.join("||", configService.findFileExtension("EXCEL_FILE_EXT", true, userInfo.getProjectId())));
        return "gisung/modal/listExcelUpload";
    }

    @GetMapping("/gisungCompareList")
    public String gisungCompareList(long id, String searchCompareResult, Model model) {

        getGisungProcessItemList(id, searchCompareResult, model);
        return "gisung/modal/compareList";
    }

    @GetMapping("/compareCostDetail/{gisungProcessItemId}")
    public String compareCostDetail(@PathVariable Long gisungProcessItemId, Model model) {
        getGisungProcessItemCostDetail(gisungProcessItemId, model);
        return "gisung/modal/compareCostDetail";
    }

    @GetMapping("/gisungCodeList")
    public String gisungCodeList(long id, String code, Model model) {

        long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();
        List<ProcessItemCostDetailDTO> processItemCostDetails = gisungService.findCostDetailByCode(processId, code, id);
        List<ProcessItemCostDetailDTO> savedProcessItemCostDetailLists = new ArrayList<>();
        BigDecimal sumTotalCount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalUnitPrice = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalPaidProgressCount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalPaidProgressCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalProgressCount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalProgressCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalSumProgressCount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalSumProgressCost = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalRemindProgressCount = new BigDecimal(BigInteger.ZERO);
        BigDecimal sumTotalRemindProgressCost = new BigDecimal(BigInteger.ZERO);
        for (ProcessItemCostDetailDTO processItemCostDetailDTO : processItemCostDetails) {
            BigDecimal count = processItemCostDetailDTO.getCount();
            BigDecimal cost = processItemCostDetailDTO.getCost();
            BigDecimal unitPrice = processItemCostDetailDTO.getUnitPrice();
            BigDecimal progressCount = processItemCostDetailDTO.getProgressCount();
            BigDecimal progressCost = processItemCostDetailDTO.getProgressCost();
            progressCost = progressCount.multiply(unitPrice);
            BigDecimal paidProgressCount = processItemCostDetailDTO.getPaidProgressCount();
            BigDecimal paidCost = processItemCostDetailDTO.getPaidCost();
            BigDecimal sumProgressCount = paidProgressCount.add(progressCount);
            BigDecimal sumProgressCost = paidCost.add(progressCost);
            BigDecimal remindProgressCount = count.subtract(sumProgressCount);
            BigDecimal remindProgressCost = cost.subtract(sumProgressCost);
            sumTotalCount = sumTotalCount.add(count);
            sumTotalCost = sumTotalCost.add(cost);
            sumTotalUnitPrice = sumTotalUnitPrice.add(unitPrice);
            sumTotalPaidProgressCount = sumTotalPaidProgressCount.add(paidProgressCount);
            sumTotalPaidProgressCost = sumTotalPaidProgressCost.add(paidCost);
            sumTotalProgressCount = sumTotalProgressCount.add(progressCount);
            sumTotalProgressCost = sumTotalProgressCost.add(progressCost);
            sumTotalSumProgressCount = sumTotalSumProgressCount.add(sumProgressCount);
            sumTotalSumProgressCost = sumTotalSumProgressCost.add(sumProgressCost);
            sumTotalRemindProgressCount = sumTotalRemindProgressCount.add(remindProgressCount);
            sumTotalRemindProgressCost = sumTotalRemindProgressCost.add(remindProgressCost);

            ProcessItemCostDetailDTO savedProcessItemCostDetailDTO = new ProcessItemCostDetailDTO(processItemCostDetailDTO.getId()
                    , processItemCostDetailDTO.getTaskName()
                    , processItemCostDetailDTO.getPhasingCode()
                    , processItemCostDetailDTO.getCode()
                    , processItemCostDetailDTO.getName()
                    , count
                    , processItemCostDetailDTO.getUnit()
                    , processItemCostDetailDTO.getUnitPrice()
                    , cost
                    , processItemCostDetailDTO.isFirst()
                    , paidProgressCount
                    , paidCost
                    , progressCount
                    , progressCost
                    , sumProgressCount
                    , sumProgressCost
                    , remindProgressCount
                    , remindProgressCost
                    , processItemCostDetailDTO.getProcessItemId());
            savedProcessItemCostDetailLists.add(savedProcessItemCostDetailDTO);
        }
        model.addAttribute("list", savedProcessItemCostDetailLists);
        model.addAttribute("sumTotalCount", sumTotalCount);
        model.addAttribute("sumTotalCost", sumTotalCost);
        model.addAttribute("sumTotalUnitPrice", sumTotalUnitPrice);
        model.addAttribute("sumTotalPaidProgressCount", sumTotalPaidProgressCount);
        model.addAttribute("sumTotalPaidProgressCost", sumTotalPaidProgressCost);
        model.addAttribute("sumTotalProgressCount", sumTotalProgressCount);
        model.addAttribute("sumTotalProgressCost", sumTotalProgressCost);
        model.addAttribute("sumTotalSumProgressCount", sumTotalSumProgressCount);
        model.addAttribute("sumTotalSumProgressCost", sumTotalSumProgressCost);
        model.addAttribute("sumTotalRemindProgressCount", sumTotalRemindProgressCount);
        model.addAttribute("sumTotalRemindProgressCost", sumTotalRemindProgressCost);
        model.addAttribute("gisungId", id);
        return "gisung/modal/gcodeList";
    }

    @GetMapping("/processItemSearchList")
    public String processItemSearchList(long id, String code, Model model) {
        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findAllGisungIdByGisungProcessItemCostCompareResult(id);

        model.addAttribute("search_gcode", code);
        model.addAttribute("codeList", gisungProcessItemCostDetailDTOs);
        return "gisung/modal/processItemSearchList";
    }
}
