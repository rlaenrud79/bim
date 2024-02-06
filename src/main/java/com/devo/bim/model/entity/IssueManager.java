package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class IssueManager {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    private LocalDateTime assignDate;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="manager_id")
    Account manager;

    public IssueManager(long issueManagerId){
        this.manager = new Account(issueManagerId);
        this.assignDate = LocalDateTime.now();
    }

    public IssueManager(long issueId, long issueManagerId){
        this.issue = new Issue(issueId);
        this.manager = new Account(issueManagerId);
        this.assignDate = LocalDateTime.now();
    }

    public void setIssueAtPostIssue(long issueId){
        this.issue = new Issue(issueId);
    }

    public AccountDTO getAccountDTO(){
        return new AccountDTO(manager);
    }
}
