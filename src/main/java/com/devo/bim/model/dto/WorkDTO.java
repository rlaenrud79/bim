package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.WorkStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkDTO {
    private long id;
    private long projectId;
    private String workName;
    private String workNameLocale;
    private WorkStatus status;
    private int sortNo;
    private long upId;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private LocalDateTime lastModifyDate;

    public WorkDTO(int sortNo) {
        this.sortNo = sortNo;
    }

    public WorkDTO(long id, long projectId, String workName, String workNameLocale, int sortNo, WorkStatus status
            , Account writer, LocalDateTime writeDate, LocalDateTime lastModifyDate, long upId) {
        this.id = id;
        this.projectId = projectId;
        this.workName = workName;
        this.workNameLocale = workNameLocale;
        this.sortNo = sortNo;
        this.status = status;
        this.writerDTO = new AccountDTO(writer);
        this.writeDate = writeDate;
        this.lastModifyDate = lastModifyDate;
        this.upId = upId;
    }

    public WorkDTO(long id, long projectId, String workName, String workNameLocale, int sortNo, WorkStatus status
            , long upId) {
        this.id = id;
        this.projectId = projectId;
        this.workName = workName;
        this.workNameLocale = workNameLocale;
        this.sortNo = sortNo;
        this.status = status;
        this.upId = upId;
    }

    public String getWorkStatusString() {
        if (this.status.equals(WorkStatus.USE)) return "Y";
        return "N";
    }
}
