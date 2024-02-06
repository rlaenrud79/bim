package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.JobSheetStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobSheetDTO {

    private long id;
    private long projectId;
    private String title;
    private String contents;
    private LocalDateTime reportDate;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private AccountDTO grantorDTO;
    private long referenceCount;
    private JobSheetStatus status;

    public JobSheetDTO(long id
            , long projectId
            , String title
            , String contents
            , LocalDateTime reportDate
            , LocalDateTime writeDate
            , Account writer
            , Account jobSheetGrantor
            , long referenceCount
            , JobSheetStatus status) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.contents = contents;
        this.reportDate = reportDate;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.grantorDTO = new AccountDTO(jobSheetGrantor);
        this.referenceCount = referenceCount;
        this.status = status;
    }

    public JobSheetDTO(long id
            , long projectId
            , String title
            , String contents
            , LocalDateTime reportDate
            , LocalDateTime writeDate
            , Account writer
            , JobSheetStatus status) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.contents = contents;
        this.reportDate = reportDate;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.status = status;
    }

}
