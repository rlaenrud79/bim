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
public class CostItem extends ObjectModelHelper<CostItem> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "costSnapShotId")
    private CostSnapShot costSnapShot;
    private String cell;
    private String cellName;
    private String formula;
    private String formulaReplace;
    private Double resultValue;
    private String result;
    private String calculateStatus;
    private LocalDateTime calculateCompleteDate;
}
