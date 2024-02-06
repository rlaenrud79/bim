package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class VmProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_id")
    private ProcessInfo processInfo;

    private String taskName;
    private int taskDepth;
    private String phasingCode;

    private int duration;
    private String plannedStartDate;
    private String plannedEndDate;
    private String startDate;
    private String endDate;

    private String description;

    @Enumerated(STRING)
    private ProcessValidateResult validate;

    private boolean includeHoliday = true;
    private boolean progressTarget;
    private BigDecimal progressRate;

    @Enumerated(STRING)
    private TaskType ganttTaskType;
    private boolean ganttOpen;
    private String ganttHolder;
    private int ganttSortNo;

    @OneToMany(fetch = LAZY, mappedBy = "processItem")
    private List<ProcessItemLink> processItemLinks = new ArrayList<>();

    private int rowNum;
    private int parentRowNum;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private String taskFullPath = "";

    private Long parentId = 0L;

    @Enumerated(STRING)
    private TaskStatus taskStatus;

    private String firstCountFormula = "";
    private BigDecimal firstCount = Utils.getDefaultDecimal();
    private String firstCountUnit = "";
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidProgressRate = Utils.getDefaultDecimal();
    private boolean isBookmark;
    private Long costWriterId;
    private LocalDateTime costWriteDate;
    private Long costLastModifierId;
    private LocalDateTime costLastModifyDate;
    private String exchangeIds="";
    private Long progressAmount = 0L;

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
