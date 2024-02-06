package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WorkPlanVO {
    private long id;
    private String year;
    private String month;
    private long workId;
    private BigDecimal monthRate = BigDecimal.ZERO;
    private double dayRate;
}
