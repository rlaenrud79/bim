package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchProcessItemCategoryVO {
    private String code="";
    private String name = "";
    private String upCode="";
    private long display = 0;
    private int cateNo = 0;
    private String cate1 = "";
    private String cate2 = "";
    private String cate3 = "";
    private String cate4 = "";
    private String cate5 = "";
    private String cate6 = "";
    private long processItemId = 0;
    private String taskType = "";
}
