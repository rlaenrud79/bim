package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.ScheduleVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @OneToMany(fetch = LAZY, mappedBy = "schedule")
    private List<ScheduleIndex> scheduleIndexes = new ArrayList<>();

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
    private String title;
    private String description;
    private String typeColor;
    private boolean isAllWork = false;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="schedule_work",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "work_id")
    )
    private List<Work> works = new ArrayList<>();

    public void setWork(Work work){
        this.works.add(work);
        work.getSchedules().add(this);
    }

    public void setScheduleIndex(int calYear, int calMonth){
        this.scheduleIndexes.add(new ScheduleIndex(calYear, calMonth, this.id));
    }

    public Schedule(long scheduleId){
        this.id = scheduleId;
    }

    public Schedule(ScheduleVO scheduleVO, int allUseWorkCnt){
        this.startDate = LocalDateTime.parse(scheduleVO.getStartDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endDate = LocalDateTime.parse(scheduleVO.getEndDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.type = scheduleVO.getSelectType();
        this.title = scheduleVO.getTitle();
        this.description = scheduleVO.getDescription();

        setScheduleTypeColor(scheduleVO);

        this.writeEmbedded = new WriteEmbedded(scheduleVO.getUserId());
        this.lastModifyEmbedded = new LastModifyEmbedded(scheduleVO.getUserId());

        for (Long workId : scheduleVO.getScheduleWorkIds()) {
           setWork(new Work(workId));
        }

        if(scheduleVO.getScheduleWorkIds().size() == allUseWorkCnt) this.isAllWork = true;
        else this.isAllWork = false;

    }

    public List<Long> getScheduleWorksIds(){
        return this.works.stream().map(o -> o.getId()).collect(Collectors.toList());
    }

    public void setScheduleAtAdminPutSchedule(ScheduleVO scheduleVO, int allUseWorkCnt){
        if(scheduleVO.getScheduleId() != 0) this.id = scheduleVO.getScheduleId();
        if(!StringUtils.isEmpty(scheduleVO.getStartDate())) this.startDate = LocalDateTime.parse(scheduleVO.getStartDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(!StringUtils.isEmpty(scheduleVO.getEndDate())) this.endDate = LocalDateTime.parse(scheduleVO.getEndDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(!StringUtils.isEmpty(scheduleVO.getSelectType())) this.type = scheduleVO.getSelectType();
        if(!StringUtils.isEmpty(scheduleVO.getTitle())) this.title = scheduleVO.getTitle();
        if(!StringUtils.isEmpty(scheduleVO.getDescription())) this.description = scheduleVO.getDescription();

        setScheduleTypeColor(scheduleVO);

        this.lastModifyEmbedded = new LastModifyEmbedded(scheduleVO.getUserId());

        for (Long workId : scheduleVO.getScheduleWorkIds()) {
            setWork(new Work(workId));
        }

        if(scheduleVO.getScheduleWorkIds().size() == allUseWorkCnt) this.isAllWork = true;
        else this.isAllWork = false;
    }

    private void setScheduleTypeColor(ScheduleVO scheduleVO) {
        this.typeColor = "#6c757d";
        if (scheduleVO.getSelectType().equalsIgnoreCase("HOLIDAY")) this.typeColor = "#dc3545";
        if (scheduleVO.getSelectType().equalsIgnoreCase("WORK")) this.typeColor = "#28a745";
        if (scheduleVO.getSelectType().equalsIgnoreCase("OUTSIDE")) this.typeColor = "#ffc107";
        if (scheduleVO.getSelectType().equalsIgnoreCase("ANNIVERSARY")) this.typeColor = "#b2b3b5";
        if (scheduleVO.getSelectType().equalsIgnoreCase("DAYOFF")) this.typeColor = "#6610f2";
        if (scheduleVO.getSelectType().equalsIgnoreCase("EVENT")) this.typeColor = "#fd7e14";
    }

    public String getStartDateYYYYMMDD(){
        return this.startDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
    }

    public String getEndDateYYYYMMDD(){
        return this.endDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
    }

    public String getWorksNames(long projectId){
        String worksNames = "";
        for (Work work : this.works) {
            if(work.getProjectId() == projectId) {
                worksNames += StringUtils.isEmpty(work.getWorkName(LocaleContextHolder.getLocale().getLanguage())) ? work.getName() + "/" : work.getWorkName(LocaleContextHolder.getLocale().getLanguage()) + "/";
            }
        }
        return StringUtils.removeEnd(worksNames, "/").replace("/", " / ");
    }
}
