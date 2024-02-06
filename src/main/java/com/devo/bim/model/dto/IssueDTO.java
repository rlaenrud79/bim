package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Issue;
import com.devo.bim.model.entity.IssueManager;
import com.devo.bim.model.enumulator.IssueStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IssueDTO {

    private long id;
    private long projectId;
    private String title;
    private String contents;
    private int priority;
    private IssueStatus status;
    private AccountDTO writerDTO;
    private LocalDateTime requestDate;
    private LocalDateTime statusUpdateDate;
    private LocalDateTime writeDate;
    private List<IssueManager> issueManagers = new ArrayList<>();

    public IssueDTO(Issue issue) {
        this.id = issue.getId();
        this.projectId = issue.getProjectId();
        this.title = issue.getTitle();
        this.contents = issue.getContents();
        this.priority = issue.getPriority();
        this.status = issue.getStatus();
        this.writerDTO = new AccountDTO(issue.getWriteEmbedded().getWriter());
        this.requestDate = issue.getRequestDate();
        this.statusUpdateDate = issue.getStatusUpdateDate();
        this.writeDate = issue.getWriteEmbedded().getWriteDate();
    }

    public void addIssueManagers(List<IssueManager> issueManagers) {
        for (IssueManager issueManager : issueManagers) {
            this.issueManagers.add(issueManager);
        }
    }

    public boolean isIncludedUser(long accountId) {
        return this.issueManagers.stream().filter(t -> t.getManager().getId() == accountId).count() > 0;
    }

    public boolean isRequestOrGoing() {
        if(this.status == IssueStatus.REQUEST || this.status == IssueStatus.GOING) return true;
        return false;
    }
}
