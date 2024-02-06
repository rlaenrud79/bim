package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.VmProcessItem;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class TaskDTO {
    private long processId;
    private long id;
    private String text;
    private String start_date;
    private int duration;
    private String type;
    private BigDecimal progress;
    private boolean open;
    private String holder;
    private String priority;
    private String parent;
    private String phasingCode;
    private int taskDepth;
    private int ganttSortNo;
    private long workId;
    private String calendar_id;

    public TaskDTO (ProcessItem processItem){
        this.processId = processItem.getProcessInfo().getId();
        this.id = processItem.getId();
        this.text = processItem.getTaskName();
        this.start_date = StringUtils.isEmpty(processItem.getStartDate()) ? "" : Utils.getDateTimeByNationAndFormatType(Utils.parseLocalDateTimeStart(processItem.getStartDate()), DateFormatEnum.GANTT);
        this.duration = processItem.getDuration();
        this.type = processItem.getGanttTaskType().getValue();
        this.progress = processItem.getProgressRate() == null ? new BigDecimal("0.00") : processItem.getProgressRate().setScale(2, RoundingMode.HALF_UP);
        this.open = processItem.isGanttOpen();
        this.holder = processItem.getGanttHolder();
        this.priority = "";
        this.parent = processItem.getParentId() == null ? "" : processItem.getParentId().toString();
        this.phasingCode = processItem.getPhasingCode();
        this.taskDepth = processItem.getTaskDepth();
        this.ganttSortNo = processItem.getGanttSortNo();
        if(processItem.getWork() == null) {
            this.workId = 0L;
            this.calendar_id = "";
        }
        else {
            this.workId = processItem.getWork().getId();
            this.calendar_id = Long.toString(processItem.getWork().getId());
        }
    }

    public TaskDTO (VmProcessItem vmProcessItem){
        this.processId = vmProcessItem.getProcessInfo().getId();
        this.id = vmProcessItem.getId();
        this.text = vmProcessItem.getTaskName();
        this.start_date = StringUtils.isEmpty(vmProcessItem.getStartDate()) ? "" : Utils.getDateTimeByNationAndFormatType(Utils.parseLocalDateTimeStart(vmProcessItem.getStartDate()), DateFormatEnum.GANTT);
        this.duration = vmProcessItem.getDuration();
        this.type = vmProcessItem.getGanttTaskType().getValue();
        this.progress = vmProcessItem.getProgressRate() == null ? new BigDecimal("0.00") : vmProcessItem.getProgressRate().setScale(2, RoundingMode.HALF_UP);
        this.open = vmProcessItem.isGanttOpen();
        this.holder = vmProcessItem.getGanttHolder();
        this.priority = "";
        this.parent = vmProcessItem.getParentId() == null ? "" : vmProcessItem.getParentId().toString();
        this.phasingCode = vmProcessItem.getPhasingCode();
        this.taskDepth = vmProcessItem.getTaskDepth();
        this.ganttSortNo = vmProcessItem.getGanttSortNo();
        if(vmProcessItem.getWork() == null) {
            this.workId = 0L;
            this.calendar_id = "";
        }
        else {
            this.workId = vmProcessItem.getWork().getId();
            this.calendar_id = Long.toString(vmProcessItem.getWork().getId());
        }
    }
}
