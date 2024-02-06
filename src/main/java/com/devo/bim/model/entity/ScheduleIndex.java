package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ScheduleIndex {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private int calYear;
    private int calMonth;
    private String calYearMonth;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="schedule_id")
    private Schedule schedule;

    public ScheduleIndex(int calYear, int calMonth, long scheduleId){
        this.calYear = calYear;
        this.calMonth = calMonth;
        this.schedule = new Schedule(scheduleId);
        if(calMonth < 10) this.calYearMonth = calYear + "-0" + calMonth;
        else this.calYearMonth = calYear + "-" + calMonth;
    }
}
