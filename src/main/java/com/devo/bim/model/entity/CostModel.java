package com.devo.bim.model.entity;

import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CostModel extends ObjectModelHelper<CostModel> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "costItemId")
    private CostItem costItem;
    private String name;
    private String queryStatus;
    private String queryResultCode;
    private Double value;
    private Long modelingId;
    private LocalDateTime queryDate;
}
