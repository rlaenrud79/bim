package com.devo.bim.service;

import com.devo.bim.model.entity.Issue;
import com.devo.bim.model.entity.IssueReport;
import com.devo.bim.model.vo.CoWorkIssueReportVO;
import com.devo.bim.model.vo.IssueReportVO;
import com.devo.bim.model.vo.IssueVO;
import com.devo.bim.repository.spring.IssueReportRepository;
import com.devo.bim.repository.spring.IssueRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueReportService extends AbstractService {

    private final IssueService issueService;
    private final IssueRepository issueRepository;
    private final IssueReportRepository issueReportRepository;

    @Transactional
    public JsonObject postIssueReport(IssueReportVO issueReportVO){

        // 이슈 조회
        Issue savedIssue = issueRepository.findByIdAndProjectId(issueReportVO.getIssueId(), userInfo.getProjectId()).orElseGet(Issue::new);
        if(savedIssue.getId() == 0)
            return proc.getResult(false, "system.issue_report_service.post_issue_report.no_issue");

        // 등록 권한 체크
        if(!issueService.haveRightForViewAndAddReport(savedIssue))
            return proc.getResult(false, "system.issue_report_service.post_issue_report.no_add_right_issue_report");

        try{
            // Issue Report 객체 생성
            IssueReport issueReport = new IssueReport(issueReportVO.getIssueId(), issueReportVO.getContents(), userInfo.getId());
            long issueReportId = issueReportRepository.save(issueReport).getId();

            return proc.getResult(true, issueReportId, "");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
