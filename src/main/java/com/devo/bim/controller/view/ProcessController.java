package com.devo.bim.controller.view;

import com.devo.bim.model.dto.HolidayClassifiedWorkDTO;
import com.devo.bim.model.entity.ProcessInfo;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.service.ProcessService;
import com.devo.bim.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/process")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_PROCESS")
public class ProcessController extends AbstractController {

    private final ProjectService projectService;
    private final ProcessService processService;

    @GetMapping("/index")
    public String index(Model model) {

        Project project = projectService.findById();

        List<HolidayClassifiedWorkDTO> holidayClassifiedWorkDTOs = processService.getHolidaysClassifiedWorkId();
        ProcessInfo processInfo = processService.getCurrentVersionProcessInfo();
        setProcessInfoAndCardCountData(model, processInfo);
        boolean showCostDetailButton = false;

        if(haveRightCostDetailButton()) showCostDetailButton = true;

        model.addAttribute("holidayClassifiedWorkDTOs", holidayClassifiedWorkDTOs);
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("processFileExtension", String.join(",", configService.findFileExtension("PROCESS_FILE_EXT", false, userInfo.getProjectId())));
        model.addAttribute("showCostDetailButton", showCostDetailButton);

        return "process/index/page";
    }

    private boolean haveRightCostDetailButton() {
        if(userInfo.isRoleAdminProject()) return true;
        if(userInfo.isRoleAdminProcess() && userInfo.isRoleAdminEstimate()) return true;
        return false;
    }

    private void setProcessInfoAndCardCountData(Model model, ProcessInfo processInfo) {
        model.addAttribute("processInfo", processInfo);
        model.addAttribute("totalCount", getProcessItemCount(processInfo.getProcessItems(), "TOTAL"));
        model.addAttribute("successProcessItemCnt", getProcessItemCount(processInfo.getProcessItems(), "SUCCESS"));
        model.addAttribute("failProcessItemCnt", getProcessItemCount(processInfo.getProcessItems(), "FAIL"));
        model.addAttribute("noneProcessItemCnt", getProcessItemCount(processInfo.getProcessItems(), "NONE"));
    }

    private int getProcessItemCount(List<ProcessItem> processItems, String cntType){
        List<ProcessItem> totalProcessItems = processItems.stream().filter(t -> t.getTaskStatus() == TaskStatus.REG && !StringUtils.isEmpty(t.getPhasingCode())).collect(Collectors.toList());
        if("SUCCESS".equalsIgnoreCase(cntType)) return (int) totalProcessItems.stream().filter(t -> t.getValidate() == ProcessValidateResult.SUCCESS).count();
        if("FAIL".equalsIgnoreCase(cntType)) return (int) totalProcessItems.stream().filter(t -> t.getValidate() == ProcessValidateResult.FAIL).count();
        if("NONE".equalsIgnoreCase(cntType)) return (int) totalProcessItems.stream().filter(t -> t.getValidate() == ProcessValidateResult.NONE).count();
        return totalProcessItems.size();
    }

    @GetMapping("/divCodeValidateArea")
    public String divCodeValidateArea(Model model) {
        ProcessInfo processInfo = processService.getCurrentVersionProcessInfo();

        setProcessInfoAndCardCountData(model, processInfo);

        return "process/index/codeValidateArea";
    }

    @GetMapping("/simulation")
    public String simulation(Model model) throws Exception {
        Project project = projectService.findById();
        long duration = Duration.between(project.getStartDate().toLocalDate().atStartOfDay(), project.getEndDate().toLocalDate().atStartOfDay()).toDays();
        model.addAttribute("holidayClassifiedWorkDTOs", processService.getHolidaysClassifiedWorkId());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("modelIds", "LATEST");
        model.addAttribute("taskExchangeIds",processService.findTaskExchangeIds());
        model.addAttribute("duration", duration);
        model.addAttribute("majorTick", duration / 12);
        model.addAttribute("viewerType",configService.findConfig("SYSTEM","BIM_RENDERING_SIMULATION").getCustomValue());
        model.addAttribute("simulationMovingIntervals", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("simulationMovingUnits", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_UNIT", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("defaultSimulationInterval", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()));
        model.addAttribute("defaultSimulationMovingUnit", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_UNIT", userInfo.getProjectId()));

        return "process/simulation/page";
    }
}
