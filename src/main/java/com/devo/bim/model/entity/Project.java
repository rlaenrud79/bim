package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WeatherXY;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.ProjectVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Project extends ObjectModelHelper<Project> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String name;
    private String purpose;
    private String address;
    private String term;

    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime endDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime startDueDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime endDueDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime startDesignDate;
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime endDesignDate;

    @Embedded
    private WeatherXY weatherXY;
    private String contents;
    private int maxUserCount;
    private String dashboard_contents;
    private String init_position;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "project")
    List<ProjectImage> projectImages = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "project")
    List<Company> companies = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "project")
    List<PayHistory> payHistories = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "project")
    List<ProjectLicense> projectLicenses = new ArrayList<>();

    private boolean isValidLicense;

    public Project(long projectId){
        this.id = projectId;
    }

    public PayHistory getLatestPayHistory(){
        return this.payHistories.stream().max(Comparator.comparingLong(PayHistory::getId)).orElseGet(PayHistory::new);
    }

    public void putContents(String contents) {
        this.contents = contents;
    }

    public void putDashboardContents(String dashboard_contents) {
        this.dashboard_contents = dashboard_contents;
    }

    public void putInitPosition(String init_position) {
        this.init_position = init_position;
    }

    public void putProject(ProjectVO projectVO) {
        this.name = projectVO.getName();
        this.purpose = projectVO.getPurpose();
        this.address = projectVO.getAddress();
        this.term = projectVO.getTerm();
        this.startDesignDate = Utils.parseLocalDateTimeEnd(projectVO.getStartDesignDate());
        this.endDesignDate = Utils.parseLocalDateTimeEnd(projectVO.getEndDesignDate());
        this.startDate = Utils.parseLocalDateTimeEnd(projectVO.getStartDate());
        this.endDate = Utils.parseLocalDateTimeEnd(projectVO.getEndDate());
        this.startDueDate = Utils.parseLocalDateTimeEnd(projectVO.getStartDueDate());
        this.endDueDate = Utils.parseLocalDateTimeEnd(projectVO.getEndDueDate());
        this.weatherXY = new WeatherXY(projectVO.getWeatherX(), projectVO.getWeatherY(),projectVO.getLongForecastRegionCode(), projectVO.getLongTemperatureRegionCode());
        this.dashboard_contents = projectVO.getDashboard_contents();
    }

    public void putIsValidLicense(boolean value){
        this.isValidLicense = value;
    }
}
