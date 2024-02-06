package com.devo.bim.controller.view;

import com.devo.bim.component.Utils;
import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.vo.*;
import com.devo.bim.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_PROJECT")
public class AdminController extends AbstractController {

    private final CompanyRoleService companyRoleService;
    private final WorkService workService;
    private final JobSheetTemplateService jobSheetTemplateService;
    private final ProjectService projectService;
    private final AccountService accountService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final ExcelFormatterService excelFormatterService;
    private final ConfigService configService;
    private final ConfigCustomService configCustomService;
    private final AccessLogService accessLogService;
    private final WorkAmountService workAmountService;
    private final WorkPlanService workPlanService;

    @GetMapping("/calendar")
    public String calendar(Model model) {

        model.addAttribute("scheduleTypes", configService.findScheduleConfigs());
        return "admin/calendar/page";
    }

    @GetMapping("/codeCustom")
    public String codeCustom(Model model) {

        List<ConfigTreeDTO> configTreeDTOs = configService.findConfigTree();
        model.addAttribute("configTreeDTOs", configTreeDTOs);

        if (configTreeDTOs.get(0).getConfigs().size() > 0) {
            SearchConfigCustomVO searchConfigCustomVO = new SearchConfigCustomVO();
            searchConfigCustomVO.setConfigId(configTreeDTOs.get(0).getConfigs().get(0).getId());
            setConfigCustomDTOSearchCondition(searchConfigCustomVO);

            ConfigCustomDTO configCustom = configCustomService.findConfigCustom(searchConfigCustomVO);
            model.addAttribute("configCustom", configCustom);
        } else {
            model.addAttribute("configCustom", null);
        }
        return "admin/codeCustom/page";
    }

    @GetMapping("/codeCustomCardBody")
    public String codeCustomCardBody(Model model, SearchConfigCustomVO searchConfigCustomVO) {
        setConfigCustomDTOSearchCondition(searchConfigCustomVO);

        ConfigCustomDTO configCustom = configCustomService.findConfigCustom(searchConfigCustomVO);
        model.addAttribute("configCustom", configCustom);
        return "admin/codeCustom/cardBody";
    }

