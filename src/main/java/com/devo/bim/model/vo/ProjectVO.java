package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectVO {
    private String name;
    private String purpose;
    private String term;
    private String address;
    private String startDate;
    private String endDate;
    private String startDueDate;
    private String endDueDate;
    private String startDesignDate;
    private String endDesignDate;

    private String weatherX;
    private String weatherY;
    private String longForecastRegionCode;
    private String longTemperatureRegionCode;

    private String contents;
    private String dashboard_contents;
    private String init_position;
}
