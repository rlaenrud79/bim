package com.devo.bim.model.vo;

import lombok.Data;

@Data
public class SearchWorkVO {
    private long projectId;
    private String lang;

    private String SortProp;
    private long upId;
}
