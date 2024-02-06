package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Schedule;
import com.devo.bim.model.entity.Work;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class HolidayDTO {
    private long holidayId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long holidayCnt = 0L;
    private String title;
    private boolean isAllWork;
    private long workId = 0L;

    public HolidayDTO (Schedule schedule){
        this.holidayId = schedule.getId();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.holidayCnt = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
        this.title = schedule.getTitle();
        this.isAllWork = schedule.isAllWork();
        this.workId = 0L;
    }

    public HolidayDTO (Schedule schedule, Work work) {
        this.holidayId = schedule.getId();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.holidayCnt = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
        this.title = schedule.getTitle();
        this.isAllWork = schedule.isAllWork();
        this.workId = work.getId();
    }
}
