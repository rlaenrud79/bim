package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSheetProcessItemVO {
    private long id;
    private String taskName;
    private String taskFullPath;
    private long cost;
    private long beforeProgressAmount;
    private String beforeProgressRate;
    private long todayProgressAmount;
    private String todayProgressRate;
    private long afterProgressAmount;
    private String afterProgressRate;
}
