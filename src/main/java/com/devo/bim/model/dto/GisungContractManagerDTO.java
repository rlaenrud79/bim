package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GisungContractManagerDTO {
    private long id;
    private long projectId;
    private String company;
    private String damName;
    private String stampPath;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;

    public GisungContractManagerDTO(long id
            , long projectId
            , String company
            , String damName
            , String stampPath
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.company = company;
        this.damName = damName;
        this.stampPath = stampPath;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public GisungContractManagerDTO(long id
            , long projectId
            , String company
            , String damName
            , String stampPath) {
        this.id = id;
        this.projectId = projectId;
        this.company = company;
        this.damName = damName;
        this.stampPath = stampPath;
    }
}
