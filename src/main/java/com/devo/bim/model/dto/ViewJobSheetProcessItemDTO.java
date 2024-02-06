package com.devo.bim.model.dto;

import com.devo.bim.model.entity.JobSheet;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ViewJobSheetProcessItem;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
public class ViewJobSheetProcessItemDTO {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private long id;
    private LocalDateTime reportDate;
    private String taskName;
    private String taskFullPath;
    private long parentId;
    private String parentTaskFullPath;
    private BigDecimal cost = new BigDecimal(BigInteger.ZERO);
    private long beforeProgressAmount;
    private BigDecimal beforeProgressRate = new BigDecimal(BigInteger.ZERO);
    private long todayProgressAmount;
    private BigDecimal todayProgressRate = new BigDecimal(BigInteger.ZERO);
    private long afterProgressAmount;
    private BigDecimal afterProgressRate = new BigDecimal(BigInteger.ZERO);
    private String totalChk;
    private BigDecimal yearProgressRate = new BigDecimal(BigInteger.ZERO);

    private String exchangeId;
    private String phasingCode;
    private long mySnapShotId = 0;
    private String mySnapShotSource;
    private String startDate;
    private String endDate;
    private int duration;
    private String cate1;
    private String cate2;
    private String cate3;
    private String cate4;
    private String cate5;
    private String cate6;
    private String cate1Name;
    private String cate2Name;
    private String cate3Name;
    private String cate4Name;
    private String cate5Name;
    private String cate6Name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> worker;
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> device;

    public ViewJobSheetProcessItemDTO(ViewJobSheetProcessItem viewJobSheetProcessItem, LocalDateTime reportDate){
        this.id = viewJobSheetProcessItem.getId();
        this.jobSheet = viewJobSheetProcessItem.getJobSheet();
        this.processItem = viewJobSheetProcessItem.getProcessItem();
        this.beforeProgressRate = viewJobSheetProcessItem.getBeforeProgressRate();
        this.afterProgressRate = viewJobSheetProcessItem.getAfterProgressRate();
        this.todayProgressRate = viewJobSheetProcessItem.getTodayProgressRate();
        this.beforeProgressAmount = viewJobSheetProcessItem.getBeforeProgressAmount();
        this.afterProgressAmount = viewJobSheetProcessItem.getAfterProgressAmount();
        this.todayProgressAmount = viewJobSheetProcessItem.getTodayProgressAmount();
        this.worker = viewJobSheetProcessItem.getWorker();
        this.device = viewJobSheetProcessItem.getDevice();
        this.mySnapShotId = viewJobSheetProcessItem.getMySnapShotId();
        this.mySnapShotSource = viewJobSheetProcessItem.getMySnapShotSource();
        this.taskName = viewJobSheetProcessItem.getTaskName();
        this.phasingCode = viewJobSheetProcessItem.getPhasingCode();
        this.startDate = viewJobSheetProcessItem.getStartDate();
        this.duration = viewJobSheetProcessItem.getDuration();
        this.endDate = viewJobSheetProcessItem.getEndDate();
        this.taskFullPath = viewJobSheetProcessItem.getTaskFullPath();
        this.exchangeId = viewJobSheetProcessItem.getExchangeIds();
        this.cost = viewJobSheetProcessItem.getCost();
        this.parentId = viewJobSheetProcessItem.getParentId();
        this.cate1 = viewJobSheetProcessItem.getCate1();
        this.cate2 = viewJobSheetProcessItem.getCate2();
        this.cate3 = viewJobSheetProcessItem.getCate3();
        this.cate4 = viewJobSheetProcessItem.getCate4();
        this.cate5 = viewJobSheetProcessItem.getCate5();
        this.cate6 = viewJobSheetProcessItem.getCate6();
        this.cate1Name = viewJobSheetProcessItem.getCate1Name();
        this.cate2Name = viewJobSheetProcessItem.getCate2Name();
        this.cate3Name = viewJobSheetProcessItem.getCate3Name();
        this.cate4Name = viewJobSheetProcessItem.getCate4Name();
        this.cate5Name = viewJobSheetProcessItem.getCate5Name();
        this.cate6Name = viewJobSheetProcessItem.getCate6Name();
        this.reportDate = reportDate;
    }
}
