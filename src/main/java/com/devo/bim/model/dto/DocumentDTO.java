package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private long id;
    private long projectId;
    private String title;
    private String description;
    private long fileId;
    private String originFileName;
    private BigDecimal size;
    private long workId;
    private String workNameLocale;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private long documentCategoryId;
    private String category;

    public DocumentDTO(long id
            , long projectId
            , String title
            , String description
            , long fileId
            , String originFileName
            , BigDecimal size
            , long workId
            , String workNameLocale
            , LocalDateTime writeDate
            , Account writer
            , long documentCategoryId
            , String category) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.originFileName = originFileName;
        this.fileId = fileId;
        this.size = size;
        this.workId = workId;
        this.workNameLocale = workNameLocale;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.documentCategoryId = documentCategoryId;
        this.category = category;
    }

    public BigDecimal getFileSizeMegaByteUnit(){
        if (this.size == null) return BigDecimal.ZERO;
        return Utils.getFileSizeMegaByteUnit(this.size);
    }
}