package com.devo.bim.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.transaction.Transactional;

import com.devo.bim.repository.dsl.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.CoWorkSnapShotFile;
import com.devo.bim.model.entity.IssueReportFile;
import com.devo.bim.model.entity.ModelingDownloadLog;
import com.devo.bim.model.entity.NoticeFile;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.repository.spring.CoWorkSnapShotFileRepository;
import com.devo.bim.repository.spring.IssueReportFileRepository;
import com.devo.bim.repository.spring.ModelingDownloadLogRepository;
import com.devo.bim.repository.spring.NoticeFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDownloadService extends AbstractService {

    private final AccountService accountService;
    private final ModelingDslRepository modelingDslRepository;
    private final ModelingDownloadLogRepository modelingDownloadLogRepository;
    private final MySnapShotFileDslRepository mySnapShotFileDslRepository;
    private final CoWorkSnapShotFileDslRepository coWorkSnapShotFileDslRepository;
    private final CoWorkSnapShotFileRepository coWorkSnapShotFileRepository;
    private final CoWorkIssueReportFileDslRepository coWorkIssueReportFileDslRepository;
    private final IssueReportFileDslRepository issueReportFileDslRepository;
    private final IssueReportFileRepository issueReportFileRepository;
    private final JobSheetFileDslRepository jobSheetFileDslRepository;
    private final NoticeFileDslRepository noticeFileDslRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final BulletinFileDslRepository bulletinFileDslRepository;
    private final DocumentFileDslRepository documentFileDslRepository;
    private final GisungReportDTODslRepository gisungReportDTODslRepository;
    private final GisungExcelFileDslRepository gisungExcelFileDslRepository;
    private final GisungListExcelFileDslRepository gisungListExcelFileDslRepository;
    private final GisungPaymentFileDslRepository gisungPaymentFileDslRepository;
    private final ProcessItemExcelFileDslRepository processItemExcelFileDslRepository;

    // 여기에 다운로드 파일 종류별 조회 쿼리 추가
    public FileDownloadInfoDTO findByIdAndFileDownloadUIType(long id, FileDownloadUIType fileDownloadUIType) {
        if (fileDownloadUIType == FileDownloadUIType.MODELING_FILE) return modelingDslRepository.findModelFileById(userInfo.getProjectId(), id);
        if (fileDownloadUIType == FileDownloadUIType.MODELING_IFC_FILE) return modelingDslRepository.findModelIfcFileById(userInfo.getProjectId(), id);
        if (fileDownloadUIType == FileDownloadUIType.MODELING_SNAP_SHOT_MODEL_FILE) return mySnapShotFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.CO_WORK_SNAP_SHOT_MODEL_FILE) return coWorkSnapShotFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.CO_WORK_ISSUE_REPORT_FILE) return coWorkIssueReportFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.ISSUE_REPORT_FILE) return issueReportFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.JOB_SHEET_FILE) return jobSheetFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.NOTIFICATION_FILE) return noticeFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.BULLETIN_FILE) return bulletinFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.DOCUMENT_FILE) return documentFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE) return gisungReportDTODslRepository.findFileSurveyDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_PART_SURVEY_FILE) return gisungReportDTODslRepository.findFilePartSurveyDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_AGGREGATE_FILE) return gisungReportDTODslRepository.findFileAggregateDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_PART_AGGREGATE_FILE) return gisungReportDTODslRepository.findFilePartAggregateDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_ACCOUNT_FILE) return gisungReportDTODslRepository.findFileAccountDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_ETC_FILE) return gisungReportDTODslRepository.findFileEtcDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_EXCEL_FILE) return gisungExcelFileDslRepository.findFileDownloadInfoDTOById();
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_PAYMENT_FILE) return gisungPaymentFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_LIST_EXCEL_FILE) return gisungListExcelFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.PROCESS_ITEM_COST_DETAIL_EXCEL_FILE) return gisungListExcelFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.PROCESS_ITEM_EXCEL_FILE) return processItemExcelFileDslRepository.findFileDownloadInfoDTOById(id);
        if (fileDownloadUIType == FileDownloadUIType.PROCESS_TEMPLATE) return new FileDownloadInfoDTO(0, 0
                                                                                                        , FileDownloadUIType.PROCESS_TEMPLATE, ""
                                                                                                        , "process_template.xlsx"
                                                                                                        , "/dist/file/process_template.xlsx"
                                                                                                        , new BigDecimal("0.00"), 5);
        return new FileDownloadInfoDTO();
    }

    @Transactional
    // 필요시 여기에 다운로드 파일 로그 정보 종류별 조회 쿼리 추가
    public void postFileDownloadLog(long tableId, FileDownloadUIType fileDownloadUIType) {
        if (fileDownloadUIType == FileDownloadUIType.MODELING_FILE) postFileDownloadLogModel("CAD", tableId, userInfo.getId());
        if (fileDownloadUIType == FileDownloadUIType.MODELING_IFC_FILE) postFileDownloadLogModel("IFC", tableId, userInfo.getId());
    }

    // 여기에 다운로드 파일 종류별 권한 체크 추가
    public boolean checkRightFileDownload(FileDownloadInfoDTO fileDownloadInfoDTO) {
        FileDownloadUIType fileDownloadUIType = fileDownloadInfoDTO.getFileDownloadUIType();

        if (fileDownloadInfoDTO.getFileDownloadUIType() == FileDownloadUIType.MODELING_FILE) return checkBIMModelingFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.MODELING_IFC_FILE) return checkBIMModelingFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.MODELING_SNAP_SHOT_MODEL_FILE) return haveFileDownloadWriterRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.CO_WORK_SNAP_SHOT_MODEL_FILE) return haveCoWorkSnapShotFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.CO_WORK_ISSUE_REPORT_FILE) return haveCoWorkIssueReportFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.ISSUE_REPORT_FILE) return haveIssueReportFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.JOB_SHEET_FILE) return haveJobSheetFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.NOTIFICATION_FILE) return haveNoticeFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.BULLETIN_FILE) return haveBulletinFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.DOCUMENT_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_PART_SURVEY_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_AGGREGATE_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_PART_AGGREGATE_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_ACCOUNT_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_REPORT_ETC_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.GISUNG_PAYMENT_FILE) return haveDocumentFileDownloadRight(fileDownloadInfoDTO);
        if (fileDownloadUIType == FileDownloadUIType.PROCESS_TEMPLATE) return haveProcessTemplateFileDownloadRight();

        return false;
    }

    public String getSaveBasePath() {
        return Utils.getSaveBasePath(winPathUpload, linuxPathUpload, macPathUpload);
    }

    public String getDownloadBasePath() {
        return Utils.getDownloadBasePath(winPathUpload, linuxPathUpload, macPathUpload);
    }

    private void postFileDownloadLogModel(String fileType, long modelId, long writerId) {
        ModelingDownloadLog modelingDownloadLog = new ModelingDownloadLog(fileType, modelId, writerId);
        modelingDownloadLogRepository.save(modelingDownloadLog);
    }

    @NotNull
    public Resource getFileResource(Path path) throws IOException {
        return new InputStreamResource(Files.newInputStream(path));
    }

    // BIM 모델관리 : 작성자와 전체 프로젝트 관리자와 공종에 속한 사람들만 다운로드 가능
    private boolean checkBIMModelingFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        Account loginAccount = accountService.findLoginAccount();
        if (haveFileDownloadWriterRight(fileDownloadInfoDTO)) return true;
        if (userInfo.isRoleAdminProject()) return true;
        if (loginAccount.getWorks().stream().filter(t -> t.getId() == fileDownloadInfoDTO.getWorkId()).count() > 0) return true;
        return false;
    }

    // 파일 작성자와 세션 유저가 동일한지 체크
    private boolean haveFileDownloadWriterRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return userInfo.getId() == fileDownloadInfoDTO.getWriterId();
    }

    private boolean haveCoWorkSnapShotFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        if (haveFileDownloadWriterRight(fileDownloadInfoDTO)) return true;

        CoWorkSnapShotFile coWorkSnapShotFile = coWorkSnapShotFileRepository.findCoWorkSnapShotById(fileDownloadInfoDTO.getId());
        return coWorkSnapShotFile.getCoWorkSnapShot().getCoWork().isJoiner(userInfo.getId());
    }

    private boolean haveCoWorkIssueReportFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return true;
    }

    private boolean haveIssueReportFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        if (haveFileDownloadWriterRight(fileDownloadInfoDTO)) return true;
        if (userInfo.isRoleAdminProject()) return true;
        if (issueReportFileRepository.findById(fileDownloadInfoDTO.getId())
                .orElseGet(IssueReportFile::new)
                .getIssueReport()
                .getIssue()
                .getIssueManagers()
                .stream()
                .filter(s -> s.getManager().getId() == userInfo.getId())
                .count() > 0) return true;
        return false;
    }

    private boolean haveJobSheetFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return true;
    }

    private boolean haveNoticeFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        if (haveFileDownloadWriterRight(fileDownloadInfoDTO)) return true;
        if (userInfo.isRoleAdminProject()) return true;
        if (accountService.haveWorks(getNoticeWorksOfNoticeFile(fileDownloadInfoDTO))) return true;
        return false;
    }

    private boolean haveBulletinFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return true;
    }

    private boolean haveDocumentFileDownloadRight(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return true;
    }

    private boolean haveProcessTemplateFileDownloadRight(){
        if (userInfo.isRoleAdminProject()) return true;
        if (userInfo.isRoleAdminProcess()) return true;
        return false;
    }

    private List<Work> getNoticeWorksOfNoticeFile(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return noticeFileRepository.findById(fileDownloadInfoDTO.getId()).orElseGet(NoticeFile::new).getNotice().getWorks();
    }
}
