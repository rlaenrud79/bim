package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.GisungWorkCost;
import com.devo.bim.model.entity.WorkAmount;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VmGisungWorkCostDTO {
    private long id;
    private long gisungId;
    private long workId;
    private long upId;
    private long projectId;
    private Integer gisungNo;
    private String year;
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal totalPaidCost = Utils.getDefaultDecimal();
    private String workNameLocale;
    private long workTotalAmount;
    private long workAmount;

    public VmGisungWorkCostDTO(long id
            , long gisungId
            , Integer gisungNo
            , long workId
            , Long projectId
            , String year
            , BigDecimal paidCost
            , BigDecimal prevPaidCost
            , BigDecimal totalPaidCost
            , String workNameLocale
            , long upId
            , long workTotalAmount
            , long workAmount) {
        this.id = id;
        this.gisungId = gisungId;
        this.gisungNo = gisungNo;
        this.workId = workId;
        this.projectId = projectId;
        this.year = year;
        this.paidCost = paidCost;
        this.prevPaidCost = prevPaidCost;
        this.totalPaidCost = totalPaidCost;
        this.workNameLocale = workNameLocale;
        this.upId = upId;
        this.workTotalAmount = workTotalAmount;
        this.workAmount = workAmount;
    }
}
