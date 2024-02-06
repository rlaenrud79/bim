package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class IssueReport {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reporter_id")
    Account reporter;

    private String contents;
    private LocalDateTime handleDate;

    @OneToMany(fetch = LAZY, mappedBy = "issueReport")
    private List<IssueReportFile> issueReportFiles = new ArrayList<>();

    public IssueReport(long issueReportId){
        this.id = issueReportId;
    }

    public IssueReport(long issueId, String contents, long userId){
        this.issue = new Issue(issueId);
        this.contents = contents;
        this.reporter = new Account(userId);
        this.handleDate = LocalDateTime.now();
    }

    public AccountDTO getAccountDTO(){
        return new AccountDTO(this.reporter);
    }
}
