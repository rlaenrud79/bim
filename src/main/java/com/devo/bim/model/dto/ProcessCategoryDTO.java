package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ProcessCategory;
import lombok.Data;

@Data
public class ProcessCategoryDTO {
    private String cate1;
    private String cate2;
    private String cate3;
    private String cate4;
    private String cate5;
    private String cate6;
    private String cate7;

    private String name;
    private long display;
    private long rowNum;

    public ProcessCategoryDTO(ProcessCategory processCategory) {
        this.cate1 = processCategory.getId().getCate1();
        this.cate2 = processCategory.getId().getCate2();
        this.cate3 = processCategory.getId().getCate3();
        this.cate4 = processCategory.getId().getCate4();
        this.cate5 = processCategory.getId().getCate5();
        this.cate6 = processCategory.getId().getCate6();
        this.cate7 = processCategory.getId().getCate7();
        this.name = processCategory.getName();
        this.display = processCategory.getDisplay();
        this.rowNum = processCategory.getRowNum();
    }
}
