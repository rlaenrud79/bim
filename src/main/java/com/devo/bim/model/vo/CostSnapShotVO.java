package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CostSnapShotVO {

    private long costId;
    private long costSnapShotId;
    private String data;
    private String description;
}