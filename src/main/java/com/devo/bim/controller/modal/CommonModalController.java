package com.devo.bim.controller.modal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/commonModal")
@RequiredArgsConstructor
public class CommonModalController extends AbstractController {

    private final SearchUserService searchUserService;
    private final FileDownloadService fileDownloadService;
    private final AccountService accountService;
    private final JobSheetService jobSheetService;
    private final JobSheetTemplateService jobSheetTemplateService;
    private final ProjectService projectService;
    private final ProcessService processService;
    private final ProcessCostService processCostService;
    private final ExcelFormatterService excelFormatterService;

    @GetMapping("/fileDownload")
    public String fileDownload(@RequestParam(required = true, defaultValue = "0") long id
            , @RequestParam(required = true, defaultValue = "") String fileDownloadUIType
            , Model model) {

        FileDownloadInfoDTO fileDownloadInfoDTO = fileDownloadService.findByIdAndFileDownloadUIType(id, getFileDownloadUITypeEnum(fileDownloadUIType));
        boolean haveRightFileDownload = fileDownloadService.checkRightFileDownload(fileDownloadInfoDTO);

        model.addAttribute("fileDownloadInfoDTO", fileDownloadInfoDTO);
        model.addAttribute("haveRightFileDownload", haveRightFileDownload);
        return "common/modal/fileDownload";
    }

    @GetMapping("/processTemplatefileDownload")
    public String processTemplatefileDownload(Model model) {

        FileDownloadInfoDTO fileDownloadInfoDTO = new FileDownloadInfoDTO(0, 0
                , FileDownloadUIType.PROCESS_TEMPLATE, ""
                , "process_template.xlsx"
                , "/dist/file/process_template.xlsx"
                , new BigDecimal("0.00"), 5);

        boolean haveRightFileDownload = fileDownloadService.checkRightFileDownload(fileDownloadInfoDTO);

        model.addAttribute("fileDownloadInfoDTO", fileDownloadInfoDTO);
        model.addAttribute("haveRightFileDownload", haveRightFileDownload);
        return "common/modal/fileDownload";
    }

    private FileDownloadUIType getFileDownloadUITypeEnum(String fileDownloadUIType) {
        return Utils.getFileDownloadUITypeEnum(fileDownloadUIType);
    }

    @GetMapping("/searchSingleUser")
    public String searchSingleUser(@RequestParam(required = true, defaultValue = "") String formElementIdForUserId
            , @RequestParam(required = true, defaultValue = "") String formElementIdForUserName
            , @RequestParam(required = true, defaultValue = "") String formElementIdForModal
            , @RequestParam(required = true, defaultValue = "N") String searchUserFilter
            , Model model) {

        boolean checkedSearchCondition = false;
        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserDTOs();

        if("Y".equalsIgnoreCase(searchUserFilter)) {
            checkedSearchCondition = true;
            doFilterWithLogInUserRole(searchUserDTOs);
        }
        else setSearchResultAll(searchUserDTOs);


        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("formElementIdForUserId", formElementIdForUserId);
        model.addAttribute("formElementIdForUserName", formElementIdForUserName);
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("checkedSearchCondition", checkedSearchCondition);
        return "common/modal/searchSingleUser";
    }

    @GetMapping("/searchSingleUserText")
    public String searchSingleUser(@RequestParam(required = true, defaultValue = "") String formElementIdForUserId
            , @RequestParam(required = true, defaultValue = "") String formElementIdForUserName
            , @RequestParam(required = true, defaultValue = "") String formElementIdForModal
            , @RequestParam(required = true, defaultValue = "N") String searchUserFilter
            , @RequestParam(required = true, defaultValue = "") String searchText
            , Model model) {

        boolean checkedSearchCondition = false;
        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserTextDTOs(searchText);

        if("Y".equalsIgnoreCase(searchUserFilter)) {
            checkedSearchCondition = true;
            doFilterWithLogInUserRole(searchUserDTOs);
        }
        else setSearchResultAll(searchUserDTOs);


        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("formElementIdForUserId", formElementIdForUserId);
        model.addAttribute("formElementIdForUserName", formElementIdForUserName);
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("checkedSearchCondition", checkedSearchCondition);
        model.addAttribute("searchText", searchText);
        return "common/modal/searchSingleUser";
    }

