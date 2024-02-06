package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Cost;
import com.devo.bim.model.entity.CostSnapShot;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Getter;

@Getter
public class CostSnapShotDTO {
    private long costSnapShotId;
    private long costId;
    private String costName;
    private String data;
    private String description;
    private long writerId;
    private String writeDate;
    private long lastModifierId;
    private String lastModifyDate;
    private Double calculateRate;
    private String calculateStatus;

    public CostSnapShotDTO(CostSnapShot costSnapShot){
        this.costSnapShotId = costSnapShot.getId();
        this.costId = costSnapShot.getCost().getId();
        this.costName = costSnapShot.getCost().getName();
        this.data = costSnapShot.getData();
        this.description = costSnapShot.getDescription();
        this.writerId = costSnapShot.getWriteEmbedded().getWriter().getId();
        this.writeDate = Utils.getDateTimeByNationAndFormatType(costSnapShot.getWriteEmbedded().getWriteDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
        this.lastModifierId = costSnapShot.getLastModifyEmbedded().getLastModifier().getId();
        this.lastModifyDate = Utils.getDateTimeByNationAndFormatType(costSnapShot.getLastModifyEmbedded().getLastModifyDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
    }

    public CostSnapShotDTO(Cost cost){
        this.costSnapShotId = 0;
        this.costId = cost.getId();
        this.costName = cost.getName();
        this.data = "";
        this.description = "";
        this.writerId = cost.getWriteEmbedded().getWriter().getId();
        this.writeDate = Utils.getDateTimeByNationAndFormatType(cost.getWriteEmbedded().getWriteDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
        this.lastModifierId =  cost.getWriteEmbedded().getWriter().getId();
        this.lastModifyDate = Utils.getDateTimeByNationAndFormatType(cost.getWriteEmbedded().getWriteDate(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
    }
}
