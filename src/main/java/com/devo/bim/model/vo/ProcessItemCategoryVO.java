package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessItemCategoryVO {
    private String code;
    private String name;
    private String upCode;
    private long display;
    private String lang;
}
