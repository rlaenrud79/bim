package com.devo.bim.controller.modal;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.ScheduleVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/adminModal")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_PROJECT")
public class AdminModalController extends AbstractController {
    private final CompanyRoleService companyRoleService;
    private final WorkService workService;
    private final WorkAmountService workAmountService;
    private final WorkPlanService workPlanService;
    private final ProjectService projectService;
    private final ConfigService configService;
    private final ScheduleService scheduleService;

    @GetMapping("/addCompanyRole")
    public String addCompanyRole(Model model) {

        return "admin/modal/addCompanyRole";
    }

    @GetMapping("/updateCompanyRole")
    public String adminCompanyRoleUpdate(@RequestParam(required = false, defaultValue = "0") long mCompanyRoleId
            , Model model) {

        CompanyRole companyRole = companyRoleService.findById(mCompanyRoleId);

        model.addAttribute("companyRole", companyRole);
        model.addAttribute("companyRoleNameEN", companyRole.getCompanyRoleName("EN"));
        model.addAttribute("companyRoleNameKO", companyRole.getCompanyRoleName("KO"));
        model.addAttribute("companyRoleNameZH", companyRole.getCompanyRoleName("ZH"));
        return "admin/modal/updateCompanyRole";

    }

    @GetMapping("/addWork")
    public String addWork(Model model) {

        model.addAttribute("ptype", "write");
        model.addAttribute("works", workService.findUseWorkAllUp());

        Work work = new Work();
        model.addAttribute("work", work);
        model.addAttribute("upId", 0);
        model.addAttribute("workNameEN", "");
        model.addAttribute("workNameKO", "");
        model.addAttribute("workNameZH", "");
        model.addAttribute("workNameJP", "");

        return "admin/modal/addWork";
    }

    @GetMapping("/updateWork")
    public String adminWorkUpdate(@RequestParam(required = false, defaultValue = "0") long mWorkId, Model model) {

        Work work = workService.findById(mWorkId);

        model.addAttribute("ptype", "edit");
        model.addAttribute("works", workService.findUseWorkAllUp());
        model.addAttribute("work", work);
        model.addAttribute("upId", work.getUpId());
        model.addAttribute("workNameEN", work.getWorkName("EN"));
        model.addAttribute("workNameKO", work.getWorkName("KO"));
        model.addAttribute("workNameZH", work.getWorkName("ZH"));
        model.addAttribute("workNameJP", work.getWorkName("JP"));

        return "admin/modal/addWork";
    }

    /**
     * 공종 총계 등록
     * @param model
     * @return
     */
    @GetMapping("/addWorkAmount")
    public String addWorkAmount(Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        WorkAmount workAmount = new WorkAmount();

        model.addAttribute("ptype", "write");
        model.addAttribute("years", years);
        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("workAmount", workAmount);
        return "admin/modal/addWorkAmount";
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    @GetMapping("/updateWorkAmount")
    public String updateWorkAmount(long id, Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);

        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);
        WorkAmount workAmount = workAmountService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("ptype", "edit");
        model.addAttribute("years", years);
        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("workAmount", workAmount);
        return "admin/modal/addWorkAmount";
    }

    /**
     * 공종 계획 등록
     * @param model
     * @return
     */
    @GetMapping("/addWorkPlan")
    public String addWorkPlan(Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }

        List<String> months = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            String monthValue = String.format("%02d", i);
            months.add(monthValue);
        }
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);
        WorkPlan workPlan = new WorkPlan();

        model.addAttribute("ptype", "write");
        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("workPlan", workPlan);
        return "admin/modal/addWorkPlan";
    }

    @GetMapping("/updateWorkPlan")
    public String updateWorkPlan(long id, Model model) {
        List<Integer> years = new ArrayList<>();
        for (int year = 2019; year <= LocalDate.now().getYear(); year++) {
            years.add(year);
        }

        List<String> months = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            String monthValue = String.format("%02d", i);
            months.add(monthValue);
        }
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);

        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);
        WorkPlan workPlan = workPlanService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("ptype", "edit");
        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("workPlan", workPlan);
        return "admin/modal/addWorkPlan";
    }

    @GetMapping("/projectImage")
    public String projectImage(Model model) {
        model.addAttribute("projectId", userInfo.getProjectId());
        model.addAttribute("images", projectService.findMyProject().getProjectImages());
        model.addAttribute("imgFileExtension", String.join("||", configService.findFileExtension("IMAGE_FILE_EXT", true, userInfo.getProjectId())));
        return "admin/modal/projectImage";
    }

    @GetMapping("/projectWeatherLocation")
    public String projectWeatherLocation(
            @RequestParam(defaultValue = "") String region1Name
            ,@RequestParam(defaultValue = "") String region2Name
            , Model model) {
        model.addAttribute("region1s", configService.findWeatherLocationRegion("1","",""));
        model.addAttribute("region2s", configService.findWeatherLocationRegion("2",region1Name,""));
        model.addAttribute("region3s", configService.findWeatherLocationRegion("3",region1Name, region2Name));
        model.addAttribute("region1Name", region1Name);
        model.addAttribute("region2Name", region2Name);
        return "admin/modal/projectWeatherLocation";
    }

    @GetMapping("/addSchedule")
    public String addSchedule(Model model) {

        model.addAttribute("scheduleVO", new ScheduleVO(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        model.addAttribute("scheduleTypes", configService.findScheduleConfigs());
        model.addAttribute("works", workService.findUseWorkAll());
        return "admin/modal/addSchedule";
    }

    @GetMapping("/updateSchedule")
    public String updateSchedule(@RequestParam(required = false, defaultValue = "0") long mScheduleId
            , Model model) {
        Schedule scheduleItem = scheduleService.findById(mScheduleId);
        model.addAttribute("scheduleItem", scheduleItem);
        model.addAttribute("scheduleTypes", configService.findScheduleConfigs());
        model.addAttribute("works", workService.findUseWorkAll());
        model.addAttribute("isTotalWorksSelected", Utils.isTotalWorksSelected(workService.findUseWorkAll(), scheduleItem.getWorks().size()));
        return "admin/modal/updateSchedule";
    }
}
