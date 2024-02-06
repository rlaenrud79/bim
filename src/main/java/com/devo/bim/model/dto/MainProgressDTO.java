package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class MainProgressDTO {
    private BigDecimal dateProgressRate;
    private BigDecimal processProgressRate;
    private BigDecimal paidProgressRate;

    public MainProgressDTO(int totalDuration, BigDecimal progressDuration, BigDecimal totalCostAmt, BigDecimal totalPaidCostAmt ){

        if(progressDuration == null) progressDuration = new BigDecimal("0.00");
        if(totalPaidCostAmt == null) totalPaidCostAmt = new BigDecimal("0.00");

        try {
            if(totalDuration == 0) this.processProgressRate = new BigDecimal("0.00");
            else this.processProgressRate = progressDuration.divide(new BigDecimal(totalDuration), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        catch (Exception e){
            this.processProgressRate = new BigDecimal("0.00");
        }

        try {
            if(totalCostAmt.equals(new BigDecimal("0.00")) || totalCostAmt == null) this.paidProgressRate = new BigDecimal("0.00");
            else this.paidProgressRate = totalPaidCostAmt.divide(totalCostAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        catch (Exception e){
            this.paidProgressRate = new BigDecimal("0.00");
        }

    }

    public void setDateProgressRate(LocalDateTime projectStartDate, LocalDateTime projectEndDate){
        if(projectStartDate == null || projectEndDate == null ) this.dateProgressRate = new BigDecimal("0.00");
        try {
            long totalProjectDate = ChronoUnit.DAYS.between(projectStartDate.toLocalDate(), projectEndDate.toLocalDate());
            long passDate = ChronoUnit.DAYS.between(projectStartDate.toLocalDate(), LocalDateTime.now().toLocalDate());
            this.dateProgressRate = new BigDecimal((((float)passDate / (float)totalProjectDate) * 100) + ""  ).setScale(2, RoundingMode.HALF_UP);
        }
        catch (Exception e) {
            this.dateProgressRate = new BigDecimal("0.00");
        }
    }
}
