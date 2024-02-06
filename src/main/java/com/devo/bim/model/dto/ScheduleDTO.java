package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Schedule;
import com.devo.bim.model.enumulator.WorkStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class ScheduleDTO {
    private long scheduleId;

    private int startYear;
    private int startMonth;
    private int startDay;

    private int endYear;
    private int endMonth;
    private int endDay;

    private boolean allDay;
    private String title;

    private String backgroundColor;
    private String borderColor;

    private boolean isTotalWorks;
    private String worksNames;
    private long writerId;
    private boolean isOwner;

    public ScheduleDTO (Schedule schedule, Long totalWorksCnt, long projectId){
        this.scheduleId = schedule.getId();

        this.startYear = schedule.getStartDate().getYear();
        this.startMonth = schedule.getStartDate().getMonth().getValue();
        this.startDay = schedule.getStartDate().getDayOfMonth();

        this.endYear = schedule.getEndDate().getYear();
        this.endMonth = schedule.getEndDate().getMonth().getValue();
        this.endDay = schedule.getEndDate().getDayOfMonth();

        this.allDay = true;

        this.title = schedule.getTitle();
        this.backgroundColor = schedule.getTypeColor();
        this.borderColor = schedule.getTypeColor();

        this.isTotalWorks = false;
        if(totalWorksCnt == schedule.getWorks().stream().filter(t -> t.getProjectId() == projectId && t.getStatus() == WorkStatus.USE).collect(Collectors.toList()).size()) this.isTotalWorks = true;

        this.worksNames = schedule.getWorksNames(projectId);
        this.writerId = schedule.getWriteEmbedded().getWriter().getId();
        this.isOwner = true;
    }

    public ScheduleDTO (Schedule schedule, Long totalWorksCnt, long sessionId, long projectId){
        this.scheduleId = schedule.getId();

        this.startYear = schedule.getStartDate().getYear();
        this.startMonth = schedule.getStartDate().getMonth().getValue();
        this.startDay = schedule.getStartDate().getDayOfMonth();

        this.endYear = schedule.getEndDate().getYear();
        this.endMonth = schedule.getEndDate().getMonth().getValue();
        this.endDay = schedule.getEndDate().getDayOfMonth();

        this.allDay = true;

        this.title = schedule.getTitle();
        this.backgroundColor = schedule.getTypeColor();
        this.borderColor = schedule.getTypeColor();

        this.isTotalWorks = false;
        if(totalWorksCnt == schedule.getWorks().stream().filter(t -> t.getProjectId() == projectId && t.getStatus() == WorkStatus.USE).collect(Collectors.toList()).size()) this.isTotalWorks = true;

        this.worksNames = schedule.getWorksNames(projectId);
        this.writerId = schedule.getWriteEmbedded().getWriter().getId();
        this.isOwner = schedule.getWriteEmbedded().getWriter().getId() == sessionId;
    }
}
