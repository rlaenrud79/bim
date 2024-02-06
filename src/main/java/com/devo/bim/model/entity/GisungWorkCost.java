package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.GisungWorkCostDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungWorkCost extends ObjectModelHelper<GisungWorkCost> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private long projectId;

    private Integer gisungNo;
    private String year;
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal totalPaidCost = Utils.getDefaultDecimal();

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public void setGisungWorkCostAtAddGisung(UserInfo userInfo, Work work, Gisung gisung, GisungWorkCostDTO gisungWorkCostDTO) {
        this.gisung = gisung;
        this.gisungNo = gisung.getGisungNo();
        this.work = work;
        this.projectId = gisungWorkCostDTO.getProjectId();
        this.year = gisung.getYear();
        this.paidCost = gisungWorkCostDTO.getPaidCost();
        this.prevPaidCost = gisungWorkCostDTO.getPrevPaidCost();
        this.totalPaidCost = gisungWorkCostDTO.getTotalPaidCost();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungWorkCostAtUpdateGisung(UserInfo userInfo, Gisung gisung, GisungWorkCostDTO gisungWorkCostDTO) {
        this.paidCost = gisungWorkCostDTO.getPaidCost();
        this.prevPaidCost = gisungWorkCostDTO.getPrevPaidCost();
        this.totalPaidCost = gisungWorkCostDTO.getTotalPaidCost();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
