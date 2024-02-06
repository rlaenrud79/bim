package com.devo.bim.service;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.GisungProcessItem;
import com.devo.bim.model.entity.GisungProcessItemCostDetail;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.SelectProgressConfig;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.devo.bim.repository.spring.GisungProcessItemRepository;
import com.devo.bim.repository.spring.ProcessItemCostDetailRepository;
import com.devo.bim.repository.spring.ProcessItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelFormatterService  extends AbstractService {
    private final ProcessItemRepository processItemRepository;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final ProcessCostService processCostService;
    private final GisungService gisungService;

    // flush 되기 전까지 메모리에 들고있는 행의 갯수
    private final int ROW_ACCESS_WINDOW_SIZE = 500;

    // region 이슈 리스트 타이틀 message properties 배열
    private final String[] titlePropsArrayForIssueList = {
              "co_work.issue_list.list_title_no"
            , "co_work.issue_list.list_title_subject"
            , "co_work.issue_list.list_title_writer_name"
            , "co_work.issue_list.list_title_writer_company"
            , "co_work.issue_list.list_title_writer_mobile"
            , "co_work.issue_list.list_title_request_date"
            , "co_work.issue_list.list_title_priority"
            , "co_work.issue_list.list_title_status"
            , "co_work.issue_list.list_title_status_update_date"
            , "co_work.issue_list.list_title_write_date"};

    private final String[] titlePropsArrayForCompanyList = {
              "admin.company_list.list_title_no"
            , "admin.company_list.list_title_company_name"
            , "admin.company_list.list_title_tel_no"
            , "admin.company_list.list_title_responsible_user"
            , "admin.company_list.list_title_responsible_tel_no"
            , "admin.company_list.list_title_company_role"
            , "admin.company_list.list_title_works"
            , "admin.company_list.list_title_is_display"
            , "admin.company_list.list_title_status"
            , "admin.company_list.list_title_last_modify_date"};    

    private final String[] titlePropsArrayForUserList = {
              "admin.user_list.page.no"
            , "admin.user_list.page.user_name"
            , "admin.user_list.page.email"
            , "admin.user_list.page.company_name"
            , "admin.user_list.page.mobile_no"
            , "admin.user_list.page.work"
            , "admin.user_list.page.enabled"            
            , "admin.user_list.page.last_modify_date"};

    private final String[] titlePropsArrayForStatisticsUserList = {
              "admin.statistics_user.page.no"
            , "admin.statistics_user.page.user_name"
            , "admin.statistics_user.page.email"
            , "admin.statistics_user.page.company_name"
            , "admin.statistics_user.page.rank"
            , "admin.statistics_user.page.work_names"
            , "admin.statistics_user.page.menu_name"
            , "admin.statistics_user.page.access_date"};

    private final String[] titlePropsArrayForProcessExcelAdded = {
            "Phasing Codes"
            , "Duration"
            , "Planned Start"
            , "Planned End"
            , "FS Codes"
            , "FS"
            , "FS Dur"
            , "Formula"
            , "Quantity"
            , "Unit"
            , "Object unit cost"
            , "Base cost"
    };

    private final String[] titlePropsArrayForPhasingCodeValidationResult = {
            "process.modal.result_code_validation.no"
            , "process.modal.result_code_validation.task_name"
            , "process.modal.result_code_validation.phasing_code"
            , "process.modal.result_code_validation.start_date"
            , "process.modal.result_code_validation.end_date"
            , "process.modal.result_code_validation.validate"
    };

    private final String[] titleProcessItemCost = {
            "cost.index.page.phasing_name"
            ,"cost.index.page.work"
            ,"cost.index.page.phasing_code"
            ,"cost.index.page.process_rate"
            ,"cost.index.page.working_duration"
            ,"cost.index.page.process_start_date"
            ,"cost.index.page.process_end_date"
            ,"cost.index.page.first_count"
            ,"cost.index.page.complex_unit_price_b"
            ,"cost.index.page.task"
            ,"cost.index.page.formula"
            ,"cost.index.page.first_count_value_a"
            ,"cost.index.page.first_count_unit"
            ,"cost.index.page.task_cost_ab"
            ,"cost.index.page.paid_cost"
    };

    private final String[] titleProcessItemCostDetailInProcessItemCost = {
            "cost.index.page.phasing_name"
            ,"cost.index.page.phasing_code"
            ,"cost.modal.cost_detail.code"
            ,"cost.modal.cost_detail.name"
            ,"cost.modal.cost_detail.first"
            ,"cost.modal.cost_detail.first_count_value"
            ,"cost.modal.cost_detail.first_count_unit"
            ,"cost.modal.cost_detail.first_count_unit_price"
    };

    private final String[] titleGisungProcessItemCostSum = {
            "cost.index.page.phasing_name"
            ,"cost.index.page.work"
            ,"cost.index.page.phasing_code"
            ,"cost.index.page.process_rate"
            ,"cost.index.page.working_duration"
            ,"cost.index.page.process_start_date"
            ,"cost.index.page.process_end_date"
            ,"cost.index.page.first_count"
            ,"cost.index.page.complex_unit_price_b"
            ,"cost.index.page.task"
            ,"cost.index.page.formula"
            ,"cost.index.page.first_count_value_a"
            ,"cost.index.page.first_count_unit"
            ,"cost.index.page.task_cost_ab"
            ,"cost.index.page.paid_cost"
            ,"gisung.index.page.job_sheet"
            ,"gisung.index.page.paid_cost"
            ,"gisung.index.page.progress_cost"
            ,"gisung.index.page.sum_progress_cost"
            ,"gisung.index.page.remind_progress_cost"
            ,"gisung.index.page.cost"
            ,"gisung.index.page.rate"
            ,"gisung.index.page.total_count"
            ,"gisung.index.detail.job_sheet_count"
            ,"gisung.index.detail.job_sheet_amount"
            ,"gisung.index.detail.paid_count"
            ,"gisung.index.detail.paid_cost"
            ,"gisung.index.detail.progress_count"
            ,"gisung.index.detail.progress_cost"
            //,"gisung.index.detail.sum_progress_count"
            //,"gisung.index.detail.sum_progress_cost"
            //,"gisung.index.detail.remind_progress_count"
            //,"gisung.index.detail.remind_progress_cost"
    };

    private final String[] titleGisungProcessItemCostDetailInProcessItemCostSum = {
            "cost.index.page.phasing_name"
            ,"cost.index.page.phasing_code"
            ,"cost.modal.cost_detail.code"
            ,"cost.modal.cost_detail.name"
            ,"cost.modal.cost_detail.first"
            ,"cost.modal.cost_detail.first_count_value"
            ,"cost.modal.cost_detail.first_count_unit"
            ,"cost.modal.cost_detail.first_count_unit_price"
            ,"gisung.index.page.cost"
            ,"gisung.index.detail.job_sheet_count"
            ,"gisung.index.detail.job_sheet_amount"
            ,"gisung.index.detail.paid_count"
            ,"gisung.index.detail.paid_cost"
            ,"gisung.index.detail.progress_count"
            ,"gisung.index.detail.progress_cost"
            //,"gisung.index.detail.sum_progress_count"
            //,"gisung.index.detail.sum_progress_cost"
            //,"gisung.index.detail.remind_progress_count"
            //,"gisung.index.detail.remind_progress_cost"
    };

    private final String[] titleGisungProcessItemCost = {
            "gisung.index.page.phasing_name"
            ,"gisung.index.page.phasing_code"
            ,"gisung.index.page.cost_detail"
            ,"gisung.index.page.total"
            ,"gisung.index.page.job_sheet"
            ,"gisung.index.page.paid_cost"
            ,"gisung.index.page.progress_cost"
            ,"gisung.index.page.sum_progress_cost"
            ,"gisung.index.page.remind_progress_cost"
            ,"gisung.index.page.cost"
            ,"gisung.index.page.rate"
            ,"gisung.index.page.total_count"
    };

    private final String[] titleGisungProcessItemCostDetailInGisungProcessItem = {
            "cost.index.page.phasing_name"
            ,"cost.index.page.phasing_code"
            ,"cost.modal.cost_detail.code"
            ,"cost.modal.cost_detail.name"
            ,"cost.modal.cost_detail.first"
            ,"cost.modal.cost_detail.first_count_value"
            ,"cost.modal.cost_detail.first_count_unit"
            ,"cost.modal.cost_detail.first_count_unit_price"
            ,"cost.modal.cost_detail.cost"
            ,"gisung.index.page.job_sheet"
            ,"gisung.index.page.paid_cost"
            ,"gisung.index.page.progress_cost"
            ,"gisung.index.page.sum_progress_cost"
            ,"gisung.index.page.remind_progress_cost"
            ,"cost.modal.cost_detail.count_title"
            ,"cost.modal.cost_detail.cost_title"
    };

    private final String[] titleProcessItemCostDetail = {
            "process.modal.cost_detail.detail_work"
            ,"process.modal.cost_detail.count"
            ,"process.modal.cost_detail.cost"
            ,"process.modal.cost_detail.code"
            ,"process.modal.cost_detail.name"
            ,"process.modal.cost_detail.first"
            ,"process.modal.cost_detail.first_count_value_a"
            ,"process.modal.cost_detail.first_count_unit"
            ,"process.modal.cost_detail.first_count_unit_price_b"
    };

    private final String[] titleProcessWorkerDeviceDetail = {
            "process.modal.worker_device.detail_work"
    };

    private final String[] titleGisungProcessItemCostGcode = {
            "process.modal.cost_detail.code"
            ,"process.modal.cost_detail.count"
            ,"process.modal.cost_detail.plan_count"
    };

    // endregion

    public <T> void ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType excelDownloadCaseType
            , String fileName
            , String sheetName
            , List<T> excelBodyDTOS
            , HttpServletResponse response) throws IOException {

        OutputStream outputStream = response.getOutputStream();

        try {
            makeResponseHeader(response, fileName);
            SXSSFWorkbook sxssfWorkbook = makeExcel(excelDownloadCaseType, sheetName, excelBodyDTOS);
            closeExcelObject(response, outputStream, sxssfWorkbook);
        } catch (Exception e) {
            log.error("error", e); // 2) 로그를 작성
        } finally {
            closeOutPutStream(outputStream);
        }
    }

    @Nullable
    private String[] getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType excelDownloadCaseType) {
        if(excelDownloadCaseType == ExcelDownloadCaseType.ISSUE_LIST) return titlePropsArrayForIssueList;
        if(excelDownloadCaseType == ExcelDownloadCaseType.COMPANY_LIST) return titlePropsArrayForCompanyList;
        if(excelDownloadCaseType == ExcelDownloadCaseType.USER_LIST) return titlePropsArrayForUserList;
        if(excelDownloadCaseType == ExcelDownloadCaseType.STATISTICS_USER_LIST) return titlePropsArrayForStatisticsUserList;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS) return titlePropsArrayForProcessExcelAdded;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PHASING_CODE_VALIDATION_RESULT) return titlePropsArrayForPhasingCodeValidationResult;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST) return titleProcessItemCost;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST) return titleProcessItemCostDetailInProcessItemCost;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL) return titleProcessItemCostDetail;
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_WORKER_DEVICE_DETAIL) return titleProcessWorkerDeviceDetail;
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST) return titleGisungProcessItemCost;
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_GISUNG_PROCESS_ITEM) return titleGisungProcessItemCostDetailInGisungProcessItem;
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_TOTAL) return titleGisungProcessItemCostSum;
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST_TOTAL) return titleGisungProcessItemCostDetailInProcessItemCostSum;
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE) return titleGisungProcessItemCostGcode;
        return null;
    }

    private <T> void makeExcelBody(ExcelDownloadCaseType excelDownloadCaseType
            , List<T> excelBodyDTOs
            , SXSSFSheet objSheet
            , SXSSFRow objRow
            , SXSSFCell objCell
            , XSSFWorkbook xssfWorkbook) {

        if(excelDownloadCaseType == ExcelDownloadCaseType.ISSUE_LIST) makeExcelBodyForIssueList(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.COMPANY_LIST) makeExcelBodyForCompanyList(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.USER_LIST) makeExcelBodyForUserList(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.STATISTICS_USER_LIST) makeExcelBodyForStatisticsUserList(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS) makeExcelBodyForProcess(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PHASING_CODE_VALIDATION_RESULT) makeExcelBodyForResultCodeValidation(excelBodyDTOs, objSheet, objRow, objCell);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST) makeExcelBodyForResultProcessItemCost(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST) makeExcelBodyForResultProcessItemCostDetailInProcessItemCost(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL) makeExcelBodyForResultProcessItemCostDetail(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_WORKER_DEVICE_DETAIL) makeExcelBodyForResultProcessWorkerDeviceDetail(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST) makeExcelBodyForResultGisungProcessItem(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_GISUNG_PROCESS_ITEM) makeExcelBodyForResultGisungProcessItemCostDetailInGisungProcessItem(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_TOTAL) makeExcelBodyForResultGisungProcessItemCostTotal(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST_TOTAL) makeExcelBodyForResultGisungProcessItemCostDetailInGisungProcessItemTotal(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
        if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE) makeExcelBodyForResultGisungProcessItemCostDetailGcode(excelBodyDTOs, objSheet, objRow, objCell, xssfWorkbook);
    }

    private <T> void makeExcelBodyForStatisticsUserList(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell) {
        int rowNum = 1;

        List<StatisticsUserDTO> statisticsUserDTOs = (List<StatisticsUserDTO>) excelBodyDTOs;

        for( StatisticsUserDTO item : statisticsUserDTOs) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // No
            objCell = objRow.createCell(0);
            objCell.setCellValue(rowNum);

            // 성명
            objCell = objRow.createCell(1);
            objCell.setCellValue(item.getUserName());

            // 아이디
            objCell = objRow.createCell(2);
            objCell.setCellValue(item.getEmail());

            // 회사명
            objCell = objRow.createCell(3);
            objCell.setCellValue(item.getCompanyName());

            // 직급
            objCell = objRow.createCell(4);
            objCell.setCellValue(item.getRank());

            // 공종
            objCell = objRow.createCell(5);
            objCell.setCellValue(item.getWorkNames());

            // 화면명
            objCell = objRow.createCell(6);
            objCell.setCellValue(item.getMenuName());

            // 접속 일시
            objCell = objRow.createCell(7);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getAccessDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC));

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForUserList(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell) {
        int rowNum = 1;

        List<AccountDTO> accounts = (List<AccountDTO>) excelBodyDTOs;

        for( AccountDTO item : accounts) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // No
            objCell = objRow.createCell(0);
            objCell.setCellValue(rowNum);

            // 성명
            objCell = objRow.createCell(1);
            objCell.setCellValue(item.getUserName());

            // 아이디
            objCell = objRow.createCell(2);
            objCell.setCellValue(item.getEmail());

            // 회사명
            objCell = objRow.createCell(3);
            objCell.setCellValue(item.getCompanyName());

            // 휴대전화번호
            objCell = objRow.createCell(4);
            objCell.setCellValue(item.getMobileNo());

            // 공종
            objCell = objRow.createCell(5);
            objCell.setCellValue(item.getWorkNames());

            // 활성여부
            objCell = objRow.createCell(6);
            objCell.setCellValue(item.getEnabled() == 1?proc.translate("admin.user_list.page.active") : proc.translate("admin.user_list.page.inactive"));

            // 최종 수정 일시
            objCell = objRow.createCell(7);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getLastModifyDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN));

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForIssueList(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell ) {

        int rowNum = 1;

        List<IssueDTO> issueDTOS = (List<IssueDTO>) excelBodyDTOs;

        for( IssueDTO item : issueDTOS) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // No
            objCell = objRow.createCell(0);
            objCell.setCellValue(rowNum);

            // 제목
            objCell = objRow.createCell(1);
            objCell.setCellValue(item.getTitle());

            // 작성자명
            objCell = objRow.createCell(2);
            objCell.setCellValue(item.getWriterDTO().getUserName());

            // 작성자 소속
            objCell = objRow.createCell(3);
            objCell.setCellValue(item.getWriterDTO().getUserName() + " (" + item.getWriterDTO().getCompanyRoleName() + " : " + item.getWriterDTO().getCompanyName() + ")");

            // 작성자 휴대번호
            objCell = objRow.createCell(4);
            objCell.setCellValue(item.getWriterDTO().getMobileNo());

            // 완료 요구일자
            objCell = objRow.createCell(5);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getRequestDate(), DateFormatEnum.YEAR_MONTH_DAY));

            // 중요도
            objCell = objRow.createCell(6);
            objCell.setCellValue(getIssuePriorityText(item.getPriority()));

            // 상태
            objCell = objRow.createCell(7);
            objCell.setCellValue(proc.translate(item.getStatus().getMessageProperty()));

            // 상태 변경일자
            objCell = objRow.createCell(8);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getStatusUpdateDate(), DateFormatEnum.YEAR_MONTH_DAY));

            // 작성일자
            objCell = objRow.createCell(9);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getWriteDate(), DateFormatEnum.YEAR_MONTH_DAY));

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForCompanyList(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell ) {

        int rowNum = 1;

        List<CompanyDTO> companyDTOS = (List<CompanyDTO>) excelBodyDTOs;

        for( CompanyDTO item : companyDTOS) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // No
            objCell = objRow.createCell(0);
            objCell.setCellValue(rowNum);

            // 회사명
            objCell = objRow.createCell(1);
            objCell.setCellValue(item.getName());

            // 현장 전화번호
            objCell = objRow.createCell(2);
            objCell.setCellValue(item.getTelNo());

            // 현장 책임자
            objCell = objRow.createCell(3);
            if(item.getResponsibleUserDTO().getUserId() == 0) objCell.setCellValue("");
            else objCell.setCellValue(item.getResponsibleUserDTO().getUserName() + " (" + item.getResponsibleUserDTO().getCompanyRoleName() + " : " + item.getResponsibleUserDTO().getCompanyName() + ")");

            // 현장 책임자 휴대전화 번호
            objCell = objRow.createCell(4);
            if(item.getResponsibleUserDTO().getUserId() == 0) objCell.setCellValue("");
            else objCell.setCellValue(item.getResponsibleUserDTO().getMobileNo());

            // 회사 역할
            objCell = objRow.createCell(5);
            objCell.setCellValue(item.getCompanyRoleName());

            // 공종
            objCell = objRow.createCell(6);
            objCell.setCellValue(item.getWorksNames());

            // 사업개요 표시
            objCell = objRow.createCell(7);
            objCell.setCellValue(item.isDisplay() ? "Y" : "N");

            // 사용여부
            objCell = objRow.createCell(8);
            objCell.setCellValue(item.getStatus().equalsIgnoreCase("REG") ? "Y" : "N");

            // 최종 수정일시
            objCell = objRow.createCell(9);
            objCell.setCellValue(Utils.getDateTimeByNationAndFormatType(item.getLastModifyDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN));

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForProcess(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell ) {

        int rowNum = 1;

        List<ProcessDTO> processDTOs = (List<ProcessDTO>) excelBodyDTOs;

        int maxTaskDepth = processDTOs.stream().max(Comparator.comparing(ProcessDTO::getTaskDepth)).orElseGet(ProcessDTO::new).getTaskDepth();

        for( ProcessDTO item : processDTOs.stream().sorted(Comparator.comparingInt(ProcessDTO::getGanttSortNo)).collect(Collectors.toList())) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // Task#
            for(int idx=0; idx < maxTaskDepth; idx++){
                objCell = objRow.createCell(idx);
                if(idx == item.getTaskDepth() - 1) objCell.setCellValue(item.getTaskName());
                else objCell.setCellValue("");
            }

            // Phasing Codes
            objCell = objRow.createCell(maxTaskDepth);
            objCell.setCellValue(item.getPhasingCode() == null ? "" : item.getPhasingCode());

            // Duration
            objCell = objRow.createCell(maxTaskDepth + 1);
            objCell.setCellValue(item.getDuration());

            // Planned Start
            objCell = objRow.createCell(maxTaskDepth + 2);
            objCell.setCellValue(item.getStartDate());

            // Planned End
            objCell = objRow.createCell(maxTaskDepth + 3);
            objCell.setCellValue(item.getEndDate());

            // FS Codes
            objCell = objRow.createCell(maxTaskDepth + 4);
            objCell.setCellValue(item.getFsCodes() == null ? "" : item.getFsCodes());

            // FS
            objCell = objRow.createCell(maxTaskDepth + 5);
            objCell.setCellValue(item.getFs() == null ? "" : item.getFs());

            // FS Dur
            objCell = objRow.createCell(maxTaskDepth + 6);
            objCell.setCellValue(item.getFsDur() == null ? "" : item.getFsDur() );

            // Formula
            objCell = objRow.createCell(maxTaskDepth + 7);
            objCell.setCellValue(item.getFirstCountFormula() == null ? "" : item.getFirstCountFormula() );

            // Quantity
            objCell = objRow.createCell(maxTaskDepth + 8);
            objCell.setCellValue(item.getFirstCount() == null ? "" : item.getFirstCount().toString());

            // Unit
            objCell = objRow.createCell(maxTaskDepth + 9);
            objCell.setCellValue(item.getFirstCountUnit() == null ? "" : item.getFirstCountUnit() );

            // Object unit cost
            objCell = objRow.createCell(maxTaskDepth + 10);
            objCell.setCellValue(item.getComplexUnitPrice() == null ? "" : item.getComplexUnitPrice().toString() );

            // Base cost
            objCell = objRow.createCell(maxTaskDepth + 11);
            objCell.setCellValue(item.getCost() == null ? "" : item.getCost().toString() );

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForResultCodeValidation(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell ) {

        int rowNum = 1;

        List<ProcessItem> processItems = (List<ProcessItem>) excelBodyDTOs;

        for( ProcessItem item : processItems) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 0x150);

            // No
            objCell = objRow.createCell(0);
            objCell.setCellValue(rowNum);

            // 공정명
            objCell = objRow.createCell(1);
            objCell.setCellValue(item.getTaskName());

            // 페이징 코드
            objCell = objRow.createCell(2);
            objCell.setCellValue(item.getPhasingCode());

            // 시작일자
            objCell = objRow.createCell(3);
            objCell.setCellValue(item.getStartDate());

            // 종료일자
            objCell = objRow.createCell(4);
            objCell.setCellValue(item.getEndDate());

            // 결과
            objCell = objRow.createCell(5);
            if(item.getValidate() == ProcessValidateResult.SUCCESS) objCell.setCellValue("SUCCESS");
            if(item.getValidate() == ProcessValidateResult.FAIL) objCell.setCellValue("FAIL");
            if(item.getValidate() == ProcessValidateResult.NONE) objCell.setCellValue("NONE");

            ++rowNum;
        }
    }

    private <T> void makeExcelBodyForResultProcessItemCost(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 2;
        List<VmProcessItemDTO> processItem = (List<VmProcessItemDTO>) excelBodyDTOs;

        int taskDepth = 0;
        for(int i=0;i<processItem.size();i++){
            taskDepth = Math.max(taskDepth, processItem.get(i).getTaskDepth());
        }

        DecimalFormat df = new DecimalFormat("#,###,###");
        DecimalFormat dfp = new DecimalFormat("#,###,###.####");

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for(VmProcessItemDTO item : processItem){
            //열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 400);

            //공정명
            for(int i=1; i<= taskDepth ; i++){
                if (item.getTaskDepth() == i) {
                    objCell = objRow.createCell(0 + i - 1);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue(item.getTaskName());
                } else {
                    objCell = objRow.createCell(0 + i - 1);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("");
                }
            }

            //공종
            objCell = objRow.createCell(1+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getWorkName());

            //공정코드
            objCell = objRow.createCell(2+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getPhasingCode());

            //진행률
            objCell = objRow.createCell(3+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(dfp.format(item.getProgressRate()) + "%");

            //작업일수
            objCell = objRow.createCell(4+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getDuration()) );

            //시작일
            objCell = objRow.createCell(5+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getStartDate());

            //종료일
            objCell = objRow.createCell(6+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getEndDate());

            //계산식
            objCell = objRow.createCell(7+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getFirstCountFormula());

            //값
            objCell = objRow.createCell(8+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if(item.getGanttTaskType().getValue().equals("task")){ objCell.setCellValue( dfp.format(item.getFirstCount()) ); }

            //단위
            objCell = objRow.createCell(9+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( item.getFirstCountUnit() );

            //복합단가
            objCell = objRow.createCell(10+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if(item.getGanttTaskType().getValue().equals("task")){
                objCell.setCellValue( df.format(item.getComplexUnitPrice()) );
            }

            //비용
            objCell = objRow.createCell(11+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getTaskCost()) );

            //기성금
            objCell = objRow.createCell(12+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getPaidCost()) );

            rowNum++;
        }

        objSheet.setColumnWidth(0, 3000);
        objSheet.setColumnWidth(1, 3000);
        objSheet.setColumnWidth(2, 3000);
        objSheet.setColumnWidth(3, 3000);
        objSheet.setColumnWidth(4, 4000);
        objSheet.setColumnWidth(5, 8000);
        objSheet.setColumnWidth(7, 3000);
        objSheet.setColumnWidth(11, 3000);
        objSheet.setColumnWidth(12, 3000);
        objSheet.setColumnWidth(13, 3000);
        objSheet.setColumnWidth(16, 4000);
        objSheet.setColumnWidth(17, 4000);
        objSheet.setColumnWidth(18, 4000);
    }
    private <T> void makeExcelBodyForResultProcessItemCostDetailInProcessItemCost(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 1;

        List<ProcessItemCostDetailDTO> processItemCostDetailDTOS = (List<ProcessItemCostDetailDTO>) excelBodyDTOs;
        if(processItemCostDetailDTOS.size() != 0){
            boolean haveFirstCount = false;
            boolean isZero = false;
            String zeroCodeString = "";
            BigDecimal summaryCountString = new BigDecimal(-1);
            BigDecimal summaryCostString = new BigDecimal(0);
            BigDecimal result= new BigDecimal(0);
            String resultString = "";

            XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
            Font headerFont = xssfWorkbook.createFont();
            headerFont.setFontName("나눔고딕");
            headerFont.setFontHeightInPoints((short)9);
            objStyle.setFont(headerFont);
            objStyle.setBorderBottom(BorderStyle.THIN);
            objStyle.setBorderTop(BorderStyle.THIN);
            objStyle.setBorderLeft(BorderStyle.THIN);
            objStyle.setBorderRight(BorderStyle.THIN);

            DecimalFormat df = new DecimalFormat("#,###,###");
            DecimalFormat dfp = new DecimalFormat("#,###,###.####");

            String currentTaskName = processItemCostDetailDTOS.get(0).getTaskName();
            String currentPhasingCode = processItemCostDetailDTOS.get(0).getPhasingCode();
            for( ProcessItemCostDetailDTO processItemCostDetail : processItemCostDetailDTOS) {
                if( !currentTaskName.equals(processItemCostDetail.getTaskName()) && !currentPhasingCode.equals(processItemCostDetail.getPhasingCode()) ){
                    currentTaskName = processItemCostDetail.getTaskName();
                    currentPhasingCode = processItemCostDetail.getPhasingCode();
                    rowNum++;
                }
                // 열 생성
                objRow = objSheet.createRow(rowNum);
                objRow.setHeight((short) 0x150);

                // 공정명
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getTaskName());


                // 공정코드
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getPhasingCode());

                // 코드
                objCell = objRow.createCell(2);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getCode());

                // 명
                objCell = objRow.createCell(3);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getName());

                // 대표
                if(processItemCostDetail.isFirst()){
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("O");
                    summaryCountString = processItemCostDetail.getCount();
                    haveFirstCount = true;
                } else {
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("");
                }

                // ⓐ값
                objCell = objRow.createCell(5);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(processItemCostDetail.getCount()));

                // 단위
                objCell = objRow.createCell(6);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getUnit());

                // ⓑ단가
                objCell = objRow.createCell(7);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(processItemCostDetail.getUnitPrice()));

                ++rowNum;
            }

            objSheet.setColumnWidth(0, 8000);
            objSheet.setColumnWidth(1, 3000);
            objSheet.setColumnWidth(2, 5000);
            objSheet.setColumnWidth(3, 8000);
            objSheet.setColumnWidth(4, 3000);
            objSheet.setColumnWidth(5, 3000);
            objSheet.setColumnWidth(6, 3000);
        }
    }


    private <T> void makeExcelBodyForResultProcessItemCostDetail(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 2;

        List<ProcessItemCostDetailDTO> processItemCostDetailDTOS = (List<ProcessItemCostDetailDTO>) excelBodyDTOs;

        boolean haveFirstCount = false;
        boolean isZero = false;
        String zeroCodeString = "";
        BigDecimal summaryCountString = new BigDecimal(-1);
        BigDecimal summaryCostString = new BigDecimal(0);
        BigDecimal result= new BigDecimal(0);
        String resultString = "";

        DecimalFormat df = new DecimalFormat("#,###,###");
        DecimalFormat dfp = new DecimalFormat("#,###,###.###");

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for( ProcessItemCostDetailDTO processItemCostDetail : processItemCostDetailDTOS) {

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 400);

            // 코드
            objCell = objRow.createCell(0);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(processItemCostDetail.getCode());

            // 명
            objCell = objRow.createCell(1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(processItemCostDetail.getName());

            // 대표
            objCell = objRow.createCell(2);
            objCell.setCellStyle(objStyle);
            if(processItemCostDetail.isFirst()){
                objCell.setCellValue("O");
                summaryCountString = processItemCostDetail.getCount();
                haveFirstCount = true;
            }
            else{
                objCell.setCellValue("");
            }

            // ⓐ값
            objCell = objRow.createCell(3);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(dfp.format(processItemCostDetail.getCount()));
            if(processItemCostDetail.getCount().signum() == 0){
                isZero = true;
                zeroCodeString += processItemCostDetail.getCode() + "값, ";
            }

            // 단위
            objCell = objRow.createCell(4);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(processItemCostDetail.getUnit());

            // ⓑ단가
            objCell = objRow.createCell(5);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(df.format(processItemCostDetail.getUnitPrice()));

            if(processItemCostDetail.getUnitPrice().signum() == 0){
                isZero = true;
                zeroCodeString += processItemCostDetail.getCode() + "단가, ";
            }

            // 공사비(ⓐ*ⓑ)
            objCell = objRow.createCell(6);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(df.format(processItemCostDetail.getCost()));
            summaryCostString = summaryCostString.add(processItemCostDetail.getCost());

            ++rowNum;
        }

        objRow = objSheet.createRow(rowNum);
        objRow.setHeight((short) 400);

        // 집계
        objCell = objRow.createCell(0);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue("집계");

        objCell = objRow.createCell(1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(processItemCostDetailDTOS.size());

        objCell = objRow.createCell(2);
        objCell.setCellStyle(objStyle);
        if(haveFirstCount){
            if(isZero){
                zeroCodeString = zeroCodeString + "의 비용이 없어 계산 할 수 없습니다.";
                objCell.setCellValue(zeroCodeString);
            }
            else{
                result = summaryCostString.divide(summaryCountString, 0, BigDecimal.ROUND_DOWN);
                resultString = df.format(result) + " = " + df.format(summaryCostString) + " / " + dfp.format(summaryCountString);
                objCell.setCellValue(resultString);
            }
        }
        else{
            objCell.setCellValue("대표가 없습니다.");
        }
        objCell = objRow.createCell(3);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(4);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(5);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, 5));

        objCell = objRow.createCell(6);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(df.format(summaryCostString));

        objSheet.setColumnWidth(0, 5000);
        objSheet.setColumnWidth(1, 8000);
        objSheet.setColumnWidth(2, 3000);
        objSheet.setColumnWidth(3, 3000);
        objSheet.setColumnWidth(4, 3000);
        objSheet.setColumnWidth(5, 4000);
        objSheet.setColumnWidth(6, 4000);

    }

    private <T> void makeExcelBodyForResultProcessWorkerDeviceDetail(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 1;

        List<SelectProgressConfig> selectProgressConfigs = (List<SelectProgressConfig>) excelBodyDTOs;

        boolean ctypeFlag = true;

        DecimalFormat df = new DecimalFormat("#,###,###");
        DecimalFormat dfp = new DecimalFormat("#,###,###.###");

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle objStyle2 = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont2 = xssfWorkbook.createFont();
        headerFont2.setFontName("나눔고딕");
        headerFont2.setFontHeightInPoints((short)9);
        headerFont2.setBold(true);
        headerFont2.setColor(IndexedColors.WHITE.getIndex());
        objStyle2.setFont(headerFont2);
        objStyle2.setBorderBottom(BorderStyle.THIN);
        objStyle2.setBorderTop(BorderStyle.THIN);
        objStyle2.setBorderLeft(BorderStyle.THIN);
        objStyle2.setBorderRight(BorderStyle.THIN);
        objStyle2.setAlignment(HorizontalAlignment.CENTER);
        objStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle2).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for( SelectProgressConfig selectProgressConfig : selectProgressConfigs) {

            if (rowNum == 1) {
                objRow = objSheet.createRow(rowNum);
                objRow.setHeight((short) 400);

                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle2);
                objCell.setCellValue("인원(인)");
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle2);
                objSheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
                ++rowNum;
            } else {
                if (ctypeFlag && selectProgressConfig.getCtype().equals("device")) {
                    objRow = objSheet.createRow(rowNum);
                    objRow.setHeight((short) 400);

                    objCell = objRow.createCell(0);
                    objCell.setCellStyle(objStyle2);
                    objCell.setCellValue("장비(대)");
                    objCell = objRow.createCell(1);
                    objCell.setCellStyle(objStyle2);
                    objSheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
                    ctypeFlag = false;
                    ++rowNum;
                }
            }

            // 열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 400);

            // 명
            objCell = objRow.createCell(0);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(selectProgressConfig.getName());

            // 값
            objCell = objRow.createCell(1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(dfp.format(selectProgressConfig.getAmount()));

            ++rowNum;
        }

        objSheet.setColumnWidth(0, 8000);
        objSheet.setColumnWidth(1, 5000);

    }

    private <T> void makeExcelBodyForResultGisungProcessItem(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 2;
        List<VmGisungProcessItemDTO> gisungProcessItemDTOs = (List<VmGisungProcessItemDTO>) excelBodyDTOs;

        DecimalFormat df = new DecimalFormat("#,###,###");
        DecimalFormat dfp = new DecimalFormat("#,###,###.####");

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle objStyle2 = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont2 = xssfWorkbook.createFont();
        headerFont2.setFontName("나눔고딕");
        headerFont2.setFontHeightInPoints((short)9);
        objStyle2.setFont(headerFont2);
        objStyle2.setBorderBottom(BorderStyle.THIN);
        objStyle2.setBorderTop(BorderStyle.THIN);
        objStyle2.setBorderLeft(BorderStyle.THIN);
        objStyle2.setBorderRight(BorderStyle.THIN);
        objStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle2).setFillForegroundColor(new XSSFColor(new java.awt.Color(204, 204, 204),null));
        objStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(VmGisungProcessItemDTO item : gisungProcessItemDTOs){
            //열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 400);

            if (item.getPhasingCode() == null || item.getPhasingCode().equals("")) {
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle2);
                objCell.setCellValue(item.getCate1Name() + " > " + item.getCate2Name() + " > " + item.getCate3Name() + " > " + item.getCate4Name() + " > " + item.getCate5Name());
                objSheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 13));
            } else {
                // 공정명
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(item.getTaskName());

                // 공정코드
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(item.getPhasingCode());

                // 복합단가
                objCell = objRow.createCell(2);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getComplexUnitPrice()));

                // 전체 공사비
                objCell = objRow.createCell(3);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getTaskCost()));

                // 실적 진행률
                objCell = objRow.createCell(4);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(item.getTodayProgressRate()) + "%");

                // 실적 공사비
                objCell = objRow.createCell(5);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getTodayProgressAmount()));

                // 전회기성 진행률
                objCell = objRow.createCell(6);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(item.getPaidProgressRate().multiply(new BigDecimal(100))) + "%");

                // 전회기성 공사비
                objCell = objRow.createCell(7);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getPaidCost()));

                // 금회기성 진행률
                objCell = objRow.createCell(8);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(item.getProgressRate().multiply(new BigDecimal(100))) + "%");

                // 금회기성 공사비
                objCell = objRow.createCell(9);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getCost()));

                // 누적기성 진행률
                objCell = objRow.createCell(10);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(item.getPaidProgressRate().multiply(new BigDecimal(100)).add(item.getProgressRate().multiply(new BigDecimal(100)))) + "%");

                // 누적기성 공사비
                objCell = objRow.createCell(11);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getPaidCost().add(item.getCost())));

                // 잔액기성금액 진행률
                objCell = objRow.createCell(12);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(new BigDecimal(100).subtract(item.getPaidProgressRate().multiply(new BigDecimal(100))).subtract(item.getProgressRate().multiply(new BigDecimal(100)))) + "%");

                // 잔액기성금액 공사비
                objCell = objRow.createCell(13);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getTaskCost().subtract(item.getPaidCost()).subtract(item.getCost())));
            }

            rowNum++;
        }

        objSheet.setColumnWidth(0, 8000);
        objSheet.setColumnWidth(1, 3000);
        objSheet.setColumnWidth(2, 3000);
        objSheet.setColumnWidth(3, 3000);
        objSheet.setColumnWidth(4, 3000);
        objSheet.setColumnWidth(5, 3000);
        objSheet.setColumnWidth(6, 3000);
        objSheet.setColumnWidth(7, 3000);
        objSheet.setColumnWidth(8, 3000);
        objSheet.setColumnWidth(9, 3000);
        objSheet.setColumnWidth(10, 3000);
        objSheet.setColumnWidth(11, 3000);
        objSheet.setColumnWidth(12, 3000);
        objSheet.setColumnWidth(13, 3000);
    }
    private <T> void makeExcelBodyForResultGisungProcessItemCostDetailInGisungProcessItem(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 2;

        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOS = (List<GisungProcessItemCostDetailDTO>) excelBodyDTOs;
        if(gisungProcessItemCostDetailDTOS.size() != 0){
            boolean haveFirstCount = false;
            boolean isZero = false;
            String zeroCodeString = "";
            BigDecimal summaryCountString = new BigDecimal(-1);
            BigDecimal summaryCostString = new BigDecimal(0);
            BigDecimal result= new BigDecimal(0);
            String resultString = "";

            XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
            Font headerFont = xssfWorkbook.createFont();
            headerFont.setFontName("나눔고딕");
            headerFont.setFontHeightInPoints((short)9);
            objStyle.setFont(headerFont);
            objStyle.setBorderBottom(BorderStyle.THIN);
            objStyle.setBorderTop(BorderStyle.THIN);
            objStyle.setBorderLeft(BorderStyle.THIN);
            objStyle.setBorderRight(BorderStyle.THIN);

            DecimalFormat df = new DecimalFormat("#,###,###");
            DecimalFormat dfp = new DecimalFormat("#,###,###.####");

            String currentTaskName = gisungProcessItemCostDetailDTOS.get(0).getTaskName();
            String currentPhasingCode = gisungProcessItemCostDetailDTOS.get(0).getPhasingCode();
            for( GisungProcessItemCostDetailDTO gisungProcessItemCostDetail : gisungProcessItemCostDetailDTOS) {
                if( !currentTaskName.equals(gisungProcessItemCostDetail.getTaskName()) && !currentPhasingCode.equals(gisungProcessItemCostDetail.getPhasingCode()) ){
                    currentTaskName = gisungProcessItemCostDetail.getTaskName();
                    currentPhasingCode = gisungProcessItemCostDetail.getPhasingCode();
                    rowNum++;
                }
                // 열 생성
                objRow = objSheet.createRow(rowNum);
                objRow.setHeight((short) 0x150);

                // 공정명
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getTaskName());


                // 공정코드
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getPhasingCode());

                // 코드
                objCell = objRow.createCell(2);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getCode());

                // 명
                objCell = objRow.createCell(3);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getName());

                // 대표
                if(gisungProcessItemCostDetail.isFirst()){
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("O");
                    summaryCountString = gisungProcessItemCostDetail.getCount();
                    haveFirstCount = true;
                } else {
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("");
                }

                // ⓐ값
                objCell = objRow.createCell(5);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getCount()));

                // 단위
                objCell = objRow.createCell(6);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getUnit());

                // ⓑ단가
                objCell = objRow.createCell(7);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getUnitPrice()));

                objCell = objRow.createCell(8);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getCost()));

                objCell = objRow.createCell(9);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getJobSheetProgressCount()));

                objCell = objRow.createCell(10);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getJobSheetProgressAmount()));

                objCell = objRow.createCell(11);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getPaidProgressCount()));

                objCell = objRow.createCell(12);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getPaidCost()));

                objCell = objRow.createCell(13);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getProgressCount()));

                objCell = objRow.createCell(14);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getProgressCost()));

                objCell = objRow.createCell(15);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getSumProgressCount()));

                objCell = objRow.createCell(16);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getSumProgressCost()));

                objCell = objRow.createCell(17);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getRemindProgressCount()));

                objCell = objRow.createCell(18);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(gisungProcessItemCostDetail.getRemindProgressCost()));

                ++rowNum;
            }

            objSheet.setColumnWidth(0, 8000);
            objSheet.setColumnWidth(1, 3000);
            objSheet.setColumnWidth(2, 5000);
            objSheet.setColumnWidth(3, 8000);
            objSheet.setColumnWidth(4, 3000);
            objSheet.setColumnWidth(5, 3000);
            objSheet.setColumnWidth(6, 3000);
            objSheet.setColumnWidth(7, 3000);
            objSheet.setColumnWidth(8, 3000);
            objSheet.setColumnWidth(9, 3000);
            objSheet.setColumnWidth(10, 3000);
            objSheet.setColumnWidth(11, 3000);
            objSheet.setColumnWidth(12, 3000);
            objSheet.setColumnWidth(13, 3000);
            objSheet.setColumnWidth(14, 3000);
            objSheet.setColumnWidth(15, 3000);
            objSheet.setColumnWidth(16, 3000);
            objSheet.setColumnWidth(17, 3000);
            objSheet.setColumnWidth(18, 3000);
        }
    }

    private <T> void makeExcelBodyForResultGisungProcessItemCostTotal(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 2;
        List<ProcessItemCostDTO> processItem = (List<ProcessItemCostDTO>) excelBodyDTOs;

        int taskDepth = 0;
        for(int i=0;i<processItem.size();i++){
            taskDepth = Math.max(taskDepth, processItem.get(i).getTaskDepth());
        }

        DecimalFormat df = new DecimalFormat("#,###,###");
        DecimalFormat dfp = new DecimalFormat("#,###,###.###");

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for(ProcessItemCostDTO item : processItem){
            //열 생성
            objRow = objSheet.createRow(rowNum);
            objRow.setHeight((short) 400);

            //공정명
            for(int i=1; i<= taskDepth ; i++){
                if (item.getTaskDepth() == i) {
                    objCell = objRow.createCell(0 + i - 1);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue(item.getTaskName());
                } else {
                    objCell = objRow.createCell(0 + i - 1);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("");
                }
            }

            //공종
            objCell = objRow.createCell(1+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getWorkName());

            //공정코드
            objCell = objRow.createCell(2+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getPhasingCode());

            //진행률
            objCell = objRow.createCell(3+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(dfp.format(item.getProgressRate()) + "%");

            //작업일수
            objCell = objRow.createCell(4+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getDuration()) );

            //시작일
            objCell = objRow.createCell(5+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getStartDate());

            //종료일
            objCell = objRow.createCell(6+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getEndDate());

            //계산식
            objCell = objRow.createCell(7+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(item.getFirstCountFormula());

            //값
            objCell = objRow.createCell(8+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if(item.getGanttTaskType().getValue().equals("task")){ objCell.setCellValue( dfp.format(item.getFirstCount()) ); }

            //단위
            objCell = objRow.createCell(9+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( item.getFirstCountUnit() );

            //복합단가
            objCell = objRow.createCell(10+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if(item.getGanttTaskType().getValue().equals("task")){
                objCell.setCellValue( df.format(item.getComplexUnitPrice()) );
            }

            //비용
            objCell = objRow.createCell(11+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getTaskCost()) );

            //기성금
            objCell = objRow.createCell(12+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue( df.format(item.getPaidCost()) );

            // 실적 진행률
            objCell = objRow.createCell(13+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(dfp.format(item.getProgressRate()) + "%");

            // 실적 공사비
            objCell = objRow.createCell(14+taskDepth-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(df.format(item.getCost()));

            // 전회기성 진행률
            objCell = objRow.createCell(15+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if (item.getPrevPaidProgressRate() != null) {
                objCell.setCellValue(dfp.format(item.getPrevPaidProgressRate().multiply(new BigDecimal(100))) + "%");
                //objCell.setCellValue(dfp.format(item.getPaidProgressRate().subtract(item.getTodayProgressRate()).multiply(new BigDecimal(100))) + "%");
            } else {
                objCell.setCellValue("0%");
            }

            // 전회기성 공사비
            objCell = objRow.createCell(16+taskDepth-1);
            objCell.setCellStyle(objStyle);
            if (item.getPrevPaidCost() != null) {
                objCell.setCellValue(df.format(item.getPrevPaidCost()));
                //objCell.setCellValue(df.format(item.getPaidCost().subtract(item.getTodayCost())));
            } else {
                objCell.setCellValue("0");
            }

            // 금회기성 진행률
            if (item.getPaidProgressRate() != null) {
                objCell = objRow.createCell(17 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(item.getPaidProgressRate().subtract(item.getPrevPaidProgressRate()).multiply(new BigDecimal(100))) + "%");
                //objCell.setCellValue(dfp.format(item.getTodayProgressRate().multiply(new BigDecimal(100))) + "%");
            } else {
                objCell = objRow.createCell(17 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0%");
            }

            // 금회기성 공사비
            if (item.getPaidCost() != null) {
                objCell = objRow.createCell(18 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                //objCell.setCellValue(df.format(item.getTodayCost()));
                objCell.setCellValue(df.format(item.getPaidCost().subtract(item.getPrevPaidCost())));
            } else {
                objCell = objRow.createCell(18 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0");
            }
            /**
            // 누적기성 진행률
            if (item.getTodayProgressRate() != null) {
                BigDecimal remindPaidProgressRate = item.getPaidProgressRate().multiply(new BigDecimal(100)).add(item.getTodayProgressRate().multiply(new BigDecimal(100)));
                objCell = objRow.createCell(19 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(remindPaidProgressRate) + "%");
            } else {
                objCell = objRow.createCell(19 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0%");
            }

            // 누적기성 공사비
            if (item.getTodayCost() != null) {
                objCell = objRow.createCell(20 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(item.getPaidCost().add(item.getTodayCost())));
            } else {
                objCell = objRow.createCell(20 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0");
            }

            // 잔액기성금액 진행률
            if (item.getTodayProgressRate() != null) {
                objCell = objRow.createCell(21+taskDepth-1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(new BigDecimal(100).subtract(item.getPaidProgressRate().multiply(new BigDecimal(100))).subtract(item.getTodayProgressRate().multiply(new BigDecimal(100)))) + "%");
            } else {
                objCell = objRow.createCell(21 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0%");
            }

            // 잔액기성금액 공사비
            if (item.getTodayCost() != null) {
                objCell = objRow.createCell(22+taskDepth-1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(new BigDecimal(item.getTaskCost()).subtract(new BigDecimal(item.getPaidCost().intValue())).subtract(item.getTodayCost())));
            } else {
                objCell = objRow.createCell(22 + taskDepth - 1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("0");
            }
            **/
            rowNum++;
        }

        objSheet.setColumnWidth(0, 3000);
        objSheet.setColumnWidth(1, 3000);
        objSheet.setColumnWidth(2, 3000);
        objSheet.setColumnWidth(3, 3000);
        objSheet.setColumnWidth(4, 4000);
        objSheet.setColumnWidth(5, 8000);
        objSheet.setColumnWidth(7, 3000);
        objSheet.setColumnWidth(11, 3000);
        objSheet.setColumnWidth(12, 3000);
        objSheet.setColumnWidth(13, 3000);
        objSheet.setColumnWidth(16, 4000);
        objSheet.setColumnWidth(17, 4000);
        objSheet.setColumnWidth(18, 4000);
        objSheet.setColumnWidth(19, 3000);
        objSheet.setColumnWidth(20, 3000);
        objSheet.setColumnWidth(21, 3000);
        objSheet.setColumnWidth(22, 3000);
        objSheet.setColumnWidth(23, 3000);
        objSheet.setColumnWidth(24, 3000);
        //objSheet.setColumnWidth(25, 3000);
        //objSheet.setColumnWidth(26, 3000);
        //objSheet.setColumnWidth(27, 3000);
        //objSheet.setColumnWidth(28, 3000);
    }
    private <T> void makeExcelBodyForResultGisungProcessItemCostDetailInGisungProcessItemTotal(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 1;

        List<ProcessItemCostDetailDTO> processItemCostDetailDTOS = (List<ProcessItemCostDetailDTO>) excelBodyDTOs;
        if(processItemCostDetailDTOS.size() != 0){
            boolean haveFirstCount = false;
            boolean isZero = false;
            String zeroCodeString = "";
            BigDecimal summaryCountString = new BigDecimal(-1);
            BigDecimal summaryCostString = new BigDecimal(0);
            BigDecimal result= new BigDecimal(0);
            String resultString = "";

            XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
            Font headerFont = xssfWorkbook.createFont();
            headerFont.setFontName("나눔고딕");
            headerFont.setFontHeightInPoints((short)9);
            objStyle.setFont(headerFont);
            objStyle.setBorderBottom(BorderStyle.THIN);
            objStyle.setBorderTop(BorderStyle.THIN);
            objStyle.setBorderLeft(BorderStyle.THIN);
            objStyle.setBorderRight(BorderStyle.THIN);

            DecimalFormat df = new DecimalFormat("#,###,###");
            DecimalFormat dfp = new DecimalFormat("#,###,###.###");

            String currentTaskName = processItemCostDetailDTOS.get(0).getTaskName();
            String currentPhasingCode = processItemCostDetailDTOS.get(0).getPhasingCode();
            for( ProcessItemCostDetailDTO processItemCostDetail : processItemCostDetailDTOS) {
                if( !currentTaskName.equals(processItemCostDetail.getTaskName()) && !currentPhasingCode.equals(processItemCostDetail.getPhasingCode()) ){
                    currentTaskName = processItemCostDetail.getTaskName();
                    currentPhasingCode = processItemCostDetail.getPhasingCode();
                    rowNum++;
                }
                // 열 생성
                objRow = objSheet.createRow(rowNum);
                objRow.setHeight((short) 0x150);

                // 공정명
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getTaskName());


                // 공정코드
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getPhasingCode());

                // 코드
                objCell = objRow.createCell(2);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getCode());

                // 명
                objCell = objRow.createCell(3);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getName());

                // 대표
                if(processItemCostDetail.isFirst()){
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("O");
                    summaryCountString = processItemCostDetail.getCount();
                    haveFirstCount = true;
                } else {
                    objCell = objRow.createCell(4);
                    objCell.setCellStyle(objStyle);
                    objCell.setCellValue("");
                }

                // ⓐ값
                objCell = objRow.createCell(5);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(processItemCostDetail.getCount()));

                // 단위
                objCell = objRow.createCell(6);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(processItemCostDetail.getUnit());

                // ⓑ단가
                objCell = objRow.createCell(7);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(processItemCostDetail.getUnitPrice()));

                objCell = objRow.createCell(8);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(df.format(processItemCostDetail.getCost()));

                // 실적 진행률
                objCell = objRow.createCell(9);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getJobSheetProgressCount() != null) {
                    objCell.setCellValue(dfp.format(processItemCostDetail.getJobSheetProgressCount()));
                } else {
                    objCell.setCellValue("0");
                }

                // 실적 공사비
                objCell = objRow.createCell(10);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getJobSheetProgressAmount() > 0) {
                    objCell.setCellValue(df.format(processItemCostDetail.getJobSheetProgressAmount()));
                } else {
                    objCell.setCellValue("0");
                }

                // 전회기성 진행률
                objCell = objRow.createCell(11);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getJobSheetProgressCount() != null) {
                    objCell.setCellValue(dfp.format(processItemCostDetail.getPaidProgressCount()));
                } else {
                    objCell.setCellValue("0");
                }

                // 전회기성 공사비
                objCell = objRow.createCell(12);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getPaidCost() != null) {
                    objCell.setCellValue(df.format(processItemCostDetail.getPaidCost()));
                } else {
                    objCell.setCellValue("0");
                }

                // 금회기성 진행률
                objCell = objRow.createCell(13);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getJobSheetProgressCount() != null) {
                    objCell.setCellValue(dfp.format(processItemCostDetail.getProgressCount()));
                } else {
                    objCell.setCellValue("0");
                }

                // 금회기성 공사비
                objCell = objRow.createCell(14);
                objCell.setCellStyle(objStyle);
                if (processItemCostDetail.getProgressCost() != null) {
                    objCell.setCellValue(df.format(processItemCostDetail.getProgressCost()));
                } else {
                    objCell.setCellValue("0");
                }

                ++rowNum;
            }

            objSheet.setColumnWidth(0, 8000);
            objSheet.setColumnWidth(1, 3000);
            objSheet.setColumnWidth(2, 5000);
            objSheet.setColumnWidth(3, 8000);
            objSheet.setColumnWidth(4, 3000);
            objSheet.setColumnWidth(5, 3000);
            objSheet.setColumnWidth(6, 3000);
            objSheet.setColumnWidth(8, 3000);
            objSheet.setColumnWidth(9, 3000);
            objSheet.setColumnWidth(10, 3000);
            objSheet.setColumnWidth(11, 3000);
            objSheet.setColumnWidth(12, 3000);
            objSheet.setColumnWidth(13, 3000);
            objSheet.setColumnWidth(14, 3000);
        }
    }

    private <T> void makeExcelBodyForResultGisungProcessItemCostDetailGcode(List<T> excelBodyDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        int rowNum = 1;

        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetails = (List<GisungProcessItemCostDetailDTO>) excelBodyDTOs;
        if(gisungProcessItemCostDetails.size() != 0){
            boolean haveFirstCount = false;
            boolean isZero = false;
            String zeroCodeString = "";
            BigDecimal summaryCountString = new BigDecimal(-1);
            BigDecimal summaryCostString = new BigDecimal(0);
            BigDecimal result= new BigDecimal(0);
            String resultString = "";

            XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
            Font headerFont = xssfWorkbook.createFont();
            headerFont.setFontName("나눔고딕");
            headerFont.setFontHeightInPoints((short)9);
            objStyle.setFont(headerFont);
            objStyle.setBorderBottom(BorderStyle.THIN);
            objStyle.setBorderTop(BorderStyle.THIN);
            objStyle.setBorderLeft(BorderStyle.THIN);
            objStyle.setBorderRight(BorderStyle.THIN);

            DecimalFormat df = new DecimalFormat("#,###,###");
            DecimalFormat dfp = new DecimalFormat("#,###,###.###");

            for( GisungProcessItemCostDetailDTO gisungProcessItemCostDetail : gisungProcessItemCostDetails) {
                // 열 생성
                objRow = objSheet.createRow(rowNum);
                objRow.setHeight((short) 0x150);

                // G코드
                objCell = objRow.createCell(0);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(gisungProcessItemCostDetail.getCode());

                // 수량
                objCell = objRow.createCell(1);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue(dfp.format(gisungProcessItemCostDetail.getCount()));

                // 목표수량
                objCell = objRow.createCell(2);
                objCell.setCellStyle(objStyle);
                objCell.setCellValue("");

                ++rowNum;
            }

            objSheet.setColumnWidth(0, 3000);
            objSheet.setColumnWidth(1, 3000);
            objSheet.setColumnWidth(2, 3000);
        }
    }

    private <T> SXSSFWorkbook makeExcel(ExcelDownloadCaseType excelDownloadCaseType, String sheetName, List<T> excelBodyDTOS){

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, ROW_ACCESS_WINDOW_SIZE);
        SXSSFSheet objSheet = sxssfWorkbook.createSheet(sheetName); // sheet 생성
        SXSSFRow objRow = null;     // row 생성
        SXSSFCell objCell = null;   // cell 생성

        if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS){
            makeProcessExcelTitle((List<ProcessDTO>)excelBodyDTOS, objSheet, objRow, objCell);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST){   // 내역목록 엑셀다운
            // 내역목록 엑셀 생성
            makeProcessItemCostTitle((List<VmProcessItemDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);

            // 새로운 시트를 생성하여 복합단가를 저장
            objSheet = sxssfWorkbook.createSheet("CostDetail"); 
            // processID 찾기
            List<VmProcessItemDTO> processItemCostDTOS = (List<VmProcessItemDTO>) excelBodyDTOS;
            Optional<ProcessItem> processItem = processItemRepository.findById(processItemCostDTOS.get(0).getProcessItemId());
            long processId = processItem.get().getProcessInfo().getId();
            //찾은 processId로 복합단가를 찾기
            excelBodyDTOS = (List<T>) processCostService.findCostDetailByProcessId(processId);

            makeProcessItemCostDetailTitleInProcessItemCost((List<ProcessItemCostDetailDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL){
            makeProcessItemCostDetailTitle((List<ProcessItemCostDetailDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.PROCESS_WORKER_DEVICE_DETAIL){
            makeProcessWorkerDeviceDetailTitle((List<SelectProgressConfig>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_TOTAL){   // 기성 전체 목록 엑셀다운
            // 내역목록 엑셀 생성
            makeGisungProcessItemCostTitle((List<ProcessItemCostDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);

            // 새로운 시트를 생성하여 복합단가를 저장
            objSheet = sxssfWorkbook.createSheet("CostDetail");
            // processID 찾기
            List<ProcessItemCostDTO> processItemCostDTOS = (List<ProcessItemCostDTO>) excelBodyDTOS;
            Optional<ProcessItem> processItem = processItemRepository.findById(processItemCostDTOS.get(0).getProcessItemId());
            long processId = processItem.get().getProcessInfo().getId();
            //찾은 processId로 복합단가를 찾기
            //excelBodyDTOS = (List<T>) processItemCostDetailRepository.findGisungCostDetailByProcessId(processId);
            excelBodyDTOS = (List<T>) processItemCostDslRepository.getProcessItemCostPayGisungProcessItemCost(processId);

            makeGisungProcessItemCostDetailTitleInGisungProcessItemTotal((List<ProcessItemCostDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST_TOTAL, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST){   // 기성 목록 엑셀다운
            // 기성목록 엑셀 생성
             makeGisungProcessItemTitle((List<VmGisungProcessItemDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
             makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);

             // 새로운 시트를 생성하여 복합단가를 저장
             objSheet = sxssfWorkbook.createSheet("GisungCostDetail");
             // gisungProcessItemId 찾기
             List<VmGisungProcessItemDTO> gisungProcessItemDTOS = (List<VmGisungProcessItemDTO>) excelBodyDTOS;
             long gisungId = gisungProcessItemDTOS.get(0).getGisungId();
             System.out.println("gisungId = " + gisungId);
             //찾은 gisungProcessItemId로 복합단가를 찾기
             excelBodyDTOS = (List<T>) gisungService.getGisungProcessItemCostDetail(gisungId);

             makeGisungProcessItemCostDetailTitleInGisungProcessItem((List<GisungProcessItemCostDetailDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
             makeExcelBody(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_GISUNG_PROCESS_ITEM, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else if(excelDownloadCaseType == ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE){   // 기성리스트 파일
            // 기성리스트 엑셀 생성
            makeGisungProcessItemCostDetailGcodeTitle((List<GisungProcessItemCostDetailDTO>)excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }
        else{
            makeExcelTitle(excelDownloadCaseType, objSheet, objRow, objCell);
            makeExcelBody(excelDownloadCaseType, excelBodyDTOS, objSheet, objRow, objCell, xssfWorkbook);
        }

        return sxssfWorkbook;
    }

    private void closeExcelObject(HttpServletResponse response, OutputStream outputStream, SXSSFWorkbook sxssfWorkbook) throws IOException {
        sxssfWorkbook.write(outputStream);
        outputStream.close();

        response.getOutputStream().flush();
        response.getOutputStream().close();

        sxssfWorkbook.dispose();
    }

    private void closeOutPutStream(OutputStream outputStream) throws IOException {
        if(outputStream != null) outputStream.close();
    }

    private void makeProcessExcelTitle(List<ProcessDTO> processDTOs, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell){

        int maxTaskDepth = processDTOs.stream().max(Comparator.comparing(ProcessDTO::getTaskDepth)).orElseGet(ProcessDTO::new).getTaskDepth();

        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.PROCESS);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 0x150);

        for(int idx=0; idx < maxTaskDepth; idx++){
            objCell = objRow.createCell(idx);
            objCell.setCellValue("Task" + (idx + 1) );
        }

        for (String s : titlePropsArray) {
            objCell = objRow.createCell(maxTaskDepth);
            objCell.setCellValue(s);
            maxTaskDepth ++;
        }

    }

    private void makeProcessItemCostTitle(List<VmProcessItemDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.PROCESS_ITEM_COST);
        List<VmProcessItemDTO> processItem = (List<VmProcessItemDTO>) excelBodyDTOS;
        int taskDepth = 0;
        for(int i=0;i<processItem.size();i++){
            taskDepth = Math.max(taskDepth, processItem.get(i).getTaskDepth());
        }

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        for(int i=1; i<= taskDepth ; i++){
            objCell = objRow.createCell(0+i-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(proc.translate(titlePropsArray[0])+i);
        }

        objCell = objRow.createCell(1+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[1]));

        objCell = objRow.createCell(2+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[2]));

        objCell = objRow.createCell(3+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[3]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(4+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[4]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(5+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[5]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(6+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[6]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(7+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[7]));
        objCell = objRow.createCell(8+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(9+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 7+taskDepth-1, 9+taskDepth-1));

        objCell = objRow.createCell(10+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[8]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(11+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(12+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 11+taskDepth-1, 12+taskDepth-1));

        objRow = objSheet.createRow(1);
        objRow.setHeight((short) 400);

        for(int i=1; i<= taskDepth ; i++){
            objCell = objRow.createCell(0+i-1);
            objCell.setCellStyle(objStyle);
            objSheet.addMergedRegion(new CellRangeAddress(0, 1, 0+i-1, 0+i-1));
        }

        objCell = objRow.createCell(1+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 1+taskDepth-1, 1+taskDepth-1));

        objCell = objRow.createCell(2+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 2+taskDepth-1, 2+taskDepth-1));

        objCell = objRow.createCell(3+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 3+taskDepth-1, 3+taskDepth-1));

        objCell = objRow.createCell(4+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 4+taskDepth-1, 4+taskDepth-1));

        objCell = objRow.createCell(5+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 5+taskDepth-1, 5+taskDepth-1));

        objCell = objRow.createCell(6+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 6+taskDepth-1, 6+taskDepth-1));

        objCell = objRow.createCell(7+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(8+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[11]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(9+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[12]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(10+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 10+taskDepth-1, 10+taskDepth-1));

        objCell = objRow.createCell(11+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[13]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(12+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));
        objCell.setCellStyle(objStyle);
    }



    private void makeProcessItemCostDetailTitleInProcessItemCost(List<ProcessItemCostDetailDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook) {
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 0x150);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(int idx = 0; idx < titlePropsArray.length; idx++ ) {
            objCell = objRow.createCell(idx);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(proc.translate(titlePropsArray[idx]));
        }
    }

    private void makeProcessItemCostDetailTitle(List<ProcessItemCostDetailDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.PROCESS_ITEM_COST_DETAIL);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        objCell = objRow.createCell(0);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[0]));
        objCell = objRow.createCell(1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        objCell = objRow.createCell(2);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[1]));
        objCell = objRow.createCell(3);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(4);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(5);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 5));

        objCell = objRow.createCell(6);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[2]));

        objRow = objSheet.createRow(1);
        for(int idx=3; idx < titlePropsArray.length ; idx++){
            objCell = objRow.createCell(idx-3);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(proc.translate(titlePropsArray[idx]));
        }

        objCell = objRow.createCell(6);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6));

    }

    private void makeProcessWorkerDeviceDetailTitle(List<SelectProgressConfig> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.PROCESS_WORKER_DEVICE_DETAIL);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        objCell = objRow.createCell(0);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[0]));
        objCell = objRow.createCell(1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

    }

    private void makeGisungProcessItemTitle(List<VmGisungProcessItemDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST);
        List<VmGisungProcessItemDTO> gisungProcessItem = (List<VmGisungProcessItemDTO>) excelBodyDTOS;

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        objCell = objRow.createCell(0);
        objCell.setCellValue(proc.translate(titlePropsArray[0]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));


        objCell = objRow.createCell(1);
        objCell.setCellValue(proc.translate(titlePropsArray[1]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

        objCell = objRow.createCell(2);
        objCell.setCellValue(proc.translate(titlePropsArray[2]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

        objCell = objRow.createCell(3);
        objCell.setCellValue(proc.translate(titlePropsArray[3]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(4); // 실적
        objCell.setCellValue(proc.translate(titlePropsArray[4]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5));

        objCell = objRow.createCell(6); // 전회기성
        objCell.setCellValue(proc.translate(titlePropsArray[5]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 7));

        objCell = objRow.createCell(8); // 금회기성
        objCell.setCellValue(proc.translate(titlePropsArray[6]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 9));

        objCell = objRow.createCell(10); // 누적기성
        objCell.setCellValue(proc.translate(titlePropsArray[7]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 11));

        objCell = objRow.createCell(12); // 잔액기성금액
        objCell.setCellValue(proc.translate(titlePropsArray[8]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 12, 13));

        objRow = objSheet.createRow(1);
        objRow.setHeight((short) 400);

        objCell = objRow.createCell(0);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(1);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(2);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(3);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));

        objCell = objRow.createCell(4);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));

        objCell = objRow.createCell(5);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));

        objCell = objRow.createCell(6);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));

        objCell = objRow.createCell(7);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));

        objCell = objRow.createCell(8);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));

        objCell = objRow.createCell(9);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));

        objCell = objRow.createCell(10);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));

        objCell = objRow.createCell(11);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));

        objCell = objRow.createCell(12);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));

        objCell = objRow.createCell(13);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));
    }

    private void makeGisungProcessItemCostDetailTitleInGisungProcessItem(List<GisungProcessItemCostDetailDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook) {
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_GISUNG_PROCESS_ITEM);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 0x150);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //공정명
        objCell = objRow.createCell(0);
        objCell.setCellValue(proc.translate(titlePropsArray[0]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

        //공정코드
        objCell = objRow.createCell(1);
        objCell.setCellValue(proc.translate(titlePropsArray[1]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

        //코드
        objCell = objRow.createCell(2);
        objCell.setCellValue(proc.translate(titlePropsArray[2]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

        //명
        objCell = objRow.createCell(3);
        objCell.setCellValue(proc.translate(titlePropsArray[3]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));

        //대표
        objCell = objRow.createCell(4);
        objCell.setCellValue(proc.translate(titlePropsArray[4]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));

        //값
        objCell = objRow.createCell(5);
        objCell.setCellValue(proc.translate(titlePropsArray[5]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 5, 5));

        //단위
        objCell = objRow.createCell(6);
        objCell.setCellValue(proc.translate(titlePropsArray[6]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6));

        //단가
        objCell = objRow.createCell(7);
        objCell.setCellValue(proc.translate(titlePropsArray[7]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));

        //공사비
        objCell = objRow.createCell(8);
        objCell.setCellValue(proc.translate(titlePropsArray[8]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 8, 8));

        //실적
        objCell = objRow.createCell(9);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 10));

        //전회기성
        objCell = objRow.createCell(11);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 12));

        //금회기성
        objCell = objRow.createCell(13);
        objCell.setCellValue(proc.translate(titlePropsArray[11]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 13, 14));

        //누적기성
        objCell = objRow.createCell(15);
        objCell.setCellValue(proc.translate(titlePropsArray[12]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 15, 16));

        //잔액기성금액
        objCell = objRow.createCell(17);
        objCell.setCellValue(proc.translate(titlePropsArray[13]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 17, 18));

        objRow = objSheet.createRow(1);
        objRow.setHeight((short) 400);

        objCell = objRow.createCell(0);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(1);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(2);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(3);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(4);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(5);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(6);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(7);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(8);
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(9);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));

        objCell = objRow.createCell(10);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[15]));

        objCell = objRow.createCell(11);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));

        objCell = objRow.createCell(12);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[15]));

        objCell = objRow.createCell(13);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));

        objCell = objRow.createCell(14);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[15]));

        objCell = objRow.createCell(15);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));

        objCell = objRow.createCell(16);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[15]));

        objCell = objRow.createCell(17);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));

        objCell = objRow.createCell(18);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[15]));
    }

    private void makeGisungProcessItemCostTitle(List<ProcessItemCostDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_TOTAL);
        List<ProcessItemCostDTO> processItem = (List<ProcessItemCostDTO>) excelBodyDTOS;
        int taskDepth = 0;
        for(int i=0;i<processItem.size();i++){
            taskDepth = Math.max(taskDepth, processItem.get(i).getTaskDepth());
        }

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        for(int i=1; i<= taskDepth ; i++){
            objCell = objRow.createCell(0+i-1);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(proc.translate(titlePropsArray[0])+i);
        }

        objCell = objRow.createCell(1+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[1]));

        objCell = objRow.createCell(2+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[2]));

        objCell = objRow.createCell(3+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[3]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(4+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[4]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(5+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[5]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(6+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[6]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(7+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[7]));
        objCell = objRow.createCell(8+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(9+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 7+taskDepth-1, 9+taskDepth-1));

        objCell = objRow.createCell(10+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[8]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(11+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[9]));
        objCell.setCellStyle(objStyle);
        objCell = objRow.createCell(12+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 11+taskDepth-1, 12+taskDepth-1));

        objCell = objRow.createCell(13+taskDepth-1); // 실적
        objCell.setCellValue(proc.translate(titlePropsArray[15]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 13+taskDepth-1, 14+taskDepth-1));

        objCell = objRow.createCell(15+taskDepth-1); // 전회기성
        objCell.setCellValue(proc.translate(titlePropsArray[16]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 15+taskDepth-1, 16+taskDepth-1));

        objCell = objRow.createCell(17+taskDepth-1); // 금회기성
        objCell.setCellValue(proc.translate(titlePropsArray[17]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 17+taskDepth-1, 18+taskDepth-1));

        /**
        objCell = objRow.createCell(19+taskDepth-1); // 누적기성
        objCell.setCellValue(proc.translate(titlePropsArray[18]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 19+taskDepth-1, 20+taskDepth-1));

        objCell = objRow.createCell(21+taskDepth-1); // 잔액기성금액
        objCell.setCellValue(proc.translate(titlePropsArray[19]));
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 0, 21+taskDepth-1, 22+taskDepth-1));
         **/



        objRow = objSheet.createRow(1);
        objRow.setHeight((short) 400);

        for(int i=1; i<= taskDepth ; i++){
            objCell = objRow.createCell(0+i-1);
            objCell.setCellStyle(objStyle);
            objSheet.addMergedRegion(new CellRangeAddress(0, 1, 0+i-1, 0+i-1));
        }

        objCell = objRow.createCell(1+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 1+taskDepth-1, 1+taskDepth-1));

        objCell = objRow.createCell(2+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 2+taskDepth-1, 2+taskDepth-1));

        objCell = objRow.createCell(3+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 3+taskDepth-1, 3+taskDepth-1));

        objCell = objRow.createCell(4+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 4+taskDepth-1, 4+taskDepth-1));

        objCell = objRow.createCell(5+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 5+taskDepth-1, 5+taskDepth-1));

        objCell = objRow.createCell(6+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 6+taskDepth-1, 6+taskDepth-1));

        objCell = objRow.createCell(7+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[10]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(8+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[11]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(9+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[12]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(10+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objSheet.addMergedRegion(new CellRangeAddress(0, 1, 10+taskDepth-1, 10+taskDepth-1));

        objCell = objRow.createCell(11+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[13]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(12+taskDepth-1);
        objCell.setCellValue(proc.translate(titlePropsArray[14]));
        objCell.setCellStyle(objStyle);



        objCell = objRow.createCell(13+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[21]));

        objCell = objRow.createCell(14+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[20]));

        objCell = objRow.createCell(15+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[21]));

        objCell = objRow.createCell(16+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[20]));

        objCell = objRow.createCell(17+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[21]));

        objCell = objRow.createCell(18+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[20]));
        /**
        objCell = objRow.createCell(19+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[21]));

        objCell = objRow.createCell(20+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[20]));

        objCell = objRow.createCell(21+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[21]));

        objCell = objRow.createCell(22+taskDepth-1);
        objCell.setCellStyle(objStyle);
        objCell.setCellValue(proc.translate(titlePropsArray[20]));
         **/
    }

    private void makeGisungProcessItemCostDetailGcodeTitle(List<GisungProcessItemCostDetailDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook){
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE);
        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetail = (List<GisungProcessItemCostDetailDTO>) excelBodyDTOS;

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 400);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        objCell = objRow.createCell(0); // G-code
        objCell.setCellValue(proc.translate(titlePropsArray[0]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(1); // 수량
        objCell.setCellValue(proc.translate(titlePropsArray[1]));
        objCell.setCellStyle(objStyle);

        objCell = objRow.createCell(2); // 목표수량
        objCell.setCellValue(proc.translate(titlePropsArray[2]));
        objCell.setCellStyle(objStyle);
    }

    private void makeGisungProcessItemCostDetailTitleInGisungProcessItemTotal(List<ProcessItemCostDTO> excelBodyDTOS, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell, XSSFWorkbook xssfWorkbook) {
        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST_TOTAL);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 0x150);

        XSSFCellStyle objStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("나눔고딕");
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        objStyle.setFont(headerFont);
        objStyle.setBorderBottom(BorderStyle.THIN);
        objStyle.setBorderTop(BorderStyle.THIN);
        objStyle.setBorderLeft(BorderStyle.THIN);
        objStyle.setBorderRight(BorderStyle.THIN);
        objStyle.setAlignment(HorizontalAlignment.CENTER);
        objStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 배경색
        ((XSSFCellStyle)objStyle).setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 51, 102),null));
        objStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(int idx = 0; idx < titlePropsArray.length; idx++ ) {
            objCell = objRow.createCell(idx);
            objCell.setCellStyle(objStyle);
            objCell.setCellValue(proc.translate(titlePropsArray[idx]));
        }
    }

    private void makeExcelTitle(ExcelDownloadCaseType excelDownloadCaseType, SXSSFSheet objSheet, SXSSFRow objRow, SXSSFCell objCell) {

        String[] titlePropsArray = getTitlePropsArrayByExcelDownloadCateType(excelDownloadCaseType);

        objRow = objSheet.createRow(0);
        objRow.setHeight((short) 0x150);

        for(int idx = 0; idx < titlePropsArray.length; idx++ ) {
            objCell = objRow.createCell(idx);
            objCell.setCellValue(proc.translate(titlePropsArray[idx]));
        }
    }

    private void makeResponseHeader(HttpServletResponse response, String fileName) {
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "_" + Utils.getSaveFileNameDate() + ".xlsx");
    }

    private String getIssuePriorityText(int priority){
        if(priority == 10) return proc.translate("co_work.issue_list.list_priority_10");
        if(priority == 20) return proc.translate("co_work.issue_list.list_priority_20");
        if(priority == 30) return proc.translate("co_work.issue_list.list_priority_30");
        return proc.translate("co_work.issue_list.list_priority_40");
    }
}
