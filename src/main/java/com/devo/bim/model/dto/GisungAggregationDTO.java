package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GisungAggregationDTO {
    private long id;
    private long projectId;
    private String year;
    private String netCheck;
    private String title;
    private long cost;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;

    public GisungAggregationDTO(long id
            , long projectId
            , String year
            , String netCheck
            , String title
            , long cost
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.year = year;
        this.netCheck = netCheck;
        this.title = title;
        this.cost = cost;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public GisungAggregationDTO(String year
            , long projectId
            , Account writer) {
        this.year = year;
        this.projectId = projectId;
        this.writerDTO = new AccountDTO(writer);
    }
}
