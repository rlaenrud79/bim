package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.CompanyDTO;
import com.devo.bim.model.entity.Company;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.*;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/adminApi")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_PROJECT")
public class AdminApiController extends AbstractController {

    private final CompanyRoleService companyRoleService;
    private final WorkService workService;
    private final WorkAmountService workAmountService;
    private final WorkPlanService workPlanService;
    private final JobSheetTemplateService jobSheetTemplateService;
    private final ProjectService projectService;
    private final CompanyService companyService;
    private final ExcelFormatterService excelFormatterService;
    private final ScheduleService scheduleService;
    private final AccountService accountService;
    private final GisungAggregationService gisungAggregationService;
    private final ConfigCustomService configCustomService;

    @PostMapping("/postCompanyRole")
    public JsonObject postCompanyRole(@RequestBody CompanyRoleVO companyRoleVO){
        return companyRoleService.postCompanyRoleAndCompanyRoleName(companyRoleVO);
    }

    @PostMapping("/putCompanyRole")
    public JsonObject putCompanyRole(@RequestBody CompanyRoleVO companyRoleVO) {
        return companyRoleService.putCompanyRoleAndCompanyRoleName(companyRoleVO);
    }

    @PostMapping("/putDisabledCompanyRole")
    public JsonObject putDisabledCompanyRole(@RequestParam(required = true, defaultValue = "0") long companyRoleId) {
        return companyRoleService.putEnabledCompanyRole(companyRoleId, "OFF");
    }

    @PostMapping("/putEnabledCompanyRole")
    public JsonObject putEnabledCompanyRole(@RequestParam(required = true, defaultValue = "0") long companyRoleId) {
        return companyRoleService.putEnabledCompanyRole(companyRoleId, "ON");
    }

    @PostMapping("/putCompanyRoleSortNoASC")
    public JsonObject putCompanyRoleSortNoASC(@RequestParam(required = true, defaultValue = "0") long companyRoleId) {
        return companyRoleService.putCompanyRoleSortNoASC(companyRoleId);
    }

    @PostMapping("/putCompanyRoleSortNoDESC")
    public JsonObject putCompanyRoleSortNoDESC(@RequestParam(required = true, defaultValue = "0") long companyRoleId) {
        return companyRoleService.putCompanyRoleSortNoDESC(companyRoleId);
    }

    @PostMapping("/postJobSheetTemplate")
    public JsonObject postJobSheetTemplate(@RequestBody JobSheetTemplateVO jobSheetTemplateVO) { return jobSheetTemplateService.postJobSheetTemplate(jobSheetTemplateVO); }

    @PostMapping("/putJobSheetTemplate")
    public JsonObject putJobSheetTemplate(@RequestBody JobSheetTemplateVO jobSheetTemplateVO) { return jobSheetTemplateService.putJobSheetTemplate(jobSheetTemplateVO); }

    @PostMapping("/postWork")
    public JsonObject postWork(@RequestBody WorkVO workVO) {
        return workService.postWorkAndWorkName(workVO);
    }

    @PostMapping("/putWork")
    public JsonObject putWork(@RequestBody WorkVO workVO) {
        return workService.putWorkAndWorkName(workVO);
    }

    @PostMapping("/postWorkAmount")
    public JsonObject postWorkAmount(@RequestBody WorkAmountVO workAmountVO) {
        return workAmountService.postWorkAmount(workAmountVO);
    }

    @PostMapping("/putWorkAmount")
    public JsonObject putWorkAmount(@RequestBody WorkAmountVO workAmountVO) {
        return workAmountService.putWorkAmount(workAmountVO);
    }

    @DeleteMapping("/deleteWorkAmount")
    public JsonObject deleteWorkAmount(long id) {
        return workAmountService.deleteWorkAmount(id);
    }

    @PostMapping("/postWorkPlan")
    public JsonObject postWorkPlan(@RequestBody WorkPlanVO workPlanVO) {
        return workPlanService.postWorkPlan(workPlanVO);
    }

    @PostMapping("/putWorkPlan")
    public JsonObject putWorkPlan(@RequestBody WorkPlanVO workPlanVO) {
        return workPlanService.putWorkPlan(workPlanVO);
    }

    @DeleteMapping("/deleteWorkPlan")
    public JsonObject deleteWorkPlan(long id) {
        return workPlanService.deleteWorkPlan(id);
    }

    @PostMapping("/putDisabledWork")
    public JsonObject putDisabledWork(@RequestParam(defaultValue = "0") long workId) {
        return workService.putWorkStatus(workId, WorkStatus.DEL);
    }

    @PostMapping("/putEnabledWork")
    public JsonObject putEnabledWork(@RequestParam(defaultValue = "0") long workId) {
        return workService.putWorkStatus(workId, WorkStatus.USE);
    }

    @PostMapping("/putWorkSortNoASC")
    public JsonObject putWorkSortNoASC(@RequestParam(defaultValue = "0") long workId) {
        return workService.putWorkSortNoASC(workId);
    }

