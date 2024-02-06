package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchProcessItemCostVO {
    private long workId = 0;
    private String phasingCode = "";
    private String taskName = "";
    private String startDate;
    private String endDate;
    private String cate1="";
    private String cate2="";
    private String cate3="";
    private String cate4="";
    private String cate5="";
    private String cate6="";
    private String upCode="";
    private int cateNo=0;
    private long id = 0;
    private String code = "";
    private Boolean isMinCost = false;
    private String ganttTaskType = "";
    private long subWorkId = 0;
    private String pageMode = "";
}
