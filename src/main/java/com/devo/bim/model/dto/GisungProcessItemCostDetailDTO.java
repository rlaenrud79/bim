package com.devo.bim.model.dto;

import com.devo.bim.model.enumulator.GisungCompareResult;
import com.devo.bim.model.enumulator.GisungStatus;
import lombok.Data;

import javax.persistence.Enumerated;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Data
public class GisungProcessItemCostDetailDTO {
    private long id;
    private String code;
    private String name;
    private boolean isFirst;
    private BigDecimal count;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal cost;
    private BigDecimal jobSheetProgressCount;
    private long jobSheetProgressAmount;
    private BigDecimal paidProgressCount;
    private BigDecimal paidCost;
    private BigDecimal progressCount;
    private BigDecimal progressCost;
    private BigDecimal sumProgressCount;
    private BigDecimal sumProgressCost;
    private BigDecimal remindProgressCount;
    private BigDecimal remindProgressCost;
    private BigDecimal compareProgressCount;
    private BigDecimal compareProgressCost;
    private GisungCompareResult compareResult = GisungCompareResult.NONE;
    private BigDecimal sumCompareProgressCount;
    private BigDecimal sumCompareProgressCost;
    private BigDecimal remindCompareProgressCount;
    private BigDecimal remindCompareProgressCost;
    private String taskName;
    private String phasingCode;
    private GisungStatus status;
    private long gisungProcessItemId;

    public GisungProcessItemCostDetailDTO(long id
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal compareProgressCount
            , BigDecimal compareProgressCost
            , GisungCompareResult compareResult) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.compareProgressCount = compareProgressCount;
        this.compareProgressCost = compareProgressCost;
        if (compareResult != null) {
            this.compareResult = compareResult;
        } else {
            this.compareResult = GisungCompareResult.NONE;
        }
    }

    public GisungProcessItemCostDetailDTO(String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal paidProgressCount
            , BigDecimal paidCost) {
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
    }

    public GisungProcessItemCostDetailDTO(long id
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal sumProgressCount
            , BigDecimal sumProgressCost
            , BigDecimal remindProgressCount
            , BigDecimal remindProgressCost
            , BigDecimal compareProgressCount
            , BigDecimal compareProgressCost
            , GisungCompareResult compareResult
            , BigDecimal sumCompareProgressCount
            , BigDecimal sumCompareProgressCost
            , BigDecimal remindCompareProgressCount
            , BigDecimal remindCompareProgressCost) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.sumProgressCount = sumProgressCount;
        this.sumProgressCost = sumProgressCost;
        this.remindProgressCount = remindProgressCount;
        this.remindProgressCost = remindProgressCost;
        this.compareProgressCount = compareProgressCount;
        this.compareProgressCost = compareProgressCost;
        this.compareResult = compareResult;
        this.sumCompareProgressCount = sumCompareProgressCount;
        this.sumCompareProgressCost = sumCompareProgressCost;
        this.remindCompareProgressCount = remindCompareProgressCount;
        this.remindCompareProgressCost = remindCompareProgressCost;
    }

    public GisungProcessItemCostDetailDTO(String taskName
            , String phasingCode
            , long id
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal sumProgressCount
            , BigDecimal sumProgressCost
            , BigDecimal remindProgressCount
            , BigDecimal remindProgressCost) {
        this.taskName = taskName;
        this.phasingCode = phasingCode;
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.sumProgressCount = sumProgressCount;
        this.sumProgressCost = sumProgressCost;
        this.remindProgressCount = remindProgressCount;
        this.remindProgressCost = remindProgressCost;
    }

    public GisungProcessItemCostDetailDTO(long id
            , String code
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal paidProgressCount
            , BigDecimal paidCost) {
        this.id = id;
        this.code = code;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
    }

    public GisungProcessItemCostDetailDTO(String code) {
        this.code = code;
    }

    public GisungProcessItemCostDetailDTO(String code
            , BigDecimal count
            , BigDecimal cost) {
        this.code = code;
        this.count = count;
        this.cost = cost;
    }

    public GisungProcessItemCostDetailDTO(String code
            , String name) {
        this.code = code;
        this.name = name;
    }

}
