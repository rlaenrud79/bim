package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.ProcessValidateStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessInfo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private String validateMessage;

    @Enumerated(STRING)
    private ProcessValidateStatus validateStatus;

    private String fileName;
    private String title;
    private String description;

    private boolean isCurrentVersion;

    @OneToMany(fetch = LAZY, mappedBy = "processInfo")
    private List<ProcessItem> processItems = new ArrayList<>();

    public ProcessInfo(long id){
        this.id = id;
    }

    public ProcessInfo(long projectId, long accountId, String fileName, String title, String description) {
        this.projectId = projectId;
        this.writeEmbedded = new WriteEmbedded(accountId);
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
        this.validateMessage = "";
        this.validateStatus = ProcessValidateStatus.READY;
        this.fileName = fileName;
        this.title = title;
        this.description = description;
        this.isCurrentVersion = false;
    }

    public void setIsCurrentVersion(boolean isCurrentVersion){
        this.isCurrentVersion = isCurrentVersion;
    }

    public void setValidateStatusAndMessage(ProcessValidateStatus processValidateStatus, String validateMessage){
        if(processValidateStatus != null)this.validateStatus = processValidateStatus;
        this.validateMessage = validateMessage;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }
}
