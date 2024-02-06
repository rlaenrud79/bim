package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.VmJobSheetProcessItemCostDTO;
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
public class GisungProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;
    private String phasingCode;
    private BigDecimal progressRate = Utils.getDefaultDecimal();
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal taskCost = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private String addItem;
    private BigDecimal compareCost = Utils.getDefaultDecimal();
    private BigDecimal compareProgressRate = Utils.getDefaultDecimal();

    @Enumerated(STRING)
    private GisungCompareResult compareResult = GisungCompareResult.NONE;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public void setGisungProcessItemCostAtAddGisung(UserInfo userInfo, Gisung gisung, VmJobSheetProcessItemCostDTO vmJobSheetProcessItemCostDTO, ProcessItem processItem) {
        this.gisung = gisung;
        this.phasingCode = vmJobSheetProcessItemCostDTO.getPhasingCode();
        this.complexUnitPrice = vmJobSheetProcessItemCostDTO.getComplexUnitPrice();
        this.taskCost = vmJobSheetProcessItemCostDTO.getTaskCost();
        this.paidCost = vmJobSheetProcessItemCostDTO.getPaidCost(); // 기성금액
        this.paidProgressRate = vmJobSheetProcessItemCostDTO.getPaidProgressRate(); // 기성 진행률
        this.beforeProgressAmount = vmJobSheetProcessItemCostDTO.getBeforeProgressAmount(); // 전회 실적금액
        this.todayProgressAmount = vmJobSheetProcessItemCostDTO.getTodayProgressAmount();   // 금회 실적금액
        this.afterProgressAmount = vmJobSheetProcessItemCostDTO.getAfterProgressAmount();   // 누적 실적금액
        this.beforeProgressRate = vmJobSheetProcessItemCostDTO.getBeforeProgressRate();     // 전회 실적 진행률
        this.todayProgressRate = vmJobSheetProcessItemCostDTO.getTodayProgressRate();       // 금회 실적 진행률
        this.afterProgressRate = vmJobSheetProcessItemCostDTO.getAfterProgressRate();       // 누적 실적 진행률
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.processItem = processItem;
        this.progressRate = vmJobSheetProcessItemCostDTO.getProgressRate();                 // 금회 기성 진행률
        this.cost = vmJobSheetProcessItemCostDTO.getCost();                                 // 금회 기성금
        this.addItem = vmJobSheetProcessItemCostDTO.getAddItem();                           // 공정추가 여부
    }

    public void setGisungProcessItemCostAtUpdateGisung(BigDecimal cost, BigDecimal progressRate, String addItem) {
        this.cost = cost;
        this.progressRate = progressRate;
        this.addItem = addItem;
        //this.paidCost = paidCost;
        //this.paidProgressRate = paidProgressRate;
    }

    public void setGisungProcessItemCostAtUpdateGisungCost(BigDecimal cost, BigDecimal progressRate) {
        this.cost = cost;
        this.progressRate = progressRate;
    }
}
