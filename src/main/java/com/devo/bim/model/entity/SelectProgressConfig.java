package com.devo.bim.model.entity;

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
public class SelectProgressConfig {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String name;

    private BigDecimal amount = Utils.getDefaultDecimal();

    private Integer sorder;

    private long processId;

    private String ctype;
}
