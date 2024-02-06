package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemCostPay;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
public class ProcessItemCostPayDTO {

    private boolean isEdit;
    private long processItemCostPayId;
    private long processItemId;
    private int costNo;
    private String costDate;
    private BigDecimal progressRate;
    private BigInteger cost;
    private BigDecimal sumProgressRate;
    private BigInteger sumCost;
    private BigDecimal taskProgressRate;
    private BigDecimal taskCost;
    private String description;
    private String writeDate;
    private String lastModifyDate;

    private int progress;
    private int sumProgress;
    private int taskProgress;

    private int beforeSumProgress;
    private BigInteger beforeSumCost;

    public ProcessItemCostPayDTO(ProcessItemCostPay processItemCostPay) {
        isEdit = false;

        processItemCostPayId = processItemCostPay.getId();
        processItemId = processItemCostPay.getProcessItem().getId();
        costNo = processItemCostPay.getCostNo();
        costDate = Utils.getDateTimeByNationAndFormatType(processItemCostPay.getCostDate(), DateFormatEnum.YEAR_MONTH_DAY);
        progressRate = processItemCostPay.getProgressRate();
        cost = processItemCostPay.getCost().toBigInteger();
        sumProgressRate = processItemCostPay.getSumProgressRate();
        sumCost = processItemCostPay.getSumCost().toBigInteger();
        taskProgressRate = processItemCostPay.getTaskProgressRate();
        taskCost = processItemCostPay.getTaskCost();
        description = processItemCostPay.getDescription();
        writeDate = processItemCostPay.getWriteEmbedded() == null ? "" : processItemCostPay.getWriteEmbedded().getWriteDate(DateFormatEnum.YEAR_MONTH_DAY);
        lastModifyDate = processItemCostPay.getLastModifyEmbedded() == null ? "" : processItemCostPay.getLastModifyEmbedded().getLastModifyDate(DateFormatEnum.YEAR_MONTH_DAY);

        setPropertyByConverting();
    }

    public void setPropertyByConverting()
    {
        progress = progressRate.intValue();
        sumProgress = sumProgressRate.intValue();
        taskProgress = taskProgressRate.intValue();

        beforeSumProgress = sumProgress - progress;
        beforeSumCost = sumCost.subtract(cost);
    }
}
