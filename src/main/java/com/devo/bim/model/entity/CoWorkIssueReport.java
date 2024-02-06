package com.devo.bim.model.entity;

import com.devo.bim.model.vo.CoWorkIssueReportVO;
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
public class CoWorkIssueReport {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coWorkIssueId")
    private CoWorkIssue coWorkIssue;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="coWorkJoinerId")
    private Account coWorkJoiner;

    private String contents;
    private LocalDateTime handleDate;

    @OneToMany(fetch = LAZY, mappedBy = "coWorkIssueReport")
    List<CoWorkIssueReportFile> coWorkIssueReportFiles = new ArrayList<>();

    public CoWorkIssueReport(long coWorkIssueReportId) {
        this.id= coWorkIssueReportId;
    }

    public CoWorkIssueReport(CoWorkIssueReportVO coWorkIssueReportVO, long accountId) {
        this.coWorkIssue = new CoWorkIssue().setId(coWorkIssueReportVO.getCoWorkIssueId());
        this.contents = coWorkIssueReportVO.getContents();
        this.coWorkJoiner = new Account(accountId);
        this.handleDate = LocalDateTime.now();
    }
}
