package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DateCountDTO {
    private String date;
    private long count;
    private String monthDay;

    public DateCountDTO(String date, Long count) {
        this.date = date;
        this.count = count;
        this.monthDay = Integer.parseInt(date.substring(5,7)) + "/" + Integer.parseInt(date.substring(8,10));
    }
}
