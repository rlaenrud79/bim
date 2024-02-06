package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemLink;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ProcessDTO {
    private long id;
    private long processId;
    private String taskName;
    private String phasingCode;
    private int duration;
    private String plannedStartDate;
    private String plannedEndDate;
    private String startDate;
    private String endDate;
    private BigDecimal progressRate;
    private int taskDepth = 0;

    private String fsCodes = "";
    private String fs = "";
    private String fsDur = "";

    private int ganttSortNo;

    private int rowNum;

    private String firstCountFormula = "";
    private BigDecimal firstCount = Utils.getDefaultDecimal();
    private String firstCountUnit = "";
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private Long progressAmount = 0L;

    public ProcessDTO(ProcessItem processItem){
        this.id = processItem.getId();
        this.processId = processItem.getProcessInfo().getId();
        this.taskName = processItem.getTaskName();
        this.phasingCode = processItem.getPhasingCode();
        this.duration = processItem.getDuration();
        this.plannedStartDate = processItem.getPlannedStartDate();
        this.plannedEndDate = processItem.getPlannedEndDate();
        this.startDate = processItem.getStartDate();
        this.endDate = processItem.getEndDate();
        this.progressRate = processItem.getProgressRate();
        this.taskDepth = processItem.getTaskDepth();
        this.ganttSortNo = processItem.getGanttSortNo();
        this.rowNum = processItem.getRowNum();

        this.firstCountFormula = processItem.getFirstCountFormula();
        this.firstCount = processItem.getFirstCount();
        this.firstCountUnit = processItem.getFirstCountUnit();
        this.complexUnitPrice = processItem.getComplexUnitPrice();
        this.cost = processItem.getCost();
        // this.progressAmount = processItem.getProgressAmount();
        if (processItem.getProgressAmount() >= 0) {
            this.progressAmount = processItem.getProgressAmount();
        } else {
            this.progressAmount = 0L;
        }
    }

    public void setFsInfo(List<ProcessItemLink> processItemLinks){
        for (ProcessItemLink processItemLink : processItemLinks) {
            this.fsCodes += ( processItemLink.getPredecessor() == null ? "" : processItemLink.getPredecessor() ) + ",";
            this.fs += ( processItemLink.getPredecessorType() == null ? "" : processItemLink.getPredecessorType() ) + ",";
            this.fsDur += processItemLink.getPredecessorDuration() + ",";
        }

        this.fsCodes = StringUtils.removeEnd( this.fsCodes, ",");
        this.fs = StringUtils.removeEnd( this.fs, ",");
        this.fsDur = StringUtils.removeEnd( this.fsDur, ",");
    }
}
