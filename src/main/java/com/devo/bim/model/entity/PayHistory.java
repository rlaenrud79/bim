package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.PayHistoryVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class PayHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private BigDecimal totalPay;
    private BigDecimal totalCost;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public PayHistory(long projectId, BigDecimal totalPay, BigDecimal totalCost, long writerId) {
        this.project = new Project(projectId);
        this.totalPay = totalPay;
        this.totalCost = totalCost;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }
}