    private void setSearchResultAll(List<SearchUserDTO> searchUserDTOs) {
        searchUserDTOs.forEach(t -> {
            t.setSearchResulTrue();
        });
    }

    private void doFilterWithLogInUserRole(List<SearchUserDTO> searchUserDTOs) {
        // 공통 - 동일 공종 인력만 결과
        searchUserDTOs.forEach(t -> {
            t.setSearchResultWithWork(accountService.getUserWorksIds(userInfo.getId()));
        });

        // 총 프로젝트 책임자는 모두 조회 가능
        if (userInfo.isRoleAdminProject()) setSearchResultAll(searchUserDTOs);

        // 공종 관리자의 경우 모든 공종관리자와 프로젝트 책임자, 공정, 내역 관리자도 포함
        if (userInfo.isRoleAdminWork()) {
            searchUserDTOs.forEach(t -> {
                t.setSearchResultWithRoles("ROLE_ADMIN_PROJECT,ROLE_ADMIN_ESTIMATE,ROLE_ADMIN_PROCESS,ROLE_ADMIN_WORK");
            });
        }
        // 공정관리자와 내역관리자는 결제할 일이 없음 그래도 모르니 총 책임자만 포함
        if (userInfo.isRoleAdminEstimate() || userInfo.isRoleAdminProcess()) {
            searchUserDTOs.forEach(t -> {
                t.setSearchResultWithRoles("ROLE_ADMIN_PROJECT");
            });
        }
        // 일반 사용자는 총책임자와 공종 책임자들만 포함.
        if (userInfo.isRoleUserNormal()) {
            searchUserDTOs.forEach(t -> {
                t.setSearchResultWithRoles("ROLE_ADMIN_PROJECT,ROLE_ADMIN_WORK");
            });
        }
    }

    @GetMapping("/searchMultiUserText")
    public String searchMultiUser( @RequestParam(required = true, defaultValue = "") String formElementIdForUserIds
            , @RequestParam(required = true, defaultValue = "") String formElementIdForModal
            , @RequestParam(required = true, defaultValue = "N") String searchUserFilter
            , @RequestParam(required = true, defaultValue = "N") String referenceSelected
            , @RequestParam(required = true, defaultValue = "") String searchText
            , @RequestParam(required = false, defaultValue = "0") long jobSheetId
            , @RequestParam(required = false, defaultValue = "") String fixId
            , Model model) {

        boolean checkedSearchCondition = false;
        List<Long> fixedIds = new ArrayList<>();
        if (fixId != null && !fixId.isEmpty()) {
            fixedIds = Arrays.asList(fixId.split(","))
                    .stream()
                    .map(s -> Long.parseLong(s.trim()))
                    .collect(Collectors.toList());
        }
        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserTextDTOs(searchText);

        if("Y".equalsIgnoreCase(searchUserFilter)) {
            checkedSearchCondition = true;
            doFilterWithLogInUserRole(searchUserDTOs);
        }
        else setSearchResultAll(searchUserDTOs);

        if("Y".equalsIgnoreCase(referenceSelected)) setSelectedForJobSheetReference(jobSheetId, searchUserDTOs, fixedIds);
        else setSelectedForLogInUserReference(searchUserDTOs, fixedIds);

        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("modalScopeId", "modalSearchJobSheetReferences");
        model.addAttribute("formElementIdForUserIds", formElementIdForUserIds);
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("checkedSearchCondition", checkedSearchCondition);
        model.addAttribute("searchText", searchText);
        model.addAttribute("fixId", fixId);
        return "common/modal/searchMultiUser";
    }

