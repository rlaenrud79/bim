package com.devo.bim.model.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.devo.bim.model.vo.JobSheetVO;
import com.devo.bim.module.ObjectModelHelper;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class JobSheet extends ObjectModelHelper<JobSheet> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String location;

    @Embedded
    private WriteEmbedded writeEmbedded;

    private LocalDateTime reportDate;
    private String title;
    private String contents;
    private String temperatureMax;
    private String temperatureMin;
    private String rainfallAmount;
    private String snowfallAmount;
    private boolean enabled;
    private long groupId;
    private String beforeContents;
    private String todayContents;

    @OneToMany(fetch = LAZY, mappedBy = "jobSheet")
    private List<JobSheetGrantor> jobSheetGrantors = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "jobSheet")
    private List<JobSheetReference> jobSheetReferences = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "jobSheet")
    private List<JobSheetSnapShot> jobSheetSnapShots = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "jobSheet")
    private List<JobSheetFile> jobSheetFiles = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "jobSheet")
    private List<JobSheetProcessItem> jobSheetProcessItems = new ArrayList<>();

    @Enumerated(STRING)
    private JobSheetStatus status;

    public JobSheet(long id) {
        this.id = id;
    }

    public void setJobSheetAtAddJobSheet(UserInfo userInfo, JobSheetVO jobSheetVO) {
        this.projectId = userInfo.getProjectId();
        this.location = jobSheetVO.getLocation();
        this.reportDate = LocalDateTime.parse(jobSheetVO.getReportDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.title = jobSheetVO.getTitle();
        this.contents = jobSheetVO.getContents();
        this.temperatureMax = jobSheetVO.getTemperatureMax();
        this.temperatureMin = jobSheetVO.getTemperatureMin();
        this.rainfallAmount = jobSheetVO.getRainfallAmount();
        this.snowfallAmount = jobSheetVO.getSnowfallAmount();
        this.enabled = true;
        if(JobSheetStatus.GOING.name().equals(jobSheetVO.getStatus())){
            this.status = JobSheetStatus.GOING;
        } else {
            this.status = JobSheetStatus.WRITING;
        }
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.todayContents = jobSheetVO.getTodayContents();
    }

    public JobSheetGrantor getJobSheetGrantor() {
        return this.jobSheetGrantors.stream().findFirst().orElseGet(JobSheetGrantor::new);
    }

    public void setJobSheetAtDenyJobSheet() {
        this.status = JobSheetStatus.REJECT;
    }

    public void setJobSheetAtApproveJobSheet() {
        this.status = JobSheetStatus.COMPLETE;
    }

    public void setJobSheetAtWritingJobSheet() {
        this.status = JobSheetStatus.WRITING;
    }

    public void setJobSheetAtDeleteJobSheet() {
        this.enabled = false;
    }

    public void setJobSheetAtUpdateJobSheet(JobSheetVO jobSheetVO) {
        this.location = jobSheetVO.getLocation();
        this.reportDate = LocalDateTime.parse(jobSheetVO.getReportDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.title = jobSheetVO.getTitle();
        this.contents = jobSheetVO.getContents();
        this.temperatureMax = jobSheetVO.getTemperatureMax();
        this.temperatureMin = jobSheetVO.getTemperatureMin();
        this.rainfallAmount = jobSheetVO.getRainfallAmount();
        this.snowfallAmount = jobSheetVO.getSnowfallAmount();
        if(JobSheetStatus.GOING.name().equals(jobSheetVO.getStatus())){
            this.status = JobSheetStatus.GOING;
        } else {
            this.status = JobSheetStatus.WRITING;
        }
        this.todayContents = jobSheetVO.getTodayContents();
    }

    public void setJobSheetAtReAddJobSheet(UserInfo userInfo, JobSheetVO jobSheetVO, long groupId) {
        this.projectId = userInfo.getProjectId();
        this.location = jobSheetVO.getLocation();
        this.reportDate = LocalDateTime.parse(jobSheetVO.getReportDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.title = jobSheetVO.getTitle();
        this.contents = jobSheetVO.getContents();
        this.temperatureMax = jobSheetVO.getTemperatureMax();
        this.temperatureMin = jobSheetVO.getTemperatureMin();
        this.rainfallAmount = jobSheetVO.getRainfallAmount();
        this.snowfallAmount = jobSheetVO.getSnowfallAmount();
        this.enabled = true;
        if(JobSheetStatus.GOING.name().equals(jobSheetVO.getStatus())){
            this.status = JobSheetStatus.GOING;
        } else {
            this.status = JobSheetStatus.WRITING;
        }
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.todayContents = jobSheetVO.getTodayContents();
        this.groupId = groupId;
    }
}
