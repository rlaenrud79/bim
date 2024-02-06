package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.IssueStatus;
import com.devo.bim.model.vo.IssueVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Issue extends ObjectModelHelper<Issue> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String title;
    private long projectId;
    private String contents;

    private LocalDateTime requestDate;

    @Enumerated(STRING)
    private IssueStatus status;

    private LocalDateTime statusUpdateDate;
    private int priority;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "issue")
    private List<IssueManager> issueManagers = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "issue")
    private List<IssueReport> issueReports = new ArrayList<>();

    public Issue(long issueId){
        this.id = issueId;
    }

    public Issue(String title, long projectId, String contents, LocalDateTime requestDate, IssueStatus status, int priority, long writerId){
        this.title = title;
        this.projectId = projectId;
        this.contents = contents;
        this.requestDate = requestDate;
        this.status = status;
        this.statusUpdateDate = LocalDateTime.now();
        this.priority = priority;
        this.writeEmbedded = new WriteEmbedded(writerId);
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
    }

    public Issue(IssueVO issueVO){

        if(issueVO.getIssueId() != 0) this.id = issueVO.getIssueId();
        this.title = issueVO.getTitle();
        this.projectId = issueVO.getProjectId();
        this.contents = issueVO.getContents();
        if(StringUtils.isEmpty(issueVO.getRequestDate() )) this.requestDate = null;
        else this.requestDate = LocalDateTime.parse(issueVO.getRequestDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = IssueStatus.valueOf(issueVO.getStatus());
        this.statusUpdateDate = LocalDateTime.now();
        this.priority = issueVO.getPriority();
        this.writeEmbedded = new WriteEmbedded(issueVO.getUserId());
        this.lastModifyEmbedded = new LastModifyEmbedded(issueVO.getUserId());
    }

    // 리턴값을 언어팩 적용해야 함.
    public String getPriorityCode(){
        return Utils.getIssuePriorityCode(this.priority);
    }

    public void setIssueAtPutIssue(Issue issue) {
        if(issue.getId() != 0) this.id = issue.getId();
        this.title = issue.getTitle();
        if(issue.getProjectId() > 0) this.projectId = issue.getProjectId();
        this.contents = issue.getContents();
        this.requestDate = issue.getRequestDate();
        if(this.status != issue.getStatus()) {
            this.status = issue.getStatus();
            this.statusUpdateDate = LocalDateTime.now();
        }
        this.priority = issue.getPriority();
        this.lastModifyEmbedded = new LastModifyEmbedded(issue.getLastModifyEmbedded().getLastModifier().getId());
    }

    public boolean isUpdate(long accountId){
        if(this.writeEmbedded.getWriter().getId() == accountId
                && (this.status == IssueStatus.WRITE || this.status == IssueStatus.REQUEST || this.status == IssueStatus.GOING)) return true;
        return false;
    }
}
