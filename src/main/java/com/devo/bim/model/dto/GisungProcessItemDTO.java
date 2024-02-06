package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.enumulator.TaskType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class GisungProcessItemDTO {
    private long id;
    private long gisungId;
    private long workId;
    private String phasingCode;
    private String taskName;
    private BigDecimal taskCost = Utils.getDefaultDecimal();
    private BigInteger taskCost2;
    private BigDecimal progressRate = Utils.getDefaultDecimal();
    private String taskFullPath = "";
    private String parentTaskFullPath = "";
    private Long parentId = 0L;
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private String status;
    private String addItem;

    public GisungProcessItemDTO(long id
            , long gisungId
            , long workId
            , String phasingCode
            , String taskName
            , String taskFullPath
            , String parentTaskFullPath
            , Long parentId
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
            , String addItem) {
        this.id = id;
        this.gisungId = gisungId;
        this.workId = workId;
        this.phasingCode = phasingCode;
        this.taskName = taskName;
        this.taskFullPath = taskFullPath;
        this.parentTaskFullPath = parentTaskFullPath;
        this.parentId = parentId;
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
        this.addItem = addItem;
    }
}
