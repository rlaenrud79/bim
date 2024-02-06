package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class IssueReportFile extends ObjectModelHelper<IssueReportFile> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "issue_report_id")
    private IssueReport issueReport;

    private String originFileName;
    private String filePath;
    private LocalDateTime writeDate;
    private int sortNo;
    private BigDecimal size;

    public IssueReportFile(long issueReportId, String originFileName, String filePath, int sortNo, BigDecimal size){
        this.issueReport = new IssueReport(issueReportId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
    }

    public boolean isImage(){
        return Utils.isImage(this.originFileName);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }

}
