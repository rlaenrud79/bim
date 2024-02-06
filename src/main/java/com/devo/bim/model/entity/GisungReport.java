package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungReportVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungReport extends ObjectModelHelper<GisungReport> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private long projectId;

    private String title;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private String surveyFileName;

    private String surveyFilePath;

    private BigDecimal surveyFileSize;

    private String partSurveyFileName;

    private String partSurveyFilePath;

    private BigDecimal partSurveyFileSize;

    private String aggregateFileName;

    private String aggregateFilePath;

    private BigDecimal aggregateFileSize;

    private String partAggregateFileName;

    private String partAggregateFilePath;

    private BigDecimal partAggregateFileSize;

    private String accountFileName;

    private String accountFilePath;

    private BigDecimal accountFileSize;

    private String etcFileName;

    private String etcFilePath;

    private BigDecimal etcFileSize;

    public void setGisungReportAtAddGisungReport(GisungReportVO gisungReportVO, Gisung gisung, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.title = gisungReportVO.getTitle();
        this.gisung = gisung;
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungReportAtUpdateGisungReport(GisungReportVO gisungReportVO, UserInfo userInfo) {
        this.title = gisungReportVO.getTitle();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }

    public void setGisungSurveyFileInfoAtFileUpload(String surveyFileName, String surveyFilePath, BigDecimal surveyFileSize){
        this.surveyFileName = surveyFileName;
        this.surveyFilePath = surveyFilePath;
        this.surveyFileSize = surveyFileSize;
    }

    public void setGisungPartSurveyFileInfoAtFileUpload(String partSurveyFileName, String partSurveyFilePath, BigDecimal partSurveyFileSize){
        this.partSurveyFileName = partSurveyFileName;
        this.partSurveyFilePath = partSurveyFilePath;
        this.partSurveyFileSize = partSurveyFileSize;
    }

    public void setGisungAggregateFileInfoAtFileUpload(String aggregateFileName, String aggregateFilePath, BigDecimal aggregateFileSize){
        this.aggregateFileName = aggregateFileName;
        this.aggregateFilePath = aggregateFilePath;
        this.aggregateFileSize = aggregateFileSize;
    }

    public void setGisungPartAggregateFileInfoAtFileUpload(String partAggregateFileName, String partAggregateFilePath, BigDecimal partAggregateFileSize){
        this.partAggregateFileName = partAggregateFileName;
        this.partAggregateFilePath = partAggregateFilePath;
        this.partAggregateFileSize = partAggregateFileSize;
    }

    public void setGisungAccountFileInfoAtFileUpload(String accountFileName, String accountFilePath, BigDecimal accountFileSize){
        this.accountFileName = accountFileName;
        this.accountFilePath = accountFilePath;
        this.accountFileSize = accountFileSize;
    }

    public void setGisungEtcFileInfoAtFileUpload(String etcFileName, String etcFilePath, BigDecimal etcFileSize){
        this.etcFileName = etcFileName;
        this.etcFilePath = etcFilePath;
        this.etcFileSize = etcFileSize;
    }

    public String getSurveyFileNameExt() {
        return Utils.getFileExtName(surveyFileName);
    }

    public String getPartSurveyFileNameExt() {
        return Utils.getFileExtName(partSurveyFileName);
    }

    public String getAggregateFileNameExt() {
        return Utils.getFileExtName(aggregateFileName);
    }

    public String getPartAggregateFileNameExt() {
        return Utils.getFileExtName(partAggregateFileName);
    }

    public String getAccountFileNameExt() {
        return Utils.getFileExtName(accountFileName);
    }

    public String getEtcFileNameExt() {
        return Utils.getFileExtName(etcFileName);
    }
}
