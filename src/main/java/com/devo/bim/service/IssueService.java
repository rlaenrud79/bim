package com.devo.bim.service;

import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.dto.IssueDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Issue;
import com.devo.bim.model.entity.IssueManager;
import com.devo.bim.model.entity.IssueReport;
import com.devo.bim.model.entity.IssueReportFile;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.IssueStatus;
import com.devo.bim.model.vo.SearchIssueVO;
import com.devo.bim.repository.dsl.IssueDTODslRepository;
import com.devo.bim.repository.spring.IssueManagerRepository;
import com.devo.bim.repository.spring.IssueReportFileRepository;
import com.devo.bim.repository.spring.IssueReportRepository;
import com.devo.bim.repository.spring.IssueRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService extends AbstractService {

    private final AccountService accountService;
    private final IssueDTODslRepository issueDTODslRepository;

    private final IssueRepository issueRepository;
    private final IssueManagerRepository issueManagerRepository;
    private final IssueReportRepository issueReportRepository;
    private final IssueReportFileRepository issueReportFileRepository;
    private final AlertService alertService;
    private final FileDeleteService fileDeleteService;

    public PageDTO<IssueDTO> findIssueDTOsBySearchCondition(SearchIssueVO searchIssueVO, Pageable pageable){
        return issueDTODslRepository.findIssueDTOs(searchIssueVO, userInfo.getId(), userInfo.isRoleAdminProject(), pageable);
    }

    public List<IssueDTO> findIssueDTOsBySearchCondition(SearchIssueVO searchIssueVO){
        return issueDTODslRepository.findIssueDTOs(searchIssueVO, userInfo.getId(), userInfo.isRoleAdminProject());
    }

    public Issue findById(long issueId){
        Issue savedIssue = issueRepository.findByIdAndProjectId(issueId, userInfo.getProjectId()).orElseGet(Issue::new);
        if(savedIssue.getId() == 0) throw new NotFoundException(proc.translate("system.common.exception_not_found"));
        if(!haveRightForViewAndAddReport(savedIssue)) throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        return savedIssue;
    }

    public boolean haveRightForViewAndAddReport(Issue issue){
        if(userInfo.isRoleAdminProject()) return true;
        if(issue.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        if(issue.getIssueManagers().stream().filter(s -> s.getManager().getId() == userInfo.getId()).count() > 0) return true;
        return false;
    }

    private boolean haveRightForDelete(Issue issue){
        if(userInfo.isRoleAdminProject()) return true;
        if(issue.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }

    private boolean haveRightForUpdate(Issue issue){
        if(userInfo.isRoleAdminProject()) return true;
        if(issue.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }

    @Transactional
    public JsonObject postIssue(Issue issue, List<IssueManager> issueManagers){
        try {
            Issue savedIssue = issueRepository.save(issue);

            for (IssueManager issueManager : issueManagers) {

                // issueManager 저장
                saveIssueManager(savedIssue.getId(), issueManager);

                // alert 저장
                if(savedIssue.getStatus() != IssueStatus.WRITE)
                    saveAlert(issue.getTitle(), savedIssue.getId(), issueManager.getManager().getId(), "INSERT");
            }

            return proc.getResult(true, "system.issue_service.post_issue");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putIssue(Issue issue, List<IssueManager> issueManagers){
        // 1. 이슈 존재여부
        Issue savedIssue = issueRepository.findByIdAndProjectId(issue.getId(), userInfo.getProjectId()).orElseGet(Issue::new);
        if(savedIssue.getId() == 0 ) return proc.getResult(false, "system.issue_service.update_issue.no_issue");

        // 2. 이슈 수정 권한 체크
        if(!haveRightForUpdate(savedIssue)) return proc.getResult(false, "system.issue_service.update_issue.no_have_right_update");

        try {
            IssueStatus savedIssueStatus = savedIssue.getStatus();

            // 3. issue 수정
            savedIssue.setIssueAtPutIssue(issue);

            // 4. 기존 issue_managers 모두 삭제
            deleteSavedIssueManager(savedIssue);

            // 5. 기존 alert 모두 disabled 처리
            setDisabledPreviousAlert(savedIssue);

            // 6. 신규 issueManagers 다시 추가 및 alert 추가
            issueManagers.forEach( t-> {
                saveNewIssueManager(t);
                if(savedIssue.getStatus() != IssueStatus.WRITE)
                    saveAlert(savedIssue.getTitle(), savedIssue.getId(), t.getManager().getId(), "UPDATE");

            });

            return proc.getResult(true, "system.issue_service.update_issue.success_issue_update");
        }
        catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void saveNewIssueManager(IssueManager t) {
        issueManagerRepository.save(t);
    }

    private void saveAlert(String title, long savedIssueId, long accountId, String actionType) {
        alertService.insertAlertForIssue(userInfo.getProjectId(), accountId, title, savedIssueId, actionType);
    }

    private void saveIssueManager(long savedIssueId, IssueManager issueManager) {
        issueManager.setIssueAtPostIssue(savedIssueId);
        saveNewIssueManager(issueManager);
    }

    private void deleteSavedIssueManager(Issue savedIssue) {
        savedIssue.getIssueManagers().forEach(t -> {
            issueManagerRepository.delete(t);
        });
    }

    private void setDisabledPreviousAlert(Issue issue) {
        alertService.setDisabledPreviousAlert(userInfo.getProjectId(), issue.getId(), AlertType.ISSUE);
    }

    private void setDisabledAlert(Issue issue) {
        alertService.setDisabledAlert(userInfo.getProjectId(), issue.getId(), AlertType.ISSUE);
    }

    @Transactional
    public JsonObject deleteIssue(long issueId){
        // 1. 이슈 존재여부
        Issue savedIssue = issueRepository.findByIdAndProjectId(issueId, userInfo.getProjectId()).orElseGet(Issue::new);
        if(savedIssue.getId() == 0 ) return proc.getResult(false, "system.issue_service.delete_issue.no_issue");

        // 2. 이슈 삭제 권한 체크
        if(!haveRightForDelete(savedIssue)) return proc.getResult(false, "system.issue_service.delete_issue.no_have_right_delete");

        try {
            // 3. issue_report & issue_report_file 삭제
            deleteIssueReportAndFile(savedIssue);

            // 4. issue_Manager 삭제
            deleteIssueManagerDB(savedIssue);

            // 5. issue삭제
            deleteIssueDB(savedIssue);
            
            // 6. alert disabled 처리
            setDisabledAlert(savedIssue);

            return proc.getResult(true, "system.issue_service.delete_issue.success_issue_delete");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void deleteIssueReportAndFile(Issue savedIssue) {
        if (savedIssue.getIssueReports().size() > 0) {
            for (IssueReport issueReport : savedIssue.getIssueReports()) {

                // 1. Issue_Report_file 삭제
                deleteIssueReportFile(issueReport);

                // 2. Issue_Report 삭제
                deleteIssueReportDB(issueReport);
            }
        }
    }

    private void deleteIssueDB(Issue savedIssue) {
        issueRepository.delete(savedIssue);
    }

    private void deleteIssueManagerDB(Issue savedIssue) {
        if(savedIssue.getIssueManagers().size() > 0) {
            deleteSavedIssueManager(savedIssue);
        }
    }

    private void deleteIssueReportDB(IssueReport issueReport) {
        issueReportRepository.delete(issueReport);
    }

    private void deleteIssueReportFile(IssueReport issueReport) {
        if (issueReport.getIssueReportFiles().size() > 0) {
            issueReport.getIssueReportFiles().forEach(t -> {

                // 1. issue_report_file 실제 파일 삭제
                fileDeleteService.deletePhysicalFile(t.getFilePath());

                // 2. issue_report_file DB 삭제
                deleteIssueReportFileDB(t);
            });
        }
    }

    private void deleteIssueReportFileDB(IssueReportFile t) {
        issueReportFileRepository.delete(t);
    }
}
