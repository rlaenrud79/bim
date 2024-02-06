package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobSheetTemplateDTO {
    private long id;
    private long projectId;
    private String title;
    private String contents;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private boolean enabled;

    public JobSheetTemplateDTO(long id, long projectId, String title, String contents, boolean enabled, Account writer, LocalDateTime writeDate) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.contents = contents;
        this.enabled = enabled;
        this.writerDTO = new AccountDTO(writer);
        this.writeDate = writeDate;
    }

    public String getStatusString() {
        if (this.enabled) return "Y";
        return "N";
    }
}
