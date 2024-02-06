package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.GisungCompareResult;
import lombok.Data;

import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.math.BigInteger;

import static javax.persistence.EnumType.STRING;

@Data
public class VmGisungProcessItemDTO {
    private long id;
    private long gisungId;
    private long workId;
    private int workUpId;
    private String phasingCode;
    private String taskName;
    private BigDecimal taskCost = Utils.getDefaultDecimal();
    private BigInteger taskCost2;
    private BigDecimal progressRate = Utils.getDefaultDecimal();
    private String taskFullPath = "";
    private String parentTaskFullPath = "";
    private Long parentId = 0L;
    private Long rowNum = 0L;
    private Long parentRowNum = 0L;
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal compareCost = Utils.getDefaultDecimal();
    private BigDecimal compareProgressRate = Utils.getDefaultDecimal();
    @Enumerated(STRING)
    private GisungCompareResult compareResult = GisungCompareResult.NONE;
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private String status;
    private String addItem;
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
    private int compareCostI = 0;

    public VmGisungProcessItemDTO(long id
            , long gisungId
            , long workId
            , String phasingCode
            , String taskName
            , BigDecimal complexUnitPrice
            , BigDecimal taskCost
            , BigDecimal paidCost
            , BigDecimal paidProgressRate
            , BigDecimal beforeProgressRate
            , BigDecimal afterProgressRate
            , BigDecimal todayProgressRate
            , long beforeProgressAmount
            , long afterProgressAmount
            , long todayProgressAmount
            , BigDecimal cost
            , BigDecimal progressRate
            , BigDecimal compareCost
            , BigDecimal compareProgressRate
            , GisungCompareResult compareResult
            , String addItem
            , String cate1
            , String cate2
            , String cate3
            , String cate4
            , String cate5
            , String cate6
            , String cate1Name
            , String cate2Name
            , String cate3Name
            , String cate4Name
            , String cate5Name
            , String cate6Name
            , int workUpId) {
        this.id = id;
        this.gisungId = gisungId;
        this.workId = workId;
        this.phasingCode = phasingCode;
        this.taskName = taskName;
        this.complexUnitPrice = complexUnitPrice;
        this.taskCost = taskCost;
        this.paidCost = paidCost;
        this.paidProgressRate = paidProgressRate;
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.cost = cost;
        this.progressRate = progressRate;
        this.compareCost = compareCost;
        this.compareProgressRate = compareProgressRate;
        this.compareResult = compareResult;
        this.addItem = addItem;
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
        this.cate6Name = cate6Name;
        this.workUpId = workUpId;
    }

    public VmGisungProcessItemDTO(long id
            , long gisungId
            , long workId
            , String phasingCode
            , String taskName
            , BigDecimal complexUnitPrice
            , BigDecimal taskCost
            , BigDecimal paidCost
            , BigDecimal paidProgressRate
            , BigDecimal beforeProgressRate
            , BigDecimal afterProgressRate
            , BigDecimal todayProgressRate
            , long beforeProgressAmount
            , long afterProgressAmount
            , long todayProgressAmount
            , BigDecimal cost
            , BigDecimal progressRate
            , BigDecimal compareCost
            , BigDecimal compareProgressRate
            , GisungCompareResult compareResult
            , String addItem
            , String cate1
            , String cate2
            , String cate3
            , String cate4
            , String cate5
            , String cate6
            , String cate1Name
            , String cate2Name
            , String cate3Name
            , String cate4Name
            , String cate5Name
            , String cate6Name) {
        this.id = id;
        this.gisungId = gisungId;
        this.workId = workId;
        this.phasingCode = phasingCode;
        this.taskName = taskName;
        this.complexUnitPrice = complexUnitPrice;
        this.taskCost = taskCost;
        this.paidCost = paidCost;
        this.paidProgressRate = paidProgressRate;
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.cost = cost;
        this.progressRate = progressRate;
        this.compareCost = compareCost;
        this.compareProgressRate = compareProgressRate;
        this.compareResult = compareResult;
        this.addItem = addItem;
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
        this.cate6Name = cate6Name;
    }

    public VmGisungProcessItemDTO(long id
            , long gisungId
            , long workId
            , String phasingCode
            , String taskName
            , BigDecimal complexUnitPrice
            , BigDecimal taskCost
            , BigDecimal paidCost
            , BigDecimal paidProgressRate
            , BigDecimal beforeProgressRate
            , BigDecimal afterProgressRate
            , BigDecimal todayProgressRate
            , long beforeProgressAmount
            , long afterProgressAmount
            , long todayProgressAmount
            , BigDecimal cost
            , BigDecimal progressRate
            , int compareCost
            , BigDecimal compareProgressRate
            , GisungCompareResult compareResult
            , String addItem
            , String cate1
            , String cate2
            , String cate3
            , String cate4
            , String cate5
            , String cate6
            , String cate1Name
            , String cate2Name
            , String cate3Name
            , String cate4Name
            , String cate5Name
            , String cate6Name) {
        this.id = id;
        this.gisungId = gisungId;
        this.workId = workId;
        this.phasingCode = phasingCode;
        this.taskName = taskName;
        this.complexUnitPrice = complexUnitPrice;
        this.taskCost = taskCost;
        this.paidCost = paidCost;
        this.paidProgressRate = paidProgressRate;
        this.beforeProgressRate = beforeProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.beforeProgressAmount = beforeProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.cost = cost;
        this.progressRate = progressRate;
        this.compareCostI = compareCost;
        this.compareProgressRate = compareProgressRate;
        this.compareResult = compareResult;
        this.addItem = addItem;
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
        this.cate6Name = cate6Name;
    }
}
