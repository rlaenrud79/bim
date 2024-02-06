package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WorkAmountDTO {
    private long id;
    private long projectId;
    private String year;
    private long totalAmount;
    private long prevAmount;
    private long amount;
    private long workId;
    private long upId;
    private String workNameLocale;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private long todayAmount;
    private BigDecimal cost = new BigDecimal(BigInteger.ZERO);
    private long beforeProgressAmount;
    private BigDecimal beforeProgressRate = new BigDecimal(BigInteger.ZERO);
    private long todayProgressAmount;
    private BigDecimal todayProgressRate = new BigDecimal(BigInteger.ZERO);
    private long afterProgressAmount;
    private BigDecimal afterProgressRate = new BigDecimal(BigInteger.ZERO);
    private BigDecimal yearProgressRate = new BigDecimal(BigInteger.ZERO);
    private long paidCost;
    //private List<JobSheetProcessItemDTO> children;
    List<JobSheetProcessItemDTO> jobSheetProcessItemDTOs = new ArrayList<>();

    public WorkAmountDTO(long workId
            , String workNameLocale
            , List<JobSheetProcessItemDTO> jobSheetProcessItemDTOs
            , BigDecimal cost
            , long beforeProgressAmount
            , BigDecimal beforeProgressRate
            , long todayProgressAmount
            , BigDecimal todayProgressRate
            , long afterProgressAmount
            , BigDecimal afterProgressRate
            , BigDecimal yearProgressRate) {
        this.workId = workId;
        this.workNameLocale = workNameLocale;
        this.jobSheetProcessItemDTOs = jobSheetProcessItemDTOs;
        this.cost = cost;
        this.beforeProgressAmount = beforeProgressAmount;
        this.beforeProgressRate = beforeProgressRate;
        this.todayProgressAmount = todayProgressAmount;
        this.todayProgressRate = todayProgressRate;
        this.afterProgressAmount = afterProgressAmount;
        this.afterProgressRate = afterProgressRate;
        this.yearProgressRate = yearProgressRate;
    }

    public WorkAmountDTO(long id
            , long projectId
            , String year
            , long totalAmount
            , long prevAmount
            , long amount
            , long workId
            , String workNameLocale
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.year = year;
        this.totalAmount = totalAmount;
        this.prevAmount = prevAmount;
        this.amount = amount;
        this.workId = workId;
        this.workNameLocale = workNameLocale;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public WorkAmountDTO(long id
            , long projectId
            , String year
            , long totalAmount
            , long prevAmount
            , long amount
            , long workId
            , String workNameLocale
            , LocalDateTime writeDate
            , Account writer
            , long todayAmount
            , long paidCost) {
        this.id = id;
        this.projectId = projectId;
        this.year = year;
        this.totalAmount = totalAmount;
        this.prevAmount = prevAmount;
        this.amount = amount;
        this.workId = workId;
        this.workNameLocale = workNameLocale;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.todayAmount = todayAmount;
        this.paidCost = paidCost;
    }

    public WorkAmountDTO(long totalAmount
            , long prevAmount
            , long amount
            , long todayAmount
            , long paidCost) {
        this.totalAmount = totalAmount;
        this.prevAmount = prevAmount;
        this.amount = amount;
        this.todayAmount = todayAmount;
        this.paidCost = paidCost;
    }

    public WorkAmountDTO(long id
            , long projectId
            , String year
            , long totalAmount
            , long prevAmount
            , long amount
            , long workId
            , long upId
            , String workNameLocale
            , long todayAmount
            , long paidCost) {
        this.id = id;
        this.projectId = projectId;
        this.year = year;
        this.totalAmount = totalAmount;
        this.prevAmount = prevAmount;
        this.amount = amount;
        this.workId = workId;
        this.upId = upId;
        this.workNameLocale = workNameLocale;
        this.todayAmount = todayAmount;
        this.paidCost = paidCost;
    }
}