    @GetMapping("/searchMultiUser")
    public String searchMultiUser( @RequestParam(required = true, defaultValue = "") String formElementIdForUserIds
            , @RequestParam(required = true, defaultValue = "") String formElementIdForModal
            , @RequestParam(required = true, defaultValue = "N") String searchUserFilter
            , @RequestParam(required = true, defaultValue = "N") String referenceSelected
            , @RequestParam(required = false, defaultValue = "0") long jobSheetId
            , @RequestParam(required = false, defaultValue = "") String fixId
            , Model model) {

        boolean checkedSearchCondition = false;
        List<Long> fixedIds = new ArrayList<>();
        if (fixId != null && !fixId.isEmpty()) {
            fixedIds = Arrays.asList(fixId.split(","))
                    .stream()
                    .map(s -> Long.parseLong(s.trim()))
                    .collect(Collectors.toList());
        }
        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserDTOs();

        if("Y".equalsIgnoreCase(searchUserFilter)) {
            checkedSearchCondition = true;
            doFilterWithLogInUserRole(searchUserDTOs);
        }
        else setSearchResultAll(searchUserDTOs);

        if("Y".equalsIgnoreCase(referenceSelected)) setSelectedForJobSheetReference(jobSheetId, searchUserDTOs, fixedIds);
        else setSelectedForLogInUserReference(searchUserDTOs, fixedIds);

        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("modalScopeId", "modalSearchJobSheetReferences");
        model.addAttribute("formElementIdForUserIds", formElementIdForUserIds);
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("checkedSearchCondition", checkedSearchCondition);
        model.addAttribute("fixId", fixId);
        return "common/modal/searchMultiUser";
    }

    private void setSelectedForLogInUserReference(List<SearchUserDTO> searchUserDTOs, List<Long> fixedIds ) {

        if (!fixedIds.isEmpty() && fixedIds.size() > 0) {
            searchUserDTOs.forEach(t-> {
                t.setSelected(fixedIds);
                t.setFixed(fixedIds);
            });
        } else {
            List<Long> userIds = accountService.findById(userInfo.getId())
                    .getAccountReferences()
                    .stream()
                    .map(o -> o.getReferenceId())
                    .collect(Collectors.toList());

            userIds.add(userInfo.getId());
            fixedIds.add(userInfo.getId());

            searchUserDTOs.forEach(t -> {
                t.setSelected(userIds);
                t.setFixed(fixedIds);
            });
        }
    }

    private void setSelectedForJobSheetReference(long jobSheetId, List<SearchUserDTO> searchUserDTOs, List<Long> fixedIds ) {

        if(jobSheetId > 0) {

            JobSheet savedJobSheet = jobSheetService.findJobSheetReferencesById(jobSheetId);
            fixedIds.add(savedJobSheet.getWriteEmbedded().getWriter().getId());

            searchUserDTOs.forEach(t-> {
                t.setSelected(savedJobSheet.getJobSheetReferences()
                        .stream()
                        .map(o -> o.getReference().getId())
                        .collect(Collectors.toList()));

                t.setFixed(fixedIds);
            });
        }
    }

    @GetMapping("/zoomInImage")
    public String zoomInImage() {
        return "commonModal/zoomInImage";
    }

    @GetMapping("/mySnapShotShare")
    public String mySnapShotShare(MySnapShotShareVO mySnapShotShareVO, Model model) {
        Account account = accountService.findLoginAccount();
        model.addAttribute("mySnapShots", account.getMySnapShots());
        model.addAttribute("mySnapShotShareVO", mySnapShotShareVO);
        return "common/modal/mySnapShotShare";
    }

    private List<MySnapShot> getMySnapShot(String modelIds) {
        Account account = accountService.findLoginAccount();

        if("all".equals(modelIds)) return account.getMySnapShotsByIdDesc();
        return account.getMySnapShotsContainWithRenderedModelByIdAsc(modelIds);
    }

    @GetMapping("/editorPreview")
    public String editorPreview(Model model) {
        return "common/modal/editorPreview";
    }

    @GetMapping("/addJobFromSnapShot")
    public String addJobFromSnapShot(Model model,
    		@RequestParam(required = false, defaultValue = "") String phasingCodes) {
    	JobSheet jobSheet = new JobSheet();
    	Account account = accountService.findById(userInfo.getId());
    	if(phasingCodes.length()>0) {
    		phasingCodes = phasingCodes.replaceAll(" ", "");
    		model.addAttribute("taskList", processService.findTaskListByPhasingCodes(phasingCodes.split(",")));
    	}
    	model.addAttribute("phasingCodes", phasingCodes);
    	model.addAttribute("jobSheet", jobSheet);
    	model.addAttribute("snapShots", jobSheet.getJobSheetSnapShots());
    	model.addAttribute("jobSheetFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
    	model.addAttribute("user", account);
    	model.addAttribute("accountGrantor", accountService.getAccountGrantor());
    	model.addAttribute("accountReferences", accountService.getAccountReferences());


    	getJobSheetTemplateList(model);
    	getProjectName(model);
    	return "common/modal/addJobFromSnapShot";
    }

    private void getProjectName(Model model) {
        Project project = projectService.findById();

        model.addAttribute("projectName", project.getName());
    }

    private void getJobSheetTemplateList(Model model) {
        SearchJobSheetTemplateVO searchJobSheetTemplateVO = new SearchJobSheetTemplateVO();
        searchJobSheetTemplateVO.setProjectId(userInfo.getProjectId());
        searchJobSheetTemplateVO.setEnabled(true);
        List<JobSheetTemplateDTO> jobSheetTemplateDTOs = jobSheetTemplateService.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);

        model.addAttribute("jobSheetTemplateList", jobSheetTemplateDTOs);
    }

    @GetMapping("/selectModelCostDetailExcel/{processItemId}/{rowState}")
    public void selectModelCostDetailExcel(@PathVariable Long processItemId, @PathVariable String rowState
            , HttpServletResponse response) throws Exception {

        String fileName = "CostDetail";
        String sheetName = "CostDetail";

        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = processCostService.findProcessItemCostDetail(processItemId, rowState);
        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL, fileName, sheetName, processItemCostDetailDTOs, response);
    }

    @GetMapping("/selectModelWorkerDeviceDetailExcel/{processItemId}/{rowState}")
    public void selectModelWorkerDeviceDetailExcel(@PathVariable Long processItemId, @PathVariable String rowState
            , HttpServletResponse response) throws Exception {

        String fileName = "WorkerDeviceDetail";
        String sheetName = "WorkerDeviceDetail";

        List<SelectProgressConfig> selectProgressConfigs = processService.getProcessItemWorkerDeviceDetail(processItemId);
        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.PROCESS_WORKER_DEVICE_DETAIL, fileName, sheetName, selectProgressConfigs, response);
    }

    @GetMapping("/printJobSheet")
    public String printJobSheet(@RequestParam(required = true, defaultValue = "") String formElementIdForUserId
            , @RequestParam(required = true, defaultValue = "") String formElementIdForUserName
            , @RequestParam(required = true, defaultValue = "") String formElementIdForModal
            , @RequestParam(required = true, defaultValue = "N") String searchUserFilter
            , Model model) {

        boolean checkedSearchCondition = false;
        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserDTOs();

        if("Y".equalsIgnoreCase(searchUserFilter)) {
            checkedSearchCondition = true;
            doFilterWithLogInUserRole(searchUserDTOs);
        }
        else setSearchResultAll(searchUserDTOs);


        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("formElementIdForUserId", formElementIdForUserId);
        model.addAttribute("formElementIdForUserName", formElementIdForUserName);
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("checkedSearchCondition", checkedSearchCondition);
        return "common/modal/printJobSheet";
    }
}
