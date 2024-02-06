package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MySnapShotVO {
    private String type;
    private long id;
    private long snapShotId;

    public boolean isCoWork() {
        return "coWork".equals(this.type);
    }

    public boolean isJobSheet() {
        return "jobSheet".equals(this.type);
    }

}
