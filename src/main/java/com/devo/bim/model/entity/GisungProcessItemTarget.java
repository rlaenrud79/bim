package com.devo.bim.model.entity;

import com.devo.bim.component.Proc;
import com.devo.bim.component.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungProcessItemTarget {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private String code;
    private BigDecimal targetCount = Utils.getDefaultDecimal();
    private BigDecimal afterTargetCount = Utils.getDefaultDecimal();
}
