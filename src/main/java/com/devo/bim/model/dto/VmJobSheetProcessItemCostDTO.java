package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class VmJobSheetProcessItemCostDTO {
    private long id;
    private long projectId;
    private long processItemId;
    private long workId;
    private LocalDateTime reportDate;
    private String phasingCode;
    private String taskName;
    private BigDecimal taskCost = Utils.getDefaultDecimal();
    private BigDecimal progressRate = Utils.getDefaultDecimal();
    private String taskFullPath;
    private long parentId;
    private String parentTaskFullPath;
    private long beforeProgressAmount;
    private BigDecimal beforeProgressRate = new BigDecimal(BigInteger.ZERO);
    private long todayProgressAmount;
    private BigDecimal todayProgressRate = new BigDecimal(BigInteger.ZERO);
    private long afterProgressAmount;
    private BigDecimal afterProgressRate = new BigDecimal(BigInteger.ZERO);
    private String totalChk;
    private BigDecimal yearProgressRate = new BigDecimal(BigInteger.ZERO);
    private int rowNum;
    private int parentRowNum;
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private String addItem;

    public VmJobSheetProcessItemCostDTO(String phasingCode, String taskName, String taskFullPath, Integer rowNum, Integer parentRowNum,
                                      long beforeProgressAmount, long todayProgressAmount, long afterProgressAmount,
                                      BigDecimal beforeProgressRate, BigDecimal todayProgressRate, BigDecimal afterProgressRate,
                                      BigDecimal complexUnitPrice, BigDecimal taskCost, BigDecimal paidCost, BigDecimal paidProgressRate,
                                      long parentId, String parentTaskFullPath, long workId, long processItemId) {
        this.phasingCode = phasingCode;
        this.taskName = taskName;
        this.taskFullPath = taskFullPath;
        this.rowNum = rowNum;
        this.parentRowNum = parentRowNum;
        this.beforeProgressAmount = beforeProgressAmount;
        this.todayProgressAmount = todayProgressAmount;
        this.afterProgressAmount = afterProgressAmount;
        this.beforeProgressRate = beforeProgressRate;
        this.todayProgressRate = todayProgressRate;
        this.afterProgressRate = afterProgressRate;
        this.complexUnitPrice = complexUnitPrice;
        this.taskCost = taskCost;
        this.paidCost = paidCost;
        this.paidProgressRate = paidProgressRate;
        this.parentId = parentId;
        this.parentTaskFullPath = parentTaskFullPath;
        this.workId = workId;
        this.processItemId = processItemId;
    }
}
