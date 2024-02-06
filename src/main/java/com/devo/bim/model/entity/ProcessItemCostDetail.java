package com.devo.bim.model.entity;

import com.devo.bim.model.dto.ProcessItemCostDetailDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItemCostDetail {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;

    private String code;
    private String name;
    private BigDecimal count;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal cost;
    private boolean isFirst;
    private BigDecimal paidProgressCount;
    private BigDecimal paidCost;
    @Embedded
    private WriteEmbedded writeEmbedded;
    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public ProcessItemCostDetail(ProcessItem processItem, ProcessItemCostDetailDTO processItemCostDetailDTO, long accountId) {
        this.processItem = processItem;
        update(processItemCostDetailDTO, accountId);
        this.writeEmbedded = new WriteEmbedded(accountId);
    }

    public ProcessItemCostDetail update(ProcessItemCostDetailDTO processItemCostDetailDTO, long accountId) {

        this.code = processItemCostDetailDTO.getCode();
        this.name = processItemCostDetailDTO.getName();
        this.count = processItemCostDetailDTO.getCount();
        this.unit = processItemCostDetailDTO.getUnit();
        this.unitPrice = processItemCostDetailDTO.getUnitPrice();
        this.cost = processItemCostDetailDTO.getCost();
        this.isFirst = processItemCostDetailDTO.isFirst();

        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);

        return this;
    }
}

