package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class VmGisungWorkCostVO {
    private long id;
    private long gisungId;
    private long workId;
    private long projectId;
    private Integer gisungNo;
    private String year;
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal totalPaidCost = Utils.getDefaultDecimal();
    private BigDecimal totalPaidRate = Utils.getDefaultDecimal();
    private String workNameLocale;
    private long workTotalAmount;
    private long workAmount;
}