    private void setConfigCustomDTOSearchCondition(SearchConfigCustomVO searchConfigCustomVO) {
        searchConfigCustomVO.setProjectId(userInfo.getProjectId());
        searchConfigCustomVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    @GetMapping("/codeRole")
    public String codeRole() {
        return "admin/codeRole/page";
    }

    @GetMapping("/codeWork")
    public String codeWork() {
        return "admin/codeWork/page";
    }

    @GetMapping({"/companyList", "/companyListCardBody"})
    public String companyList(@ModelAttribute SearchCompanyVO searchCompanyVO
            , @PageableDefault(size = defaultPageSize_companyList) Pageable pageable
            , HttpServletRequest request
            , Model model) {

        searchCompanyVO.setProjectId(userInfo.getProjectId());
        PageDTO<CompanyDTO> companyDTOs = companyService.findCompanyDTOs(searchCompanyVO, pageable);

        setPagingConfig(model, companyDTOs);

        model.addAttribute("works", workService.findAll());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());

        if ("companyList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "admin/companyList/page";
        return "admin/companyList/cardBody";
    }

    @GetMapping({"/companyRoleList", "/companyRoleListCardBody"})
    public String companyRoleList(@ModelAttribute SearchCompanyRoleVO searchCompanyRoleVO
            , HttpServletRequest request
            , Model model) {

        procCompanyRoleList(searchCompanyRoleVO, model);

        if ("companyRoleList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "admin/companyRoleList/page";
        return "admin/companyRoleList/cardBody";
    }

    private void procCompanyRoleList(SearchCompanyRoleVO searchCompanyRoleVO, Model model) {
        setCompanyRoleDTOSearchCondition(searchCompanyRoleVO);

        List<CompanyRoleDTO> companyRoleDTOs = companyRoleService.findCompanyRoleDTOs(searchCompanyRoleVO);

        model.addAttribute("companyRoleDTOs", companyRoleDTOs);
        model.addAttribute("minSortNo", getMinSortNo(companyRoleDTOs));
        model.addAttribute("maxSortNo", getMaxSortNo(companyRoleDTOs));
    }

    private int getMaxSortNo(List<CompanyRoleDTO> companyRoleDTOs) {
        return companyRoleDTOs.stream()
                .max(Comparator.comparing(CompanyRoleDTO::getSortNo))
                .orElseGet(() -> {
                    CompanyRoleDTO companyRoleDTO = new CompanyRoleDTO(1);
                    return companyRoleDTO;
                }).getSortNo();
    }

    private int getMinSortNo(List<CompanyRoleDTO> companyRoleDTOs) {
        return companyRoleDTOs.stream()
                .min(Comparator.comparing(CompanyRoleDTO::getSortNo))
                .orElseGet(() -> {
                    CompanyRoleDTO companyRoleDTO = new CompanyRoleDTO(1);
                    return companyRoleDTO;
                }).getSortNo();
    }

    private void setCompanyRoleDTOSearchCondition(SearchCompanyRoleVO searchCompanyRoleVO) {
        searchCompanyRoleVO.setProjectId(userInfo.getProjectId());
        searchCompanyRoleVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    @GetMapping("/companyUpdate")
    public String companyUpdate(@RequestParam(required = true, defaultValue = "0") long companyId
            , @ModelAttribute SearchCompanyVO searchCompanyVO
            , @PageableDefault(size = defaultPageSize_companyList) Pageable pageable
            , Model model) {

        Company companyItem = companyService.findById(companyId);

        if (companyItem.getId() == 0) new NotFoundException(proc.translate("system.admin_controller.not_found_company"));

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);

        SearchCompanyRoleVO searchCompanyRoleVO = new SearchCompanyRoleVO();
        setCompanyRoleDTOSearchCondition(searchCompanyRoleVO);

        List<CompanyRoleDTO> companyRoleDTOs = companyRoleService.findCompanyRoleDTOs(searchCompanyRoleVO);

        model.addAttribute("companyItem", companyItem);
        model.addAttribute("companyRoles", companyRoleDTOs);
        model.addAttribute("works", workService.findUseWorkAll());
        model.addAttribute("companyUserCnt", accountService.findUserCntByCompanyId(companyItem.getId()));
        model.addAttribute("isTotalWorksSelected", isTotalWorksSelected(companyItem));
        model.addAttribute("telNoPattern", configService.findConfigForCache("LOCALE","FORMAT_PHONE", userInfo.getProjectId()));
        model.addAttribute("telNoPlaceholder", configService.findConfigForCache("LOCALE","PLACEHOLDER_PHONE", userInfo.getProjectId()));
        model.addAttribute("pageable", pageable);
        return "admin/companyUpdate/page";
    }

    private boolean isTotalWorksSelected(Company companyItem) {
        return workService.findUseWorkAll().size() == companyItem.getWorks().size();
    }

    @GetMapping("/companyAdd")
    public String companyAdd(Model model) {
        SearchCompanyRoleVO searchCompanyRoleVO = new SearchCompanyRoleVO();
        setCompanyRoleDTOSearchCondition(searchCompanyRoleVO);

        List<CompanyRoleDTO> companyRoleDTOs = companyRoleService.findCompanyRoleDTOs(searchCompanyRoleVO);

        model.addAttribute("companyVO", new CompanyVO());
        model.addAttribute("companyRoles", companyRoleDTOs);
        model.addAttribute("works", workService.findUseWorkAll());
        model.addAttribute("telNoPattern", configService.findConfigForCache("LOCALE","FORMAT_PHONE", userInfo.getProjectId()));
        model.addAttribute("telNoPlaceholder", configService.findConfigForCache("LOCALE","PLACEHOLDER_PHONE", userInfo.getProjectId()));

        return "admin/companyAdd/page";
    }

    @GetMapping("/jobSheetTempList")
    public String jobSheetTempList(SearchJobSheetTemplateVO searchJobSheetTemplateVO, Model model) {
        procJobSheetTemplateList(searchJobSheetTemplateVO, model);
        return "admin/jobSheetTempList/page";
    }

    @PostMapping("/jobSheetTempListCardBody")
    public String jobSheetTempListCardBody(@RequestBody SearchJobSheetTemplateVO searchJobSheetTemplateVO, Model model) {
        procJobSheetTemplateList(searchJobSheetTemplateVO, model);
        return "admin/jobSheetTempList/cardBody";
    }

    private void procJobSheetTemplateList(SearchJobSheetTemplateVO searchJobSheetTemplateVO, Model model) {
        setSearchJobSheetTemplateVO(searchJobSheetTemplateVO);

        List<JobSheetTemplateDTO> jobSheetTemplateDTOs = jobSheetTemplateService.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);
        model.addAttribute("jobSheetTemplateDTOs", jobSheetTemplateDTOs);
        model.addAttribute("sortProp", searchJobSheetTemplateVO.getSortProp());

    }

    private void setSearchJobSheetTemplateVO(SearchJobSheetTemplateVO searchJobSheetTemplateVO) {
        searchJobSheetTemplateVO.setProjectId(userInfo.getProjectId());
    }


    @GetMapping("/jobSheetTempAdd")
    public String jobSheetTempAdd() {
        return "admin/jobSheetTempAdd/page";
    }

    @GetMapping("/jobSheetTempUpdate")
    public String jobSheetTempUpdate(@RequestParam long id, Model model) {
        JobSheetTemplate jobSheetTemplate = jobSheetTemplateService.findById(id);
        if (jobSheetTemplate.getProjectId() != userInfo.getProjectId()) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }
        model.addAttribute("jobSheetTemplate", jobSheetTemplateService.findById(id));

        return "admin/jobSheetTempUpdate/page";
    }

