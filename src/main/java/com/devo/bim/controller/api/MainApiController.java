package com.devo.bim.controller.api;

import com.devo.bim.component.NetworkDevice;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.NetworkDeviceDTO;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.vo.AccountVO;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mainApi")
@RequiredArgsConstructor
public class MainApiController extends AbstractController {

    private final MainService mainService;
    private final AlertService alertService;
    private final AccountService accountService;
    private final WeatherAPIService weatherAPIService;
    private final ProjectService projectService;
    private final NetworkDevice networkDevice;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/getUserPopUpNotice")
    public JsonObject getUserPopUpNotice(){
        return mainService.getUserPopUpNotice();
    }

    @PostMapping("/putNoPopup")
    public JsonObject putNoPopup(@RequestParam(required = true, defaultValue = "0") long alertId){
        return alertService.putNoPopup(alertId);
    }

    @PutMapping("/user")
    public JsonObject putUser(@RequestBody AccountVO accountVO) {
        return accountService.procPutMyInfo(accountVO);
    }

    @GetMapping("/weather/nowForecast")
    public boolean getNowForecast(){
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                weatherAPIService.procNowForecast(project.getId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/weather/shortForecast")
    public boolean getShortForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                weatherAPIService.procShortForecast(project.getId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/weather/longWeatherForecast")
    public boolean getLongWeatherForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                weatherAPIService.procLongWeatherForecast(project.getId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/weather/longTemperatureForecast")
    public boolean getLongTemperatureForecast() {
        try {
            List<Project> projectList = projectService.findEnabledProjectList();
            for (Project project : projectList) {
                weatherAPIService.procLongTemperatureForecast(project.getId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/licenseReference/{email}/{password}")
    public String licenseReference(@PathVariable String email, @PathVariable String password) {

        if( accountService.isSystemAdmin(email, password))
        {
            String result = "";
            List<NetworkDeviceDTO> networkDeviceDTOs = networkDevice.getNetworkDeviceDTO();

            for (NetworkDeviceDTO networkDeviceDTO : networkDeviceDTOs) {
                result += networkDeviceDTO.getMacAddress() + " | " + passwordEncoder.encode(networkDeviceDTO.getMacAddress()) + "<br/>";
            }

            return result.isBlank()? "No server information !!! " : result;
        }

        return "You do not have permission to access server information.";
    }
}
