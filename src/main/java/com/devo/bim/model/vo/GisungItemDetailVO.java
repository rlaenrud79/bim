package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GisungItemDetailVO {
    private long id;
    private long gisungId;
    private long gisungItemId;
    private long projectId;
    private String itemDetail01;
    private String itemDetail02;
    private String itemDetail03;
    private String itemDetail04;
    private String itemDetail05;
    private String itemDetail06;
    private String netCheck;
    private String gtype;
}
