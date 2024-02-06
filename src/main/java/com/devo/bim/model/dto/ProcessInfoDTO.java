package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ProcessInfo;
import com.devo.bim.model.enumulator.ProcessValidateStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ProcessInfoDTO {

    private long processInfoId;
    private long projectId;

    private String validateMessage;
    private ProcessValidateStatus validateStatus;

    private String writeDate;
    private AccountDTO writerDTO;
    private String title;
    private String description;
    private boolean isCurrentVersion;

    public ProcessInfoDTO(ProcessInfo processInfo){
        this.processInfoId = processInfo.getId();
        this.projectId = processInfo.getProjectId();
        this.validateMessage = processInfo.getValidateMessage();
        this.validateStatus = processInfo.getValidateStatus();
        this.writeDate = processInfo.getWriteEmbedded().getWriteDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.writerDTO = new AccountDTO(processInfo.getWriteEmbedded().getWriter());
        this.title = processInfo.getTitle();
        this.description = processInfo.getDescription();
        this.isCurrentVersion = processInfo.isCurrentVersion();
    }
}
