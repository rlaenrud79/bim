package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WorkPlanDTO {
    private long id;
    private long projectId;
    private String year;
    private String month;
    private BigDecimal monthRate = BigDecimal.ZERO;
    private double dayRate;
    private long workId;
    private String workNameLocale;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;

    public WorkPlanDTO(long id
            , long projectId
            , String year
            , String month
            , BigDecimal monthRate
            , double dayRate
            , long workId
            , String workNameLocale
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.year = year;
        this.month = month;
        this.monthRate = monthRate;
        this.dayRate = dayRate;
        this.workId = workId;
        this.workNameLocale = workNameLocale;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public WorkPlanDTO(long workId
            , String year
            , String month
            , BigDecimal monthRate
            , double dayRate) {
        this.workId = workId;
        this.year = year;
        this.month = month;
        this.monthRate = monthRate;
        this.dayRate = dayRate;
    }
}
