package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HolidayClassifiedWorkDTO {
    private String workId;
    private String holidays;

    public HolidayClassifiedWorkDTO (String workId, String holiday){
        this.workId = workId;
        this.holidays = holiday;
    }
}
