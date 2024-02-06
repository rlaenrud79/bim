package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ScheduleVO {

    private long scheduleId;

    private List<Long> scheduleWorkIds = new ArrayList<>();

    private String title;
    private String startDate;
    private String endDate;

    private String selectType;
    private String description;

    private long projectId;
    private long userId;

    public ScheduleVO(String today){
        this.startDate = today;
        this.endDate = today;
    }
}
