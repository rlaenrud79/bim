package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ProcessItemCostPay;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaidCostAllDTO {
    private String costDate;
    private int costNo;
    private String description;

    public PaidCostAllDTO(ProcessItemCostPay processItemCostPay){
        costDate = Utils.getDateTimeByNationAndFormatType(LocalDateTime.now(), DateFormatEnum.YEAR_MONTH_DAY);
        costNo = processItemCostPay.getCostNo() + 1;
        description = "";
    }
}