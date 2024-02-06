package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class JobSheetProcessItemCost {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private long projectId;
    private long workId;

    private String phasingCode;

    @Embedded
    private WriteEmbedded writeEmbedded;

    private LocalDateTime reportDate;

    @Enumerated(STRING)
    private JobSheetStatus status;

    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;

    public void setJobSheetProcessItemCostAtAddJobSheet(UserInfo userInfo, ProcessItem processItem, JobSheet jobSheet, BigDecimal beforeProgressRate, BigDecimal afterProgressRate, BigDecimal todayProgressRate, long beforeProgressAmount, long afterProgressAmount, long todayProgressAmount) {
        this.projectId = userInfo.getProjectId();
        this.jobSheet = jobSheet;
        this.processItem = processItem;
        this.phasingCode = processItem.getPhasingCode();
        this.workId = processItem.getWork().getId();
        this.reportDate = jobSheet.getReportDate();
        this.status = jobSheet.getStatus();
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }
}
