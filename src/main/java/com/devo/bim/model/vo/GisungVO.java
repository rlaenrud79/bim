package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.GisungProcessItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class GisungVO {
    private long id;
    private String year;
    private String month;
    private String title;
    private long projectId;
    private String jobSheetStartDate;
    private String jobSheetEndDate;
    private Integer gisungNo;
    private String status;
    private BigDecimal sumPaidCost = Utils.getDefaultDecimal();
    private BigDecimal sumPaidProgressRate = Utils.getDefaultDecimal();
    private long contractAmount = 0;
    private BigDecimal itemPaidCost = Utils.getDefaultDecimal();
    private BigDecimal sumItemPaidCost = Utils.getDefaultDecimal();
    private BigDecimal itemPaidProgressRate = Utils.getDefaultDecimal();
    private List<Long> gisungProcessItemIds;
}
