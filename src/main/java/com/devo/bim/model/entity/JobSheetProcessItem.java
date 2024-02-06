package com.devo.bim.model.entity;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class JobSheetProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private long mySnapShotId = 0;
    private String phasingCode;
    private String taskFullPath;
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private long grantorId;
    private LocalDateTime grantDate;
    private String exchangeId;
    private String mySnapShotSource;
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> worker;
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> device;
    private long gisungId = 0;

    public void setJobSheetProcessItemAtAddJobSheet(JobSheet jobSheet, ProcessItem processItem, String phasingCode, String taskFullPath, BigDecimal beforeProgressRate, BigDecimal afterProgressRate, BigDecimal todayProgressRate, long beforeProgressAmount, long afterProgressAmount, long todayProgressAmount, Long grantorId, List<Object> worker, List<Object> device, String exchangeId, long mySnapShotId, String mySnapShotSource) {
        this.jobSheet = jobSheet;
        this.processItem = processItem;
        this.phasingCode = phasingCode;
        this.taskFullPath = taskFullPath;
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.grantorId = grantorId;
        this.grantDate = LocalDateTime.now();
        this.worker = worker;
        this.device = device;
        this.exchangeId = exchangeId;
        this.mySnapShotId = mySnapShotId;
        this.mySnapShotSource = mySnapShotSource;
    }

    public void setJobSheetProcessItemAtAddJobSheet(JobSheet jobSheet, ProcessItem processItem, String phasingCode, String taskFullPath, BigDecimal beforeProgressRate, BigDecimal afterProgressRate, BigDecimal todayProgressRate, long beforeProgressAmount, long afterProgressAmount, long todayProgressAmount, Long grantorId, List<Object> worker, List<Object> device, String exchangeId, long mySnapShotId, String mySnapShotSource, long gisungId) {
        this.jobSheet = jobSheet;
        this.processItem = processItem;
        this.phasingCode = phasingCode;
        this.taskFullPath = taskFullPath;
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.grantorId = grantorId;
        this.grantDate = LocalDateTime.now();
        this.worker = worker;
        this.device = device;
        this.exchangeId = exchangeId;
        this.mySnapShotId = mySnapShotId;
        this.mySnapShotSource = mySnapShotSource;
        this.gisungId = gisungId;
    }

    public void setWorker(List<Object> worker) {
        this.worker = worker;
    }
    public void setMySnapShotId(long mySnapShotId) {
        this.mySnapShotId = mySnapShotId;
    }
    public void setMySnapShotSource(String mySnapShotSource) {
        this.mySnapShotSource = mySnapShotSource;
    }

}
