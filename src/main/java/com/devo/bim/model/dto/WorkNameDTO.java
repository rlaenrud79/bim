package com.devo.bim.model.dto;

import lombok.Data;

@Data
public class WorkNameDTO {

    private long workId;
    private long workNameId;
    private String languageCode;
    private String workName;

    public WorkNameDTO(long workId, long workNameId, String languageCode, String workName) {
        this.workId = workId;
        this.workNameId = workNameId;
        this.languageCode = languageCode;
        this.workName = workName;
    }
}
