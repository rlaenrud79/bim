package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetFile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;

    private String originFileName;
    private String filePath;

    @Embedded
    private WriteEmbedded writeEmbedded;
    private int sortNo;
    private BigDecimal size;

    public JobSheetFile(long jobSheetId, String originFileName, String filePath, int sortNo, BigDecimal size, long writerId) {
        this.jobSheet = new JobSheet(jobSheetId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }

    public void setJobSheetFileAtJobSheetCopy(JobSheet jobSheet, JobSheetFile jobSheetFile, String filePath, int sortNo) {
        this.jobSheet = jobSheet;
        this.originFileName = jobSheetFile.getOriginFileName();
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = jobSheetFile.getSize();
        this.writeEmbedded = jobSheetFile.getWriteEmbedded();
    }
}

