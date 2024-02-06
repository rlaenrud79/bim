package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.GisungProcessItemCostDetailDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.GisungCompareResult;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class GisungProcessItemCostDetail {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_process_item_id")
    private GisungProcessItem gisungProcessItem;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "processItemId")
    private ProcessItem processItem;
    private String code;
    private String name;
    private BigDecimal count = Utils.getDefaultDecimal();
    private String unit;
    private BigDecimal unitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private boolean isFirst;
    @Embedded
    private WriteEmbedded writeEmbedded;
    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;
    private BigDecimal jobSheetProgressCount = Utils.getDefaultDecimal();
    private long jobSheetProgressAmount;
    private BigDecimal paidProgressCount = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal progressCount = Utils.getDefaultDecimal();
    private BigDecimal progressCost = Utils.getDefaultDecimal();
    private BigDecimal compareProgressCount = Utils.getDefaultDecimal();
    private BigDecimal compareProgressCost = Utils.getDefaultDecimal();

    @Enumerated(STRING)
    private GisungCompareResult compareResult = GisungCompareResult.NONE;

    public void setGisungProcessItemCostDetailAtAddGisung(UserInfo userInfo, Gisung gisung, GisungProcessItem gisungProcessItem, GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO
        , ProcessItem processItem) {
        this.gisung = gisung;
        this.gisungProcessItem = gisungProcessItem;
        this.code = gisungProcessItemCostDetailDTO.getCode();
        this.name = gisungProcessItemCostDetailDTO.getName();
        this.count = gisungProcessItemCostDetailDTO.getCount();
        this.unit = gisungProcessItemCostDetailDTO.getUnit();
        this.unitPrice = gisungProcessItemCostDetailDTO.getUnitPrice();
        this.cost = gisungProcessItemCostDetailDTO.getCost();
        this.isFirst = gisungProcessItemCostDetailDTO.isFirst();
        this.jobSheetProgressCount = gisungProcessItemCostDetailDTO.getJobSheetProgressCount();
        this.jobSheetProgressAmount = gisungProcessItemCostDetailDTO.getJobSheetProgressAmount();
        this.paidProgressCount = gisungProcessItemCostDetailDTO.getPaidProgressCount();
        this.paidCost = gisungProcessItemCostDetailDTO.getPaidCost();
        this.progressCount = gisungProcessItemCostDetailDTO.getProgressCount();
        this.progressCost = gisungProcessItemCostDetailDTO.getProgressCost();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.processItem = processItem;
    }

    public void setGisungProcessItemCostDetailAtUpdateGisung(BigDecimal progressCount, BigDecimal progressCost) {
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        //this.paidCost = paidCost;
        //this.paidProgressRate = paidProgressRate;
    }
}
