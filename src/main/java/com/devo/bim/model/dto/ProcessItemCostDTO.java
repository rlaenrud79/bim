package com.devo.bim.model.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.enumulator.TaskType;

import lombok.Data;

@Data
public class ProcessItemCostDTO {
    private String id; // gridId 전용 : 그리드 row 객체를 data로 사용할 때 넘어옴
    private String rowState;
    private long processItemId;
    private long processParentItemId = 0L;
    private long parentRowNum;
    private TaskType ganttTaskType;
    private String taskName;
    private String phasingCode;
    private int duration;
    private int taskDepth;
    private String startDate;
    private String endDate;
    private String description;
    private BigDecimal progressRate;
    private String workName;
    private String taskFullPath;
    private String firstCountFormula;
    private BigDecimal firstCount;
    private String firstCountUnit;
    private BigDecimal complexUnitPrice;
    private BigInteger taskCost;
    private BigDecimal paidCost;

    private String calculateCostResult = "";
    private String title;
    private int taskProgress;

    private boolean isBookmark;
    private BigDecimal paidProgressRate;

    private ProcessItemCostOrigin originData;

    private String exchangeId;
    private Long progressAmount = 0L;
    private String[] variables;

    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal todayCost = Utils.getDefaultDecimal();
    private BigDecimal todayProgressRate = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidProgressRate = Utils.getDefaultDecimal();

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

    private BigDecimal cateProgressRate = Utils.getDefaultDecimal();


    public ProcessItemCostDTO(ProcessItem processItem) {
        this.rowState = "R"; //CRUD

        this.processItemId = processItem.getId();
        if (processItem.getParentId() != null && processItem.getParentId() >= 0) {
            this.processParentItemId = processItem.getParentId();
        } else {
            this.processParentItemId = 0L;
        }
        this.parentRowNum = processItem.getParentRowNum();
        this.ganttTaskType = processItem.getGanttTaskType();
        this.taskName = processItem.getTaskName();
        this.phasingCode = processItem.getPhasingCode();
        this.duration = processItem.getDuration();
        this.taskDepth = processItem.getTaskDepth();
        this.startDate = processItem.getStartDate();
        this.endDate = processItem.getEndDate();
        this.description = processItem.getDescription();
        this.progressRate = processItem.getProgressRate();
        this.workName = processItem.getWork().getWorkName();
        this.taskFullPath = Utils.isEmpty(processItem.getTaskFullPath(), "");
        this.firstCountFormula = Utils.isEmpty(processItem.getFirstCountFormula(), "");
        this.firstCount = Utils.isEmpty(processItem.getFirstCount());
        this.firstCountUnit = Utils.isEmpty(processItem.getFirstCountUnit(), "");
        this.complexUnitPrice = Utils.isEmpty(processItem.getComplexUnitPrice());
        this.taskCost = Utils.isEmpty(processItem.getCost()).toBigInteger();
        this.paidCost = Utils.isEmpty(processItem.getPaidCost());
        this.title = Utils.isEmpty(processItem.getTitle(), "");
        this.taskProgress = this.progressRate.intValue();
        this.isBookmark = processItem.isBookmark();
        this.paidProgressRate = processItem.getPaidProgressRate();

        this.originData = new ProcessItemCostOrigin(processItem);

        this.exchangeId = processItem.getExchangeIds();
        if (processItem.getProgressAmount() != null) {
            this.progressAmount = processItem.getProgressAmount();
        } else {
            this.progressAmount = 0L;
        }
        this.prevPaidCost = processItem.getPrevPaidCost();
        this.prevPaidProgressRate = processItem.getPrevPaidProgressRate();
        this.cate1 = processItem.getCate1();
        this.cate2 = processItem.getCate2();
        this.cate3 = processItem.getCate3();
        this.cate4 = processItem.getCate4();
        this.cate5 = processItem.getCate5();
        this.cate6 = processItem.getCate6();
    }

