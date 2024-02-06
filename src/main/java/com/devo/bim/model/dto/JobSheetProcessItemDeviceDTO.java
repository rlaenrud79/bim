package com.devo.bim.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class JobSheetProcessItemDeviceDTO {
    private long jobSheetId;

    private String name;

    private BigDecimal amount;

    private BigDecimal beforeAmount;

    @QueryProjection
    public JobSheetProcessItemDeviceDTO(String name, BigDecimal amount, BigDecimal beforeAmount) {
        this.name = name;
        this.amount = amount;
        this.beforeAmount = beforeAmount;
    }
}
