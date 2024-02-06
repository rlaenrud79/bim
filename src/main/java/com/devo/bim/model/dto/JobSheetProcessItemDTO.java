package com.devo.bim.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class JobSheetProcessItemDTO {

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

    public JobSheetProcessItemDTO(long id, LocalDateTime reportDate){
        this.id = id;
        this.reportDate = reportDate;
    }

    public JobSheetProcessItemDTO(String taskName, String taskFullPath, BigDecimal cost, long beforeProgressAmount, BigDecimal beforeProgressRate, long todayProgressAmount, BigDecimal todayProgressRate, long afterProgressAmount, BigDecimal afterProgressRate, long parentId, String parentTaskFullPath, BigDecimal yearProgressRate){
        this.taskName = taskName;
        this.taskFullPath = taskFullPath;
        this.cost = cost;
        this.beforeProgressAmount = beforeProgressAmount;
        this.beforeProgressRate = beforeProgressRate;
        this.todayProgressAmount = todayProgressAmount;
        this.todayProgressRate = todayProgressRate;
        this.afterProgressAmount = afterProgressAmount;
        this.afterProgressRate = afterProgressRate;
        this.parentId = parentId;
        this.parentTaskFullPath = parentTaskFullPath;
        this.yearProgressRate = yearProgressRate;
    }

    public JobSheetProcessItemDTO(String taskName, String taskFullPath, BigDecimal cost, long beforeProgressAmount, BigDecimal beforeProgressRate, long todayProgressAmount, BigDecimal todayProgressRate, long afterProgressAmount, BigDecimal afterProgressRate, long parentId, String parentTaskFullPath){
        this.taskName = taskName;
        this.taskFullPath = taskFullPath;
        this.cost = cost;
        this.beforeProgressAmount = beforeProgressAmount;
        this.beforeProgressRate = beforeProgressRate;
        this.todayProgressAmount = todayProgressAmount;
        this.todayProgressRate = todayProgressRate;
        this.afterProgressAmount = afterProgressAmount;
        this.afterProgressRate = afterProgressRate;
        this.parentId = parentId;
        this.parentTaskFullPath = parentTaskFullPath;
    }

    public JobSheetProcessItemDTO(String exchangeId, String phasingCode){
        this.exchangeId = exchangeId;
        this.phasingCode =phasingCode;
    }

}