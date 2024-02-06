package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchJobSheetTemplateVO {

    private long projectId;
    private boolean enabled;
    private String SortProp;
}
