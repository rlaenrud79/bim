package com.devo.bim.model.dto;

import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.AlertType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MainIssueNoticeDTO {
    private long id;
    private long projectId;
    private String title;
    private String contents;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private String dtoType;
    private List<Long> workAccountIds = new ArrayList<>();

    public MainIssueNoticeDTO(Issue issue){
        this.id = issue.getId();
        this.projectId = issue.getProjectId();
        this.title = issue.getTitle();
        this.contents = issue.getContents();
        this.writerDTO = new AccountDTO(issue.getWriteEmbedded().getWriter());
        this.writeDate = issue.getWriteEmbedded().getWriteDate();
        this.dtoType = "ISSUE";
    }

    public MainIssueNoticeDTO(CoWorkIssue coWorkIssue, long projectId){
        this.id = coWorkIssue.getId();
        this.projectId = projectId;
        this.title = coWorkIssue.getTitle();
        this.contents = coWorkIssue.getContents();
        this.writerDTO = new AccountDTO(coWorkIssue.getWriteEmbedded().getWriter());
        this.writeDate = coWorkIssue.getWriteEmbedded().getWriteDate();
        this.dtoType = "CO_WORK_ISSUE";
    }

    public MainIssueNoticeDTO(Notice notice){
        this.id = notice.getId();
        this.projectId = notice.getProjectId();
        this.title = notice.getTitle();
        this.contents = notice.getContents();
        this.writerDTO = new AccountDTO(notice.getWriteEmbedded().getWriter());
        this.writeDate = notice.getWriteEmbedded().getWriteDate();
        this.dtoType = "NOTICE";

        for (Work work : notice.getWorks()) {
            for (Account account : work.getAccounts()) {
                this.workAccountIds.add(account.getId());
            }
        }
    }

    public boolean isNew(){
        return ChronoUnit.DAYS.between(this.writeDate, LocalDateTime.now()) < 1;
    }

    public boolean isNewTab(){
        if("CO_WORK_ISSUE".equalsIgnoreCase(this.dtoType)) return true;
        return false;
    }
}
