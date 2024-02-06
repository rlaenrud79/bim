package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
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
public class GisungProcessItemCostTarget extends ObjectModelHelper<Gisung> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private String code;
    private BigDecimal count = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal targetCount = Utils.getDefaultDecimal();
}
