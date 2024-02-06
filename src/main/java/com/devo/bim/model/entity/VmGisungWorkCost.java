package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class VmGisungWorkCost {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private Integer gisungNo;
    private long projectId;
    private String year;
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal totalPaidCost = Utils.getDefaultDecimal();
    private long workTotalAmount;
    private long workAmount;
}