    @PostMapping("/putWorkSortNoDESC")
    public JsonObject putWorkSortNoDESC(@RequestParam(defaultValue = "0") long workId) {
        return workService.putWorkSortNoDESC(workId);
    }

    @DeleteMapping("/projectImage/{projcetImageId}")
    public JsonObject deleteProjectImage(@PathVariable long projcetImageId) {
        return projectService.deleteProjectImage(projcetImageId);
    }

    @PostMapping("/payHistory")
    public JsonObject postPayHistory(BigDecimal totalPay, BigDecimal totalCost) {return projectService.postPayHistory(totalPay, totalCost);}

    @PutMapping("/project")
    public JsonObject project(@RequestBody ProjectVO projectVO) {return projectService.putProject(projectVO);}

    @PutMapping("/projectContents")
    public JsonObject putProjectContents(@RequestBody ProjectVO projectVO) {return projectService.putProjectContents(projectVO.getContents());}

    @GetMapping("/checkEmail/{email}")
    public JsonObject checkEmail(@PathVariable String email)
    {return accountService.checkEmail(email);}

    @PostMapping("/user")
    public JsonObject postUser(@RequestBody AccountVO accountVO) {
        return accountService.procPostUser(accountVO);
    }

    @PutMapping("/user")
    public JsonObject putUser(@RequestBody AccountVO accountVO) {
        return accountService.procPutUser(accountVO);
    }

    @PutMapping("/initPassword/{accountId}")
    public JsonObject initPassword(@PathVariable long accountId) {
        return accountService.initPassword(accountId);
    }

    @PostMapping("/postCompany")
    public JsonObject postCompany(@RequestBody CompanyVO companyVO){
        setCompanyDefaultValue(companyVO);

        Company company = new Company(companyVO);

        return companyService.postCompany(company);
    }

    @PostMapping("/putCompany")
    public JsonObject putCompany(@RequestBody CompanyVO companyVO){
        setCompanyDefaultValue(companyVO);

        Company company = new Company(companyVO);

        return companyService.putCompany(company);
    }

    private void setCompanyDefaultValue(@RequestBody CompanyVO companyVO) {
        companyVO.setProjectId(userInfo.getProjectId());
        companyVO.setUserId(userInfo.getId());
    }

    @GetMapping("/companyListExcel")
    public void issueListExcel(@ModelAttribute SearchCompanyVO searchCompanyVO
            , HttpServletResponse response) throws Exception {

        String fileName = "CompanyList";
        String sheetName = "Company List";
        searchCompanyVO.setProjectId(userInfo.getProjectId());
        List<CompanyDTO> companyDTOs = companyService.findCompanyDTOs(searchCompanyVO);

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.COMPANY_LIST, fileName, sheetName, companyDTOs, response);
    }

    @GetMapping("/getEvent/{yearMonth}")
    public JsonObject getEvent(@PathVariable String yearMonth){
        return scheduleService.getAdminEventByYearMonth(yearMonth);
    }

    @PostMapping("/postSchedule")
    public JsonObject postSchedule(@RequestBody ScheduleVO scheduleVO){

        setUserAndProject(scheduleVO);
        return scheduleService.postScheduleAtAdmin(scheduleVO);
    }

    @PostMapping("/putSchedule")
    public JsonObject putSchedule(@RequestBody ScheduleVO scheduleVO){

        setUserAndProject(scheduleVO);
        return scheduleService.putScheduleAtAdmin(scheduleVO);
    }

    @DeleteMapping("/deleteSchedule/{scheduleId}")
    public JsonObject deleteSchedule(@PathVariable long scheduleId) {
        return scheduleService.deleteScheduleAtAdmin(scheduleId);
    }

    private void setUserAndProject(ScheduleVO scheduleVO) {
        scheduleVO.setUserId(userInfo.getId());
        scheduleVO.setProjectId(userInfo.getProjectId());
    }

    @PostMapping("/configCustom")
    public JsonObject postConfigCustom(@RequestBody ConfigCustomVO configCustomVO) {
        return configCustomService.postConfigCustom(configCustomVO);
    }

    @PutMapping("/configCustom")
    public JsonObject putConfigCustom(@RequestBody ConfigCustomVO configCustomVO) {
        return configCustomService.putConfigCustom(configCustomVO);
    }

    @DeleteMapping("/configCustom")
    public JsonObject deleteConfigCustom(long id) {
        return configCustomService.deleteConfigCustom(id);
    }

    @PostMapping("/postGisungAggregation")
    public JsonObject postGisungAggregation(@RequestBody GisungAggregationVO gisungAggregationVO) {return gisungAggregationService.postGisungAggregation(gisungAggregationVO);}

    @PostMapping("/putGisungAggregation")
    public JsonObject putGisungAggregation(@RequestBody GisungAggregationVO gisungAggregationVO) {return gisungAggregationService.putGisungAggregation(gisungAggregationVO);}

    @DeleteMapping("/deleteCompany")
    public JsonObject deleteCompany(long id) {
        return companyService.deleteCompany(id);
    }
}
