package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PayHistoryVO {
    BigDecimal totalPay;
    BigDecimal totalCost;
}