    @GetMapping("/project")
    public String project(Model model) {
        Project project = projectService.findMyProject();
        PayHistory payHistory = project
                .getPayHistories()
                .stream()
                .sorted(Comparator.comparingLong(PayHistory::getId).reversed())
                .findFirst()
                .orElseGet(PayHistory::new);

        model.addAttribute("project", project);
        model.addAttribute("projectImages", project.getProjectImages());
        model.addAttribute("payHistory", payHistory);

        return "admin/project/page";
    }

    @GetMapping("/projectImageArea")
    public String projectImageArea(Model model) {
        model.addAttribute("projectImages", projectService.findMyProject().getProjectImages());

        return "admin/project/imageArea";
    }

    @GetMapping("/statisticsLogin")
    public String statisticsLogin(@RequestParam(defaultValue = "") String startDate
            , @RequestParam(defaultValue = "") String endDate
            , Model model) {

        if (startDate.isEmpty()) startDate = Utils.todayString();
        if (endDate.isEmpty()) endDate = Utils.todayString();

        model.addAttribute("statistics", accessLogService.getStatisticsLogin(startDate, endDate));

        return "admin/statisticsLogIn/page";
    }

    @GetMapping("/statisticsMenu")
    public String statisticsMenu(@RequestParam(defaultValue = "") String startDate
            , @RequestParam(defaultValue = "") String endDate
            , Model model) {

        if (startDate.isEmpty()) startDate = Utils.todayString();
        if (endDate.isEmpty()) endDate = Utils.todayString();

        model.addAttribute("statistics", accessLogService.getStatisticsMenu(startDate, endDate));

        return "admin/statisticsMenu/page";
    }

    @GetMapping("/statisticsUser")
    public String statisticsUser(@ModelAttribute SearchStatisticsUserVO searchStatisticsUserVO
            , @PageableDefault(size = defaultPageSize_statisticsUserList) Pageable pageable
            , Model model) {

        PageDTO<StatisticsUserDTO> statisticsUserDTOs = accessLogService.getStatisticsUser(searchStatisticsUserVO, pageable);

        setPagingConfig(model, statisticsUserDTOs);

        model.addAttribute("searchVO", searchStatisticsUserVO);
        model.addAttribute("workNames", workService.findByLanguageCode());

        return "admin/statisticsUser/page";
    }

