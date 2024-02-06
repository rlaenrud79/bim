package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ProcessCategory;
import com.devo.bim.model.entity.ProcessItemCategory;
import com.devo.bim.model.enumulator.TaskType;
import lombok.Data;

@Data
public class ProcessItemCategoryDTO {
    private String code;
    private String name;
    private String upCode;
    private long display;
    private long processItemId;
    private long jobSheetProcessItemCount;
    private long subProcessItemCount;
    private TaskType ganttTaskType;

    public ProcessItemCategoryDTO(ProcessItemCategory processItemCategory, long processItemId, TaskType ganttTaskType, long subProcessItemCount, long jobSheetProcessItemCount) {
        this.code = processItemCategory.getCode();
        this.name = processItemCategory.getName();
        this.upCode = processItemCategory.getUpCode();
        this.display = processItemCategory.getDisplay();
        this.processItemId = processItemId;
        this.ganttTaskType = ganttTaskType;
        this.subProcessItemCount = subProcessItemCount;
        this.jobSheetProcessItemCount = jobSheetProcessItemCount;
    }
}
