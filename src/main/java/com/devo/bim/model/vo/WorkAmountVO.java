package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class WorkAmountVO {
    private long id;
    private String year;
    private long workId;
    private String workNameLocale;
    private long totalAmount;
    private long prevAmount;
    private long amount;
    private long workPlanAmount;
    private long progressAmount;
    private BigDecimal progressRate = new BigDecimal(BigInteger.ZERO);
    private long paidCost;
    private BigDecimal paidCostRate = new BigDecimal(BigInteger.ZERO);
    private long workPlanAmountSum;
    private long progressAmountSum;
    private BigDecimal progressSumRate = new BigDecimal(BigInteger.ZERO);
    private long paidCostSum;
    private BigDecimal paidCostSumRate = new BigDecimal(BigInteger.ZERO);
    private List<Object[]> processItemDepthList;
    private BigDecimal yearProgressAmount = new BigDecimal(BigInteger.ZERO);
    private BigDecimal yearPaidCost = new BigDecimal(BigInteger.ZERO);
}
