package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkIssueReportFile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coWorkIssueReportId")
    private CoWorkIssueReport coWorkIssueReport;

    private String originFileName;
    private String filePath;
    private LocalDateTime writeDate;
    private int sortNo;
    private BigDecimal size;

    @OneToMany(fetch = LAZY, mappedBy = "coWorkIssueReport")
    private List<CoWorkIssueReportFile> coWorkIssueReportFiles = new ArrayList<>();

    public CoWorkIssueReportFile(long coWorkIssueReportId, String originalFilename, String filePath, int sortNo, BigDecimal size) {
        this.coWorkIssueReport = new CoWorkIssueReport(coWorkIssueReportId);
        this.originFileName  = originalFilename;
        this.filePath = filePath;
        this.writeDate = LocalDateTime.now();
        this.sortNo = sortNo;
        this.size = size;
    }
}
