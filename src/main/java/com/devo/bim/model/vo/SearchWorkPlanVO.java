package com.devo.bim.model.vo;

import lombok.Data;

@Data
public class SearchWorkPlanVO {
    private long projectId = 0L;
    private long searchWorkId = 0L;
    private String SortProp = "";
    private String lang = "";
}
