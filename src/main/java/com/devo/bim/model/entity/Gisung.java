package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.GisungStatus;
import com.devo.bim.model.vo.GisungVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Gisung extends ObjectModelHelper<Gisung> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String title;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private String year;
    private String month;
    private String jobSheetStartDate;
    private String jobSheetEndDate;
    private Integer gisungNo;
    @Enumerated(STRING)
    private GisungStatus status;
    private BigDecimal sumPaidCost = Utils.getDefaultDecimal();
    private BigDecimal sumPaidProgressRate = Utils.getDefaultDecimal();
    private long contractAmount = 0;
    private BigDecimal itemPaidCost = Utils.getDefaultDecimal();
    private BigDecimal sumItemPaidCost = Utils.getDefaultDecimal();
    private BigDecimal itemPaidProgressRate = Utils.getDefaultDecimal();

    public Gisung(long gisungId) {
        this.id = gisungId;
    }

    public void setGisungAtAddGisung(GisungVO gisungVO, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.title = gisungVO.getTitle();
        this.year = gisungVO.getYear();
        this.month = gisungVO.getMonth();
        this.jobSheetStartDate = gisungVO.getJobSheetStartDate();
        this.jobSheetEndDate = gisungVO.getJobSheetEndDate();
        this.gisungNo = gisungVO.getGisungNo();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        if(GisungStatus.COMPLETE.name().equals(gisungVO.getStatus())){
            this.status = GisungStatus.COMPLETE;
        } else {
            this.status = GisungStatus.WRITING;
        }
    }

    public void setGisungAtUpdateGisung(GisungVO gisungVO, UserInfo userInfo) {
        this.title = gisungVO.getTitle();
        this.year = gisungVO.getYear();
        this.month = gisungVO.getMonth();
        this.jobSheetStartDate = gisungVO.getJobSheetStartDate();
        this.jobSheetEndDate = gisungVO.getJobSheetEndDate();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }

    public void setGisungAtGisungStatusComplete(GisungVO gisungVO) {
        this.status = GisungStatus.COMPLETE;
        this.sumPaidCost = gisungVO.getSumPaidCost();
        this.sumPaidProgressRate = gisungVO.getSumPaidProgressRate();
        if(GisungStatus.COMPLETE.name().equals(gisungVO.getStatus())){
            this.status = GisungStatus.COMPLETE;
        } else {
            this.status = GisungStatus.WRITING;
        }
    }

    public void setGisungAtUpdateGisungItem(GisungVO gisungVO) {
        this.contractAmount = gisungVO.getContractAmount();
        this.itemPaidCost = gisungVO.getItemPaidCost();
        this.sumItemPaidCost = gisungVO.getSumItemPaidCost();
        this.itemPaidProgressRate = gisungVO.getItemPaidProgressRate();
    }
}
