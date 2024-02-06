package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.GisungCompareResult;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class VmGisungProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private String phasingCode;
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal progressRate = Utils.getDefaultDecimal();
    private BigDecimal compareCost = Utils.getDefaultDecimal();
    private BigDecimal compareProgressRate = Utils.getDefaultDecimal();

    @Enumerated(STRING)
    private GisungCompareResult compareResult = GisungCompareResult.NONE;

    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private String addItem;
    private String taskName;
    private BigDecimal taskCost = Utils.getDefaultDecimal();
    private Long rowNum = 0L;
    private Long parentRowNum = 0L;
    private String cate1;
    private String cate2;
    private String cate3;
    private String cate4;
    private String cate5;
    private String cate6;
    private String cate1Name;
    private String cate2Name;
    private String cate3Name;
    private String cate4Name;
    private String cate5Name;
    private String cate6Name;
    private Long cate1Display;
    private Long cate2Display;
    private Long cate3Display;
    private Long cate4Display;
    private Long cate5Display;
    private Long cate6Display;
}
