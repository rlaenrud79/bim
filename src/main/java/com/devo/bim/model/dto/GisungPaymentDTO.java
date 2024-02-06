package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GisungPaymentDTO {
    private long id;
    private long projectId;
    private String title;
    private String description;
    private long fileId;
    private String originFileName;
    private BigDecimal size;
    private String workNameLocale;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;

    public GisungPaymentDTO(long id
            , long projectId
            , String title
            , String description
            , long fileId
            , String originFileName
            , BigDecimal size
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.originFileName = originFileName;
        this.fileId = fileId;
        this.size = size;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public BigDecimal getFileSizeMegaByteUnit(){
        return Utils.getFileSizeMegaByteUnit(this.size);
    }
}
