package com.devo.bim.component;

import com.devo.bim.model.entity.Project;
import com.devo.bim.service.ConfigService;
import com.devo.bim.service.ProjectService;
import com.devo.bim.service.WeatherAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final WeatherAPIService weatherAPIService;
    private final ProjectService projectService;
    private final ConfigService configService;

    @Scheduled(cron = "0 10/20 * * * *")
    public void nowForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                String weatherSourceConfig = configService.findWeatherSourceConfig(project.getId());
                if (weatherSourceConfig.equals("KOR")) {
                    weatherAPIService.procNowForecast(project.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 15 * * * *")
    public void shortForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                String weatherSourceConfig = configService.findWeatherSourceConfig(project.getId());
                if (weatherSourceConfig.equals("KOR")) {
                    weatherAPIService.procShortForecast(project.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 5 2/2 * * *")
    public void longWeatherForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                String weatherSourceConfig = configService.findWeatherSourceConfig(project.getId());
                if (weatherSourceConfig.equals("KOR")) {
                    weatherAPIService.procLongWeatherForecast(project.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 5 2/2 * * *")
    public void longTemperatureForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                String weatherSourceConfig = configService.findWeatherSourceConfig(project.getId());
                if (weatherSourceConfig.equals("KOR")) {
                    weatherAPIService.procLongTemperatureForecast(project.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 5 1/12 * * *")
    public void oldForecast(){
        try{
            weatherAPIService.deleteOldForecast();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
