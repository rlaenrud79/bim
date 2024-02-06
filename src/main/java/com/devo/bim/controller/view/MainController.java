package com.devo.bim.controller.view;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.CalendarDayVO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.devo.bim.model.vo.WorkAmountVO;
import com.devo.bim.service.*;
import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.JsonArray;
import java.util.Calendar;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController extends AbstractController {
    private final ProjectService projectService;
    private final ProcessService processService;
    private final MainService mainService;
    private final AccountService accountService;
    private final WeatherService weatherService;
    private final ConfigService configService;
    private final JobSheetTemplateService jobSheetTemplateService;
    private final WorkService workService;
    private final WorkAmountService workAmountService;
    private final WorkPlanService workPlanService;
    private final GisungService gisungService;
    private final JobSheetService jobSheetService;
    private final ProjectController projectController;

    @GetMapping("/main/index")
    public String index(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) throws Exception {

        Project project = projectService.findById();
        long currentProcessInfoId = processService.getCurrentVersionProcessInfo().getId();
        LocalDateTime dateTime = LocalDateTime.now();

        List<HolidayClassifiedWorkDTO> holidayClassifiedWorkDTOs = processService.getHolidaysClassifiedWorkId();
        List<MainIssueNoticeDTO> mainCoWorkIssues = mainService.getMainCoWorkIssues(7);
        List<MainIssueNoticeDTO> mainIssues = mainService.getMainIssues(7);
        List<MainIssueNoticeDTO> mainNotices = mainService.getNotices(7);
        MainProgressDTO mainProgressDTO = processService.getMainProcessRate();
        String isWeatherDisplay = configService.findWeatherDisplayConfig(userInfo.getProjectId());
        String viewerType = configService.findConfig("SYSTEM","BIM_RENDERING_MAIN").getCustomValue();
        List<SimulationDTO> simulationDTOs = processService.findTaskExchangeIdsUtilToday();
        List<SimulationDTO> allDTOs = processService.findTaskExchangeIds();

        mainProgressDTO.setDateProgressRate(project.getStartDate(), project.getEndDate());

        // 공정현황
        // 총계 당해년도
        List<WorkAmountDTO> workAmounts = workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), Utils.todayString(), 0);
        WorkAmountDTO workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), Utils.todayString(), 0, 0);

        // 계획 당해년도
        List<Long> workPlanAmounts = projectController.getWorkPlanAmount(workAmounts, project.getId(), Utils.todayString());
        long workPlanAmountSum = 0; //workPlanAmounts.stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < workPlanAmounts.size(); i++) {
            workPlanAmountSum = workPlanAmountSum + workPlanAmounts.get(i);
        }

        // 당해년도 대비
        BigDecimal workAmountContrastSum = new BigDecimal(BigInteger.ZERO);
        BigDecimal workAmountPaidCostContrastSum = new BigDecimal(BigInteger.ZERO);
        BigDecimal todayAmount = new BigDecimal(workAmountTotal.getTodayAmount());  // 당해년도 실시
        if (workPlanAmountSum > 0) {    // 당해년도 실시 대비
            workAmountContrastSum = todayAmount.divide(new BigDecimal(workPlanAmountSum), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountPaidCostContrastSum = new BigDecimal(workAmountTotal.getPaidCost()).divide(new BigDecimal(workPlanAmountSum), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workAmountContrastSum.setScale(2, RoundingMode.HALF_UP);
        workAmountPaidCostContrastSum.setScale(2, RoundingMode.HALF_UP);

        // 누계
        // 계획 누적
        long workPlanAmountSum2 = workAmountTotal.getPrevAmount() + workPlanAmountSum; //workPlanAmounts.stream().mapToInt(Integer::intValue).sum();
        long workAmountTodaySum = workAmountTotal.getPrevAmount() + workAmountTotal.getTodayAmount();  // 누적 실시
        BigDecimal workAmountContrastTodaySum = new BigDecimal(BigInteger.ZERO);
        long workAmountPaidCostSum = workAmountTotal.getPrevAmount() + workAmountTotal.getPaidCost();  // 누적 기성
        BigDecimal workAmountContrastPaidCostSum = new BigDecimal(BigInteger.ZERO);
        if (workPlanAmountSum2 > 0) {   // 전체누계 대비
            workAmountContrastTodaySum = new BigDecimal(workAmountTodaySum).divide(new BigDecimal(workPlanAmountSum2), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountContrastPaidCostSum = new BigDecimal(workAmountPaidCostSum).divide(new BigDecimal(workPlanAmountSum2), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        BigDecimal workAmountYearTodayAmount = new BigDecimal(BigInteger.ZERO); // 당해년도 공정률
        BigDecimal workAmountYearPaidCost = new BigDecimal(BigInteger.ZERO); // 당해년도 기성률
        if (workAmountTotal.getAmount() > 0) {   // 당해년도 공정률, 당해년도 기성률
            workAmountYearTodayAmount = new BigDecimal(workAmountTotal.getTodayAmount()).divide(new BigDecimal(workAmountTotal.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountYearPaidCost = new BigDecimal(workAmountTotal.getPaidCost()).divide(new BigDecimal(workAmountTotal.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workAmountContrastTodaySum.setScale(2, RoundingMode.HALF_UP);
        workAmountContrastPaidCostSum.setScale(2, RoundingMode.HALF_UP);

        List<WorkAmountVO> workAmountVOS = new ArrayList<>();
        int index = 0;
        for (WorkAmountDTO workAmountDTO : workAmounts) {
            WorkAmountVO workAmountVO = new WorkAmountVO();
            workAmountVO.setWorkNameLocale(workAmountDTO.getWorkNameLocale());
            workAmountVO.setTotalAmount(workAmountDTO.getTotalAmount());
            workAmountVO.setPrevAmount(workAmountDTO.getPrevAmount());
            workAmountVO.setAmount(workAmountDTO.getAmount());
            workAmountVO.setWorkPlanAmount(workPlanAmounts.get(index));
            workAmountVO.setProgressAmount(workAmountDTO.getTodayAmount());
            workAmountVO.setPaidCost(workAmountDTO.getPaidCost());
            workAmountVO.setWorkPlanAmountSum(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index));
            workAmountVO.setProgressAmountSum(workAmountDTO.getPrevAmount() + workAmountDTO.getTodayAmount());
            workAmountVO.setPaidCostSum(workAmountDTO.getPrevAmount() + workAmountDTO.getPaidCost());
            if (workPlanAmounts.get(index) > 0) {
                workAmountVO.setProgressRate(new BigDecimal(workAmountDTO.getTodayAmount()).divide(new BigDecimal(workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setPaidCostRate(new BigDecimal(workAmountDTO.getPaidCost()).divide(new BigDecimal(workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }
            if ((workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)) > 0) {
                workAmountVO.setProgressSumRate(new BigDecimal(workAmountDTO.getPrevAmount() + workAmountDTO.getTodayAmount()).divide(new BigDecimal(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setPaidCostSumRate(new BigDecimal(workAmountDTO.getPrevAmount() + workAmountDTO.getPaidCost()).divide(new BigDecimal(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }
            if (workAmountVO.getAmount() > 0) {
                workAmountVO.setYearProgressAmount(new BigDecimal(workAmountVO.getProgressAmount()).divide(new BigDecimal(workAmountVO.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setYearPaidCost(new BigDecimal(workAmountVO.getPaidCost()).divide(new BigDecimal(workAmountVO.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }

            List<Object[]> processItemDepthList = processService.findProcessItemByWorkIdByTaskDepth(currentProcessInfoId, workAmountDTO.getWorkId(), 3, Utils.todayString());
            List<Object[]> newProcessItemDepthList = new ArrayList<>();
            for (Object[] processItemDepth : processItemDepthList) {
                String[] words = processItemDepth[0].toString().split(">");
                String lastWord = words[words.length - 1];
                Object[] newProcessItemDepth = new Object[3];
                newProcessItemDepth[0] = lastWord;              // task_full_path
                newProcessItemDepth[1] = processItemDepth[1];   // progress_amount
                newProcessItemDepth[2] = processItemDepth[2];   // progress_rate
                newProcessItemDepthList.add(newProcessItemDepth);
            }
            workAmountVO.setProcessItemDepthList(newProcessItemDepthList);

            workAmountVOS.add(workAmountVO);
            index = index + 1;
        }

        // 메인 차트
        List<String> categories = new ArrayList<>();
        List<String> categories2 = new ArrayList<>();
        List<List<Long>> data = new ArrayList<>();
        List<List<Long>> data2 = new ArrayList<>();
        List<Long> gisungData = new ArrayList<>();
        List<Long> jobSheetData = new ArrayList<>();
        List<Long> planData = new ArrayList<>();
        List<Long> planData2 = new ArrayList<>();
        List<String> series = new ArrayList<>();
        List<String> series2 = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        String firstDay = searchProcessItemCostVO.getStartDate();
        String lastDay = searchProcessItemCostVO.getEndDate();
        if (firstDay == null || firstDay.equals("")) {
            firstDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        // 문자열을 LocalDate로 변환
        //LocalDate startDate = LocalDate.parse(firstDay, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1);
        LocalDate startDate = LocalDate.parse(Utils.todayPlusMonthString(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 6개월 전으로 이동
        LocalDate endDate = startDate.minusMonths(6);

        for (LocalDate date = endDate; date.isBefore(startDate); date = date.plusMonths(1)) {

            categories.add(formatter.format(date));
            String year = String.valueOf(date.getYear());
            String month = String.format("%02d", date.getMonthValue());

            // 기성
            gisungData.add(gisungService.getGisungProcessItemSumYearMonthCost(year, month));
            System.out.println(formatter.format(date));
            System.out.println(gisungData);

            // 계획
            BigDecimal yearAmountSum = workAmountService.getWorkAmountByAmountSum(userInfo.getProjectId(), year);
            BigDecimal monthPlanSum = workPlanService.getWorkPlanByMonthRateSum(year, month);
            planData.add(yearAmountSum.multiply(monthPlanSum).divide(new BigDecimal("100"), RoundingMode.HALF_UP).longValue());
            System.out.println(formatter.format(date));
            System.out.println(planData);
        }
        data.add(gisungData);
        data.add(planData);
        System.out.println(data);

        series.add("기성");
        series.add("계획");

        model.addAttribute("gisungChartData", Utils.getChartDataJson("기성 현황", "gisun_status", series, data, categories));


        //firstDay = "2023-11-24";
        startDate = LocalDate.parse(firstDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 10일 전으로 이동
        endDate = startDate.minusDays(10);

        for (LocalDate date = endDate; date.isBefore(startDate); date = date.plusDays(1)) {

            String reportDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String year = String.valueOf(date.getYear());
            String month = String.format("%02d", date.getMonthValue());
            System.out.println(formatter.format(date));
            categories2.add(reportDate);
            System.out.println(reportDate);

            // 실적
            jobSheetData.add(jobSheetService.getJobSheetProcessItemTodayProgressAmountSum(reportDate));
            System.out.println(jobSheetData);

            // 계획
            BigDecimal yearAmountSum = workAmountService.getWorkAmountByAmountSum(userInfo.getProjectId(), year);
            BigDecimal dayPlanSum = workPlanService.getWorkPlanByDayRateSum(year, month);
            planData2.add(yearAmountSum.multiply(dayPlanSum).divide(new BigDecimal("100"), RoundingMode.HALF_UP).longValue());
            System.out.println(planData2);
        }
        data2.add(jobSheetData);
        //data2.add(planData2);
        System.out.println(data2);

        series2.add("실적");
        //series2.add("계획");

        model.addAttribute("jobSheetChartData", Utils.getChartDataJson("실적 현황", "job_sheet_status", series2, data2, categories2));
        /**
        for (long day = Timestamp.valueOf(firstDay + " 00:00:00").getTime(); day <= Timestamp.valueOf(lastDay + " 23:59:59").getTime(); day += 86400000) {
            String date = new SimpleDateFormat("d").format(new Date(day));
            categories.add(date);

            // 발행상품
            search.setLstSimsaResultStatus("2"); // 발행상품
            search.setDate(new SimpleDateFormat("Y-M-d").format(new Date(day)));
            data.add(getTokenProductChartCnt("2", search.getDate()));

            // 발행심사
            search.setLstSimsaResultStatus("1"); // 발행심사
            search.setDate(new SimpleDateFormat("Y-M-d").format(new Date(day)));
            data.add(getTokenProductChartCnt("1", search.getDate()));
        }
         **/

        // 공정현황
        model.addAttribute("workAmountTotal", workAmountTotal);
        model.addAttribute("workPlanAmountSum", workPlanAmountSum); // 당해년도 계획
        model.addAttribute("workAmountContrastSum", workAmountContrastSum); // 당해년도 실시 대비
        model.addAttribute("workAmountPaidCostContrastSum", workAmountPaidCostContrastSum); // 당해년도 기성 대비
        model.addAttribute("workPlanAmountSum2", workPlanAmountSum2);   // 누적 계획
        model.addAttribute("workAmountTodaySum", workAmountTodaySum);   // 누적 실시
        model.addAttribute("workAmountContrastTodaySum", workAmountContrastTodaySum);   // 누적 실시 대비
        model.addAttribute("workAmountPaidCostSum", workAmountTodaySum);   // 누적 기성
        model.addAttribute("workAmountContrastPaidCostSum", workAmountContrastPaidCostSum);   // 누적 기성 대비
        model.addAttribute("workAmountYearTodayAmount", workAmountYearTodayAmount);   // 당해년도 공정률
        model.addAttribute("workAmountYearPaidCost", workAmountYearPaidCost);   // 당해년도 기성률
        model.addAttribute("workAmounts", workAmountVOS);



        model.addAttribute("holidayClassifiedWorkDTOs", holidayClassifiedWorkDTOs);
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        model.addAttribute("modelIds", "LATEST");
        model.addAttribute("viewerType",viewerType);
        model.addAttribute("mainProgressDTO", mainProgressDTO);

        model.addAttribute("allTaskExchangeIds",allDTOs);
        model.addAttribute("taskExchangeIds",simulationDTOs);
        model.addAttribute("today", Utils.todayPlusDayString(1));
        model.addAttribute("curDate", Utils.todayString());

        model.addAttribute("mainCoWorkIssues", mainCoWorkIssues);
        model.addAttribute("mainIssues", mainIssues);
        model.addAttribute("mainNotices", mainNotices);

        model.addAttribute("mainCoWorkIssuesCnt", mainCoWorkIssues.size());
        model.addAttribute("mainIssuesCnt", mainIssues.size());
        model.addAttribute("mainNoticesCnt", mainNotices.size());
        model.addAttribute("isWeatherDisplay", isWeatherDisplay);
        model.addAttribute("nowForecastDTO", weatherService.getNowForecast(dateTime));
        model.addAttribute("todayString", Utils.convertDate(Utils.todayString()));
        model.addAttribute("todayWeekString", Utils.convertDateWithDayOfWeek(Utils.todayString()));


        long duration = Duration.between(project.getStartDate().toLocalDate().atStartOfDay(), project.getEndDate().toLocalDate().atStartOfDay()).toDays();
        long durationDate = Duration.between(project.getStartDate().toLocalDate().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        long remindDate = Duration.between(LocalDate.now().atStartOfDay(), project.getEndDate().toLocalDate().atStartOfDay()).toDays();


        model.addAttribute("duration", duration);
        model.addAttribute("durationDate", durationDate);
        model.addAttribute("remindDate", remindDate);
        model.addAttribute("majorTick", duration / 12);
        model.addAttribute("simulationMovingIntervals", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("simulationMovingUnits", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_UNIT", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("defaultSimulationInterval", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()));
        model.addAttribute("defaultSimulationMovingUnit", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_UNIT", userInfo.getProjectId()));

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processService.getCurrentVersionProcessInfo());
        model.addAttribute("workNames", workService.findByLanguageCode());
        if (projectService.findMyProject().getProjectImages().size() > 0) {
            model.addAttribute("projectImage", projectService.findMyProject().getProjectImages().get(0).getPath());
        } else {
            model.addAttribute("projectImage", "");
        }
        if (projectService.findMyProject().getName() != null) {
            model.addAttribute("projectName", projectService.findMyProject().getName());
        } else {
            model.addAttribute("projectName", "");
        }

        if (projectService.findMyProject().getDashboard_contents() != null) {
            model.addAttribute("dashboardContents", projectService.findMyProject().getDashboard_contents());
        } else {
            model.addAttribute("dashboardContents", "");
        }

        if (projectService.findMyProject().getInit_position() != null) {
            model.addAttribute("initPosition", projectService.findMyProject().getInit_position());
        } else {
            model.addAttribute("initPosition", "");
        }

        model.addAttribute("processAvg", processService.getProcessItemProgressRateAvg(currentProcessInfoId));

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        List<CalendarDayVO> days = Utils.generateDays(year, month);
        model.addAttribute("days", days);

        List<ViewJobSheetProcessItem> jobSheetProcessItemList = jobSheetService.getJobSheetProcessItemReportDateListLimit(Utils.todayString());
        model.addAttribute("processItemList", jobSheetProcessItemList);
        model.addAttribute("calendarHtml", jobSheetService.generateCalendarHtml(year, month));
        model.addAttribute("tab", "J");
        model.addAttribute("prevDate", Utils.todayMinusDayString(1));
        model.addAttribute("nextDate", Utils.todayPlusDayString(1));

        model.addAttribute("prevDateString", Utils.convertDate(Utils.todayMinusDayString(1)));
        model.addAttribute("deviceList", jobSheetService.getJobSheetProcessItemDeviceDate(Utils.todayMinusDayString(1)));
        model.addAttribute("workerList", jobSheetService.getJobSheetProcessItemWorkerDate(Utils.todayMinusDayString(1)));
        model.addAttribute("deviceSum", jobSheetService.getJobSheetProcessItemDeviceAmountSum(Utils.todayMinusDayString(1)));
        model.addAttribute("workerSum", jobSheetService.getJobSheetProcessItemWorkerAmountSum(Utils.todayMinusDayString(1)));
        //model.addAttribute("deviceList", jobSheetService.getJobSheetProcessItemDeviceDate("2023-11-28"));
        //model.addAttribute("workerList", jobSheetService.getJobSheetProcessItemWorkerDate("2023-11-28"));
        //model.addAttribute("deviceSum", jobSheetService.getJobSheetProcessItemDeviceAmountSum("2023-11-28"));
        //model.addAttribute("workerSum", jobSheetService.getJobSheetProcessItemWorkerAmountSum("2023-11-28"));

        return "main/index";
    }

    @GetMapping("/mainModel/index")
    public String mainModel(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) throws Exception {

        Project project = projectService.findById();
        long currentProcessInfoId = processService.getCurrentVersionProcessInfo().getId();
        LocalDateTime dateTime = LocalDateTime.now();

        List<HolidayClassifiedWorkDTO> holidayClassifiedWorkDTOs = processService.getHolidaysClassifiedWorkId();
        List<MainIssueNoticeDTO> mainCoWorkIssues = mainService.getMainCoWorkIssues(7);
        List<MainIssueNoticeDTO> mainIssues = mainService.getMainIssues(7);
        List<MainIssueNoticeDTO> mainNotices = mainService.getNotices(7);
        MainProgressDTO mainProgressDTO = processService.getMainProcessRate();
        String isWeatherDisplay = configService.findWeatherDisplayConfig(userInfo.getProjectId());
        String viewerType = configService.findConfig("SYSTEM","BIM_RENDERING_MAIN").getCustomValue();
        List<SimulationDTO> simulationDTOs = processService.findTaskExchangeIdsUtilToday();
        List<SimulationDTO> allDTOs = processService.findTaskExchangeIds();

        mainProgressDTO.setDateProgressRate(project.getStartDate(), project.getEndDate());

        // 공정현황
        // 총계 당해년도
        List<WorkAmountDTO> workAmounts = workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), Utils.todayString(), 0);
        WorkAmountDTO workAmountTotal = workAmountService.findSumWorkAmountListForYear(project.getId(), Utils.todayString(), 0, 0);

        // 계획 당해년도
        List<Long> workPlanAmounts = projectController.getWorkPlanAmount(workAmounts, project.getId(), Utils.todayString());
        long workPlanAmountSum = 0; //workPlanAmounts.stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < workPlanAmounts.size(); i++) {
            workPlanAmountSum = workPlanAmountSum + workPlanAmounts.get(i);
        }

        // 당해년도 대비
        BigDecimal workAmountContrastSum = new BigDecimal(BigInteger.ZERO);
        BigDecimal workAmountPaidCostContrastSum = new BigDecimal(BigInteger.ZERO);
        BigDecimal todayAmount = new BigDecimal(workAmountTotal.getTodayAmount());  // 당해년도 실시
        if (workPlanAmountSum > 0) {    // 당해년도 실시 대비
            workAmountContrastSum = todayAmount.divide(new BigDecimal(workPlanAmountSum), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountPaidCostContrastSum = new BigDecimal(workAmountTotal.getPaidCost()).divide(new BigDecimal(workPlanAmountSum), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workAmountContrastSum.setScale(2, RoundingMode.HALF_UP);
        workAmountPaidCostContrastSum.setScale(2, RoundingMode.HALF_UP);

        // 누계
        // 계획 누적
        long workPlanAmountSum2 = workAmountTotal.getPrevAmount() + workPlanAmountSum; //workPlanAmounts.stream().mapToInt(Integer::intValue).sum();
        long workAmountTodaySum = workAmountTotal.getPrevAmount() + workAmountTotal.getTodayAmount();  // 누적 실시
        BigDecimal workAmountContrastTodaySum = new BigDecimal(BigInteger.ZERO);
        long workAmountPaidCostSum = workAmountTotal.getPrevAmount() + workAmountTotal.getPaidCost();  // 누적 기성
        BigDecimal workAmountContrastPaidCostSum = new BigDecimal(BigInteger.ZERO);
        if (workPlanAmountSum2 > 0) {   // 전체누계 대비
            workAmountContrastTodaySum = new BigDecimal(workAmountTodaySum).divide(new BigDecimal(workPlanAmountSum2), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountContrastPaidCostSum = new BigDecimal(workAmountPaidCostSum).divide(new BigDecimal(workPlanAmountSum2), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        BigDecimal workAmountYearTodayAmount = new BigDecimal(BigInteger.ZERO); // 당해년도 공정률
        BigDecimal workAmountYearPaidCost = new BigDecimal(BigInteger.ZERO); // 당해년도 기성률
        if (workAmountTotal.getAmount() > 0) {   // 당해년도 공정률, 당해년도 기성률
            workAmountYearTodayAmount = new BigDecimal(workAmountTotal.getTodayAmount()).divide(new BigDecimal(workAmountTotal.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            workAmountYearPaidCost = new BigDecimal(workAmountTotal.getPaidCost()).divide(new BigDecimal(workAmountTotal.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workAmountContrastTodaySum.setScale(2, RoundingMode.HALF_UP);
        workAmountContrastPaidCostSum.setScale(2, RoundingMode.HALF_UP);

        List<WorkAmountVO> workAmountVOS = new ArrayList<>();
        int index = 0;
        for (WorkAmountDTO workAmountDTO : workAmounts) {
            WorkAmountVO workAmountVO = new WorkAmountVO();
            workAmountVO.setWorkNameLocale(workAmountDTO.getWorkNameLocale());
            workAmountVO.setTotalAmount(workAmountDTO.getTotalAmount());
            workAmountVO.setPrevAmount(workAmountDTO.getPrevAmount());
            workAmountVO.setAmount(workAmountDTO.getAmount());
            workAmountVO.setWorkPlanAmount(workPlanAmounts.get(index));
            workAmountVO.setProgressAmount(workAmountDTO.getTodayAmount());
            workAmountVO.setPaidCost(workAmountDTO.getPaidCost());
            workAmountVO.setWorkPlanAmountSum(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index));
            workAmountVO.setProgressAmountSum(workAmountDTO.getPrevAmount() + workAmountDTO.getTodayAmount());
            workAmountVO.setPaidCostSum(workAmountDTO.getPrevAmount() + workAmountDTO.getPaidCost());
            if (workPlanAmounts.get(index) > 0) {
                workAmountVO.setProgressRate(new BigDecimal(workAmountDTO.getTodayAmount()).divide(new BigDecimal(workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setPaidCostRate(new BigDecimal(workAmountDTO.getPaidCost()).divide(new BigDecimal(workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }
            if ((workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)) > 0) {
                workAmountVO.setProgressSumRate(new BigDecimal(workAmountDTO.getPrevAmount() + workAmountDTO.getTodayAmount()).divide(new BigDecimal(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setPaidCostSumRate(new BigDecimal(workAmountDTO.getPrevAmount() + workAmountDTO.getPaidCost()).divide(new BigDecimal(workAmountDTO.getPrevAmount() + workPlanAmounts.get(index)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }
            if (workAmountVO.getAmount() > 0) {
                workAmountVO.setYearProgressAmount(new BigDecimal(workAmountVO.getProgressAmount()).divide(new BigDecimal(workAmountVO.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                workAmountVO.setYearPaidCost(new BigDecimal(workAmountVO.getPaidCost()).divide(new BigDecimal(workAmountVO.getAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
            }

            List<Object[]> processItemDepthList = processService.findProcessItemByWorkIdByTaskDepth(currentProcessInfoId, workAmountDTO.getWorkId(), 3, Utils.todayString());
            List<Object[]> newProcessItemDepthList = new ArrayList<>();
            for (Object[] processItemDepth : processItemDepthList) {
                String[] words = processItemDepth[0].toString().split(">");
                String lastWord = words[words.length - 1];
                Object[] newProcessItemDepth = new Object[3];
                newProcessItemDepth[0] = lastWord;              // task_full_path
                newProcessItemDepth[1] = processItemDepth[1];   // progress_amount
                newProcessItemDepth[2] = processItemDepth[2];   // progress_rate
                newProcessItemDepthList.add(newProcessItemDepth);
            }
            workAmountVO.setProcessItemDepthList(newProcessItemDepthList);

            workAmountVOS.add(workAmountVO);
            index = index + 1;
        }

        // 공정현황
        model.addAttribute("workAmountTotal", workAmountTotal);
        model.addAttribute("workPlanAmountSum", workPlanAmountSum); // 당해년도 계획
        model.addAttribute("workAmountContrastSum", workAmountContrastSum); // 당해년도 실시 대비
        model.addAttribute("workAmountPaidCostContrastSum", workAmountPaidCostContrastSum); // 당해년도 기성 대비
        model.addAttribute("workPlanAmountSum2", workPlanAmountSum2);   // 누적 계획
        model.addAttribute("workAmountTodaySum", workAmountTodaySum);   // 누적 실시
        model.addAttribute("workAmountContrastTodaySum", workAmountContrastTodaySum);   // 누적 실시 대비
        model.addAttribute("workAmountPaidCostSum", workAmountTodaySum);   // 누적 기성
        model.addAttribute("workAmountContrastPaidCostSum", workAmountContrastPaidCostSum);   // 누적 기성 대비
        model.addAttribute("workAmountYearTodayAmount", workAmountYearTodayAmount);   // 당해년도 공정률
        model.addAttribute("workAmountYearPaidCost", workAmountYearPaidCost);   // 당해년도 기성률
        model.addAttribute("workAmounts", workAmountVOS);



        model.addAttribute("holidayClassifiedWorkDTOs", holidayClassifiedWorkDTOs);
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        model.addAttribute("modelIds", "LATEST");
        model.addAttribute("viewerType",viewerType);
        model.addAttribute("mainProgressDTO", mainProgressDTO);

        model.addAttribute("allTaskExchangeIds",allDTOs);
        model.addAttribute("taskExchangeIds",simulationDTOs);
        model.addAttribute("today", Utils.todayPlusDayString(1));

        model.addAttribute("mainCoWorkIssues", mainCoWorkIssues);
        model.addAttribute("mainIssues", mainIssues);
        model.addAttribute("mainNotices", mainNotices);

        model.addAttribute("mainCoWorkIssuesCnt", mainCoWorkIssues.size());
        model.addAttribute("mainIssuesCnt", mainIssues.size());
        model.addAttribute("mainNoticesCnt", mainNotices.size());
        model.addAttribute("isWeatherDisplay", isWeatherDisplay);
        model.addAttribute("nowForecastDTO", weatherService.getNowForecast(dateTime));
        model.addAttribute("todayString", Utils.convertDate(Utils.todayString()));


        long duration = Duration.between(project.getStartDate().toLocalDate().atStartOfDay(), project.getEndDate().toLocalDate().atStartOfDay()).toDays();


        model.addAttribute("duration", duration);
        model.addAttribute("majorTick", duration / 12);
        model.addAttribute("simulationMovingIntervals", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("simulationMovingUnits", configService.findConfigForCache("SIMULATION","SIMULATION_MOVING_UNIT", userInfo.getProjectId()).replace(" ", "").split(","));
        model.addAttribute("defaultSimulationInterval", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_INTERVAL", userInfo.getProjectId()));
        model.addAttribute("defaultSimulationMovingUnit", configService.findConfigForCache("SIMULATION","DEFAULT_SIMULATION_MOVING_UNIT", userInfo.getProjectId()));

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processService.getCurrentVersionProcessInfo());
        model.addAttribute("workNames", workService.findByLanguageCode());
        if (projectService.findMyProject().getProjectImages().size() > 0) {
            model.addAttribute("projectImage", projectService.findMyProject().getProjectImages().get(0).getPath());
        } else {
            model.addAttribute("projectImage", "");
        }
        if (projectService.findMyProject().getName() != null) {
            model.addAttribute("projectName", projectService.findMyProject().getName());
        } else {
            model.addAttribute("projectName", "");
        }

        if (projectService.findMyProject().getDashboard_contents() != null) {
            model.addAttribute("dashboardContents", projectService.findMyProject().getDashboard_contents());
        } else {
            model.addAttribute("dashboardContents", "");
        }

        if (projectService.findMyProject().getInit_position() != null) {
            model.addAttribute("initPosition", projectService.findMyProject().getInit_position());
        } else {
            model.addAttribute("initPosition", "");
        }



        return "mainModel/index";
    }

    @GetMapping("/main/weather")
    public String weather(Model model) {
        Project project = projectService.findById();
        LocalDateTime dateTime = LocalDateTime.now();

        model.addAttribute("projectAddress", project.getAddress());
        model.addAttribute("dateTime", dateTime);
        model.addAttribute("nowForecastDTO", weatherService.getNowForecast(dateTime));
        model.addAttribute("shortDaysForecastDTO", weatherService.getShortDaysForecast(dateTime));
        model.addAttribute("shortPeriodForecastDTOs", weatherService.getShortPeriodsForecast(dateTime));
        model.addAttribute("longForecastDTO", weatherService.getLongForecast(dateTime));

        return "main/weather";
    }


    @GetMapping({"/", "/index", "/main"})
    public String main() {
        return "redirect:/main/index";
    }

    @GetMapping("/main/user")
    public String user(Model model) {
        Account account = accountService.findById(userInfo.getId());
        userInfo.setUserInfo(account);
        model.addAttribute("user", account);
        model.addAttribute("imgFileExtension", String.join("||", configService.findFileExtension("IMAGE_FILE_EXT", true, userInfo.getProjectId())));
        setAttributePhoneNoPattern(model);

        getAccountGrantor(model, account);
        getAccountReferences(model, account);
        getJobSheetTemplates(model);

        return "main/user/page";
    }

    private void getAccountReferences(Model model, Account account) {
        List<Account> accountReferences = new ArrayList<>();

        List<Long> ids = account.getAccountReferences()
                .stream()
                .sorted(Comparator.comparingInt(AccountReference::getSortNo))
                .map(t -> t.getReferenceId())
                .collect(Collectors.toList());

        if(ids.size() > 0) accountReferences = accountService.findByIds(ids);
        model.addAttribute("accountReferences", accountReferences);
    }

    private void getAccountGrantor(Model model, Account account) {
        Account accountGrantor = accountService.findById(account.getAccountGrantors()
                .stream()
                .max(Comparator.comparingLong(AccountGrantor::getId))
                .orElseGet(AccountGrantor::new)
                .getGrantorId());
        model.addAttribute("accountGrantor", accountGrantor);
    }

    private void getJobSheetTemplates(Model model) {
        SearchJobSheetTemplateVO searchJobSheetTemplateVO = new SearchJobSheetTemplateVO();
        searchJobSheetTemplateVO.setProjectId(userInfo.getProjectId());
        searchJobSheetTemplateVO.setEnabled(true);
        List<JobSheetTemplateDTO> jobSheetTemplateDTOs = jobSheetTemplateService.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);

        model.addAttribute("jobSheetTemplateList", jobSheetTemplateDTOs);
    }
}
