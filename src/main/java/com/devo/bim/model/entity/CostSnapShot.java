package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.CalculateStatus;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CostSnapShot extends ObjectModelHelper<CostSnapShot> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "costId")
    private Cost cost;
    private String data;
    private String description;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private Double calculateRate;

    @Enumerated(STRING)
    private CalculateStatus calculateStatus;

    public CostSnapShot(Cost cost, String description, String data, long writerId){
        this.cost = cost;
        this.description = description;
        this.data = data;
        this.writeEmbedded = new WriteEmbedded(writerId);
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
    }

    public void updateCostSnapShotData(String data, long writerId){
        this.data = data;
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
    }

    public String getMenuItemId()
    {
        return "costSnapShot-" + id +"-"+ cost.getId();
    }

    public String getMenuItemValue()
    {
        return Utils.getDateTimeByNationAndFormatType(lastModifyEmbedded.getLastModifyDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC) + " " + description;
    }
}
