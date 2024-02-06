package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class GisungProcessItemCostDetailVO {
    private BigDecimal progressCount;
    private BigDecimal progressCost;
}
