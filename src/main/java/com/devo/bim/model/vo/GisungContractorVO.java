package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GisungContractorVO {
    private long id;
    private long gisungId;
    private String company;
    private String name;
    private String staff;
    private String company2;
    private String name2;
    private Integer documentNo;
    private String stampPath;
    private String stampPath2;
}
