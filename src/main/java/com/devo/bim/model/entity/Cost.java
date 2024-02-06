package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Cost {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long projectId;
    private String name;
    @Embedded
    private WriteEmbedded writeEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "cost")
    private List<CostSnapShot> costSnapShots = new ArrayList<>();

    public Cost(long projectId, String name, long writerId){
        this.projectId = projectId;
        this.name = name;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public Cost(long costId) {
        this.id=costId;
    }

    public String getMenuItemId()
    {
        return "cost-" + id;
    }

    public String getMenuItemValue()
    {
        return Utils.getDateTimeByNationAndFormatType(writeEmbedded.getWriteDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC) + " " + name;
    }
}
