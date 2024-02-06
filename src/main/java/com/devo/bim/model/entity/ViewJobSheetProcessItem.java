package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
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

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ViewJobSheetProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private long mySnapShotId = 0;
    private String phasingCode;
    private String taskFullPath;
    private BigDecimal beforeProgressRate;
    private BigDecimal afterProgressRate;
    private BigDecimal todayProgressRate;
    private long beforeProgressAmount;
    private long afterProgressAmount;
    private long todayProgressAmount;
    private long grantorId;
    private LocalDateTime grantDate;
    private String exchangeIds;
    private String taskName;
    private BigDecimal cost = Utils.getDefaultDecimal();
    private Long parentId = 0L;
    private String startDate;
    private String endDate;
    private int duration;
    private String mySnapShotSource;
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> worker;
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private List<Object> device;
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
}
