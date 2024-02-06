package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.GisungStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GisungDTO {
    private long id;
    private long projectId;
    private String title;
    private String year;
    private String month;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private Integer gisungNo;
    private GisungStatus status;
    private BigDecimal sumPaidCost = Utils.getDefaultDecimal();
    private BigDecimal sumPaidProgressRate = Utils.getDefaultDecimal();

    public GisungDTO(long id
            , long projectId
            , String title
            , String year
            , String month
            , Integer gisungNo
            , LocalDateTime writeDate
            , Account writer
            , GisungStatus status
            , BigDecimal sumPaidCost
            , BigDecimal sumPaidProgressRate) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.year = year;
        this.month = month;
        this.gisungNo = gisungNo;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.status = status;
        this.sumPaidCost = sumPaidCost;
        this.sumPaidProgressRate = sumPaidProgressRate;
    }
}
