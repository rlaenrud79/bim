package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.DocumentCategoryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentCategoryDTO {
    private long id;
    private long projectId;
    private String name;
    private DocumentCategoryStatus status;
    private int sortNo;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private LocalDateTime lastModifyDate;

    public DocumentCategoryDTO(int sortNo) {
        this.sortNo = sortNo;
    }

    public DocumentCategoryDTO(long id, long projectId, String name, int sortNo, DocumentCategoryStatus status
            , LocalDateTime writeDate, Account writer, LocalDateTime lastModifyDate) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.sortNo = sortNo;
        this.status = status;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.lastModifyDate = lastModifyDate;
    }

    public DocumentCategoryDTO(long id, long projectId, String name, int sortNo, DocumentCategoryStatus status
            , LocalDateTime writeDate, Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.sortNo = sortNo;
        this.status = status;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public DocumentCategoryDTO(long id, long projectId, String name, int sortNo, DocumentCategoryStatus status
            , int upId) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.sortNo = sortNo;
        this.status = status;
    }

    public String getDocumentCategoryStatusString() {
        if (this.status.equals(DocumentCategoryStatus.USE)) return "Y";
        return "N";
    }
}