    @GetMapping("/statisticsUserList")
    public String statisticsUserList(@ModelAttribute SearchStatisticsUserVO searchStatisticsUserVO
            , @PageableDefault(size = defaultPageSize_statisticsUserList) Pageable pageable
            , Model model) {

        PageDTO<StatisticsUserDTO> statisticsUserDTOs = accessLogService.getStatisticsUser(searchStatisticsUserVO, pageable);
        setPagingConfig(model, statisticsUserDTOs);

        return "admin/statisticsUser/list";
    }

    @GetMapping("/statisticsUserListExcel")
    public void statisticsUserListExcel(@ModelAttribute SearchStatisticsUserVO searchStatisticsUserVO
            , HttpServletResponse response) throws Exception {

        String fileName = "StatisticsUserList";
        String sheetName = "Statistics User List";

        List<StatisticsUserDTO> statisticsUserDTOs = accessLogService.getStatisticsUserBySearchCondition(searchStatisticsUserVO);

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.STATISTICS_USER_LIST, fileName, sheetName, statisticsUserDTOs, response);
    }

    @GetMapping("/user")
    public String user(Model model) {
        setAttributePhoneNoPattern(model);
        return getUserPage(model, new Account());
    }

    @GetMapping("/user/{accountId}")
    public String user(@PathVariable long accountId, Model model) {
        setAttributePhoneNoPattern(model);
        return getUserPage(model, accountService.findById(accountId));
    }

    private String getUserPage(Model model, Account account) {
        model.addAttribute("user", account);
        model.addAttribute("companies", companyService.findByProjectId());
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("roleNames", roleService.getProductRoleNameList(account.isRoleSystemAdmin()));
        model.addAttribute("imgFileExtension", String.join("||", configService.findFileExtension("IMAGE_FILE_EXT", true, userInfo.getProjectId())));

        return "admin/user/page";
    }

    @GetMapping("/userList")
    public String userList(@ModelAttribute SearchAccountVO searchAccountVO
            , @PageableDefault(size = defaultPageSize_adminUserList) Pageable pageable
            , Model model) {

        PageDTO<AccountDTO> pageAccountDTOs = accountService.findAccountDTOsByProjectId(searchAccountVO, pageable);

        setPagingConfig(model, pageAccountDTOs);

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        model.addAttribute("searchVO", searchAccountVO);
        model.addAttribute("companies", companyService.findByProjectId());
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("roleNames", roleService.findByLanguageCode());
        model.addAttribute("workJson" , jsonString);

        return "admin/userList/page";
    }

    @GetMapping("/userListExcel")
    public void userListExcel(@ModelAttribute SearchAccountVO searchAccountVO
            , HttpServletResponse response) throws Exception {

        String fileName = "UserList";
        String sheetName = "User List";

        List<AccountDTO> accountDTOs = accountService.findAccountDTOsBySearchCondition(searchAccountVO);

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.USER_LIST, fileName, sheetName, accountDTOs, response);
    }

    @GetMapping("/searchUserList")
    public String searchUserList(@ModelAttribute SearchAccountVO searchAccountVO
            , @PageableDefault(size = defaultPageSize_searchUserList) Pageable pageable
            , Model model) {

        PageDTO<AccountDTO> pageAccountDTOs = accountService.findAccountDTOsByProjectId(searchAccountVO, pageable);

        setPagingConfig(model, pageAccountDTOs);

        model.addAttribute("searchVO", searchAccountVO);

        return "admin/userList/list";
    }

    public String userUpdate(Model model) {
        return "admin/userUpdate/page";
    }

    @GetMapping("/workList")
    public String workList(SearchWorkVO searchWorkVO, Model model) {
        procWorkList(searchWorkVO, model);

        return "admin/workList/page";
    }

    @GetMapping("/workListCardBody")
    public String workListCardBody(SearchWorkVO searchWorkVO, Model model) {
        procWorkList(searchWorkVO, model);
        return "admin/workList/cardBody";
    }

    private void procWorkList(SearchWorkVO searchWorkVO, Model model) {
        setWorkDTOSearchCondition(searchWorkVO);

        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("minSortNo", getWorkListMinSortNo(workDTOs));
        model.addAttribute("maxSortNo", getWorkListMaxSortNo(workDTOs));
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    private int getWorkListMaxSortNo(List<WorkDTO> workDTOs) {
        return workDTOs.stream()
                .max(Comparator.comparing(WorkDTO::getSortNo))
                .orElseGet(() -> new WorkDTO(1))
                .getSortNo();
    }

    private int getWorkListMinSortNo(List<WorkDTO> workDTOs) {
        return workDTOs.stream()
                .min(Comparator.comparing(WorkDTO::getSortNo))
                .orElseGet(() -> new WorkDTO(1))
                .getSortNo();
    }

    /**
     * 공종 총액 관리
     * @param searchWorkAmountVO
     * @param model
     * @return
     */
    @GetMapping("/workAmountList")
    public String workAmountList(@ModelAttribute SearchWorkAmountVO searchWorkAmountVO
            , @PageableDefault(size = defaultPageSize_workAmountList) Pageable pageable
            , Model model) {
        // work
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        // document
        searchWorkAmountVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkAmountVO.setProjectId(userInfo.getProjectId());
        PageDTO<WorkAmountDTO> pageWorkAmountDTOs = workAmountService.findWorkAmountDTOs(searchWorkAmountVO, pageable);

        setPagingConfig(model, pageWorkAmountDTOs);

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("searchWorkAmountVO", searchWorkAmountVO);
        model.addAttribute("list", pageWorkAmountDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "admin/workAmountList/page";
    }

    @GetMapping("/workAmountCardBody")
    public String workAmountCardBody(@ModelAttribute SearchWorkAmountVO searchWorkAmountVO
            , @PageableDefault(size = defaultPageSize_workAmountList) Pageable pageable
            , Model model) {
        // document
        searchWorkAmountVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkAmountVO.setProjectId(userInfo.getProjectId());
        PageDTO<WorkAmountDTO> pageWorkAmountDTOs = workAmountService.findWorkAmountDTOs(searchWorkAmountVO, pageable);

        setPagingConfig(model, pageWorkAmountDTOs);

        model.addAttribute("searchWorkAmountVO", searchWorkAmountVO);
        model.addAttribute("list", pageWorkAmountDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "admin/workAmountList/cardBody";
    }

    /**
     * 공종 계획 관리
     * @param searchWorkPlanVO
     * @param model
     * @return
     */
    @GetMapping("/workPlanList")
    public String workAmountList(@ModelAttribute SearchWorkPlanVO searchWorkPlanVO
            , @PageableDefault(size = defaultPageSize_workPlanList) Pageable pageable
            , Model model) {
        // work
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        // document
        searchWorkPlanVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkPlanVO.setProjectId(userInfo.getProjectId());
        PageDTO<WorkPlanDTO> pageWorkPlanDTOs = workPlanService.findWorkPlanDTOs(searchWorkPlanVO, pageable);

        setPagingConfig(model, pageWorkPlanDTOs);

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("searchWorkPlanVO", searchWorkPlanVO);
        model.addAttribute("list", pageWorkPlanDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "admin/workPlanList/page";
    }

    @GetMapping("/workPlanCardBody")
    public String workPlanCardBody(@ModelAttribute SearchWorkPlanVO searchWorkPlanVO
            , @PageableDefault(size = defaultPageSize_workPlanList) Pageable pageable
            , Model model) {
        // document
        searchWorkPlanVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkPlanVO.setProjectId(userInfo.getProjectId());
        PageDTO<WorkPlanDTO> pageWorkPlanDTOs = workPlanService.findWorkPlanDTOs(searchWorkPlanVO, pageable);

        setPagingConfig(model, pageWorkPlanDTOs);

        model.addAttribute("searchWorkPlanVO", searchWorkPlanVO);
        model.addAttribute("list", pageWorkPlanDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());

        return "admin/workPlanList/cardBody";
    }
}
