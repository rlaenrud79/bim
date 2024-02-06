package com.devo.bim.model.dto;

import lombok.Data;

@Data
public class WorkIdDTO {
    private long workId;
    private String workName;

    public WorkIdDTO(long workId, String workName){
        this.workId = workId;
        this.workName = workName;
    }
}
