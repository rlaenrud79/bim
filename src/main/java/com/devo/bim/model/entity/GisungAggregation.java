package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungAggregationVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungAggregation extends ObjectModelHelper<GisungAggregation> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String year;
    private String netCheck;
    private String title;
    private long cost;
    private String gtype;
    private BigDecimal percent = BigDecimal.ZERO;
    private Integer documentNo = 0;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public void setGisungAggregationAtAddGisungAggregation(UserInfo userInfo, GisungAggregationVO gisungAggregationVO) {
        this.projectId = userInfo.getProjectId();
        this.year = gisungAggregationVO.getYear();
        this.netCheck = gisungAggregationVO.getNetCheck();
        this.title = gisungAggregationVO.getTitle();
        this.cost = gisungAggregationVO.getCost();
        this.gtype = gisungAggregationVO.getGtype();
        this.percent = gisungAggregationVO.getPercent();
        this.documentNo = gisungAggregationVO.getDocumentNo();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungAggregationAtUpdateGisungAggregation(UserInfo userInfo, GisungAggregationVO gisungAggregationVO) {
        this.year = gisungAggregationVO.getYear();
        this.netCheck = gisungAggregationVO.getNetCheck();
        this.title = gisungAggregationVO.getTitle();
        this.cost = gisungAggregationVO.getCost();
        this.gtype = gisungAggregationVO.getGtype();
        this.percent = gisungAggregationVO.getPercent();
        this.documentNo = gisungAggregationVO.getDocumentNo();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }

    public GisungAggregation(String year
            , long projectId
            , long writerId) {
        this.year = year;
        this.projectId = projectId;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

}
