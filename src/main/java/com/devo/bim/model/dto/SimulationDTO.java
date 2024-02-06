package com.devo.bim.model.dto;

import java.math.BigDecimal;

import com.devo.bim.model.entity.ProcessItem;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimulationDTO {

    private long processItemId;
    private String phasingCode;
    private long workId;
    private String startDate;
    private String endDate;
    private String taskFullPath;
    private String[] exchangeIds;
    private BigDecimal progressRate;

    public SimulationDTO(ProcessItem processItem)
    {
        this.processItemId = processItem.getId();
        this.phasingCode = processItem.getPhasingCode();
        this.workId = processItem.getWork().getId();
        this.startDate = processItem.getStartDate();
        this.endDate = processItem.getEndDate();
        this.taskFullPath = processItem.getTaskFullPath();
        this.progressRate = processItem.getProgressRate();
        if(processItem.getExchangeIds() != null) {
        	this.exchangeIds = processItem.getExchangeIds().split("\\|,\\|");
        }
    }
}