    public ProcessItemCostDTO(ProcessItem processItem
            , String cate1Name
            , String cate2Name
            , String cate3Name
            , String cate4Name
            , String cate5Name
            , BigDecimal cateProgressRate) {
        this.rowState = "R"; //CRUD

        this.processItemId = processItem.getId();
        if (processItem.getParentId() != null && processItem.getParentId() >= 0) {
            this.processParentItemId = processItem.getParentId();
        } else {
            this.processParentItemId = 0L;
        }
        this.parentRowNum = processItem.getParentRowNum();
        this.ganttTaskType = processItem.getGanttTaskType();
        this.taskName = processItem.getTaskName();
        this.phasingCode = processItem.getPhasingCode();
        this.duration = processItem.getDuration();
        this.taskDepth = processItem.getTaskDepth();
        this.startDate = processItem.getStartDate();
        this.endDate = processItem.getEndDate();
        this.description = processItem.getDescription();
        this.progressRate = processItem.getProgressRate();
        this.workName = processItem.getWork().getWorkName();
        this.taskFullPath = Utils.isEmpty(processItem.getTaskFullPath(), "");
        this.firstCountFormula = Utils.isEmpty(processItem.getFirstCountFormula(), "");
        this.firstCount = Utils.isEmpty(processItem.getFirstCount());
        this.firstCountUnit = Utils.isEmpty(processItem.getFirstCountUnit(), "");
        this.complexUnitPrice = Utils.isEmpty(processItem.getComplexUnitPrice());
        this.taskCost = Utils.isEmpty(processItem.getCost()).toBigInteger();
        this.paidCost = Utils.isEmpty(processItem.getPaidCost());
        this.title = Utils.isEmpty(processItem.getTitle(), "");
        this.taskProgress = this.progressRate.intValue();
        this.isBookmark = processItem.isBookmark();
        this.paidProgressRate = processItem.getPaidProgressRate();

        this.originData = new ProcessItemCostOrigin(processItem);

        this.exchangeId = processItem.getExchangeIds();
        if (processItem.getProgressAmount() != null) {
            this.progressAmount = processItem.getProgressAmount();
        } else {
            this.progressAmount = 0L;
        }
        this.prevPaidCost = processItem.getPrevPaidCost();
        this.prevPaidProgressRate = processItem.getPrevPaidProgressRate();
        this.cate1 = processItem.getCate1();
        this.cate2 = processItem.getCate2();
        this.cate3 = processItem.getCate3();
        this.cate4 = processItem.getCate4();
        this.cate5 = processItem.getCate5();
        this.cate6 = processItem.getCate6();
        this.cate1Name = cate1Name;
        this.cate2Name = cate2Name;
        this.cate3Name = cate3Name;
        this.cate4Name = cate4Name;
        this.cate5Name = cate5Name;
        this.cateProgressRate = cateProgressRate;
    }

    public ProcessItemCostDTO(ProcessItem processItem
            , BigDecimal todayCost
            , BigDecimal todayProgressRate) {
        this.rowState = "R"; //CRUD

        this.processItemId = processItem.getId();
        this.processParentItemId = processItem.getParentId();
        this.parentRowNum = processItem.getParentRowNum();
        this.ganttTaskType = processItem.getGanttTaskType();
        this.taskName = processItem.getTaskName();
        this.phasingCode = processItem.getPhasingCode();
        this.duration = processItem.getDuration();
        this.taskDepth = processItem.getTaskDepth();
        this.startDate = processItem.getStartDate();
        this.endDate = processItem.getEndDate();
        this.description = processItem.getDescription();
        this.progressRate = processItem.getProgressRate();
        this.workName = processItem.getWork().getWorkName();
        this.taskFullPath = Utils.isEmpty(processItem.getTaskFullPath(), "");
        this.firstCountFormula = Utils.isEmpty(processItem.getFirstCountFormula(), "");
        this.firstCount = Utils.isEmpty(processItem.getFirstCount());
        this.firstCountUnit = Utils.isEmpty(processItem.getFirstCountUnit(), "");
        this.complexUnitPrice = Utils.isEmpty(processItem.getComplexUnitPrice());
        this.taskCost = Utils.isEmpty(processItem.getCost()).toBigInteger();
        this.paidCost = Utils.isEmpty(processItem.getPaidCost());
        this.title = Utils.isEmpty(processItem.getTitle(), "");
        this.taskProgress = this.progressRate.intValue();
        this.isBookmark = processItem.isBookmark();
        this.paidProgressRate = processItem.getPaidProgressRate();

        this.originData = new ProcessItemCostOrigin(processItem);

        this.exchangeId = processItem.getExchangeIds();
        if (processItem.getProgressAmount() != null) {
            this.progressAmount = processItem.getProgressAmount();
        } else {
            this.progressAmount = 0L;
        }

        this.cost = processItem.getCost();
        this.progressRate = processItem.getProgressRate();
        this.todayCost = todayCost;
        this.todayProgressRate = todayProgressRate;
    }

    private class ProcessItemCostOrigin {
        private String firstCountFormula;
        private BigDecimal firstCount;
        private String firstCountUnit;
        private BigDecimal complexUnitPrice;

        public ProcessItemCostOrigin(ProcessItem processItem) {
            this.firstCountFormula = processItem.getFirstCountFormula();
            this.firstCount = processItem.getFirstCount();
            this.firstCountUnit = processItem.getFirstCountUnit();
            this.complexUnitPrice = processItem.getComplexUnitPrice();
        }
    }

    public ProcessItemCostDTO(String cate1
            , String cate2
            , String cate3
            , String cate4
            , String cate5
            , String cate6
            , String cate1Name
            , String cate2Name
            , String cate3Name
            , String cate4Name
            , String cate5Name) {
        this.cate1 = cate1;
        this.cate2 = cate2;
        this.cate3 = cate3;
        this.cate4 = cate4;
        this.cate5 = cate5;
        this.cate6 = cate6;
        this.cate1Name = cate1Name;
        this.cate2Name = cate2Name;
        this.cate3Name = cate3Name;
        this.cate4Name = cate4Name;
        this.cate5Name = cate5Name;
    }
}
