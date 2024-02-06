package com.devo.bim.model.vo;

import com.devo.bim.model.entity.GisungAggregation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class GisungAggregationVO {
    private long id;
    private long projectId;
    private String year;
    private String netCheck;
    private String title;
    private long cost;
    private List<GisungAggregation> data;
    private String gtype;
    private BigDecimal percent;
    private Integer documentNo;
    private BigDecimal prevCost = BigDecimal.ZERO;
    private BigDecimal todayCost = BigDecimal.ZERO;
    private BigDecimal sumCost = BigDecimal.ZERO;
    private BigDecimal sumRate = BigDecimal.ZERO;
}
