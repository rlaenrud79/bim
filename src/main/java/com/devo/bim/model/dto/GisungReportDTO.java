package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GisungReportDTO {
    private long id;
    private long projectId;
    private String title;
    private String surveyOriginFileName;
    private BigDecimal surveySize;
    private String partSurveyOriginFileName;
    private BigDecimal partSurveySize;
    private String aggregateOriginFileName;
    private BigDecimal aggregateSize;
    private String partAggregateOriginFileName;
    private BigDecimal partAggregateSize;
    private String accountOriginFileName;
    private BigDecimal accountSize;
    private String etcOriginFileName;
    private BigDecimal etcSize;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private long gisungId;

    public GisungReportDTO(long id
            , long projectId
            , String title
            , long gisungId
            , String surveyOriginFileName
            , BigDecimal surveySize
            , LocalDateTime writeDate
            , Account writer) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.gisungId = gisungId;
        this.surveyOriginFileName = surveyOriginFileName;
        this.surveySize = surveySize;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
    }

    public BigDecimal getFileSizeMegaByteUnit(){
        return Utils.getFileSizeMegaByteUnit(this.surveySize);
    }
}
