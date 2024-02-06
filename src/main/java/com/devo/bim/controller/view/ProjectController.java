package com.devo.bim.controller.view;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.repository.spring.ProcessInfoRepository;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;
import com.devo.bim.model.vo.SearchJobSheetVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController extends AbstractController {

    private final JobSheetTemplateService jobSheetTemplateService;
    private final ProjectService projectService;
    private final AccountService accountService;
    private final JobSheetService jobSheetService;
    private final AlertService alertService;
    private final WeatherService weatherService;
    private final ProcessCostService processCostService;
    private final ProcessService processService;
    private final WorkAmountService workAmountService;
    private final WorkPlanService workPlanService;
    private final ProcessInfoRepository processInfoRepository;

    @GetMapping("/index")
    public String index(Model model) {
        Project project = projectService.findMyProject();

        model.addAttribute("project", project);
        model.addAttribute("projectImages", project.getProjectImages());

        return "project/index/page";
    }

    @GetMapping("/jobSheetList")
    public String jobSheetList(@ModelAttribute SearchJobSheetVO searchJobSheetVO
            , @PageableDefault(size = defaultPageSize_jobSheetList) Pageable pageable
            , Model model
            , HttpServletRequest request
            , HttpServletResponse response) {

        String searchVOCookie = getCookieValue(request, "searchJobSheetVO");
        String searchPageCookie = getCookieValue(request, "searchJobSheetPage");

        if (!searchJobSheetVO.getKeepCondition()) {
            searchVOCookie = null;
            searchPageCookie = null;
        }

        if (searchVOCookie != null) {
            getSearchVOByCookie(searchJobSheetVO, searchVOCookie);
        }

        if (searchPageCookie != null) {
            pageable = getPageableByCookie(searchPageCookie);
        }

        getJobSheetList(searchJobSheetVO, pageable, model, response);

        return "project/jobSheetList/page";
    }

    @GetMapping("/jobSheetListCardBody")
    public String jobSheetListCardBody(@ModelAttribute SearchJobSheetVO searchJobSheetVO
            , @PageableDefault(size = defaultPageSize_jobSheetList) Pageable pageable
            , Model model
            , HttpServletResponse response) {

        getJobSheetList(searchJobSheetVO, pageable, model, response);

        return "project/jobSheetList/cardBody";
    }

    private void getJobSheetList(SearchJobSheetVO searchJobSheetVO, Pageable pageable, Model model, HttpServletResponse response) {
        setProjectIdByUserInfo(searchJobSheetVO);

        PageDTO<JobSheetDTO> pageJobSheetDTOs = jobSheetService.findJobSheetDTOs(searchJobSheetVO, pageable);

        setPagingConfig(model, pageJobSheetDTOs);

        model.addAttribute("searchJobSheetVO", searchJobSheetVO);
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());

        setVOCookie(searchJobSheetVO, "searchJobSheetVO", "/project", response);
        setPageableCookie(pageable, "searchJobSheetPage", "/project", response);
    }

    private void setProjectIdByUserInfo(SearchJobSheetVO searchJobSheetVO) {
        searchJobSheetVO.setProjectId(userInfo.getProjectId());
    }

    @GetMapping("/jobSheetView")
    public String jobSheetView(long id, Model model) {
        JobSheet jobSheet = getJobSheet(id, model);
        getProjectName(model);
        setAlertIsRead(jobSheet);
        //getJobSheetProcessItem(jobSheet, model);
        model.addAttribute("jobSheetProcessItems", jobSheetService.findViewJobSheetProcessItemByJobSheetId(id));

        return "project/jobSheetView/page";
    }

    private void setAlertIsRead(JobSheet jobSheet) {
        alertService.setIsReadTrue(jobSheet.getProjectId(), userInfo.getId(), jobSheet.getId(), AlertType.JOB_SHEET);
    }

    private JobSheet getJobSheet(long id, Model model) {
        JobSheet jobSheet = jobSheetService.findJobSheetByIdAndProjectId(id, userInfo.getProjectId());
        if (jobSheet.getId() == 0) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }
        model.addAttribute("jobSheet", jobSheet);
        model.addAttribute("isWriter", jobSheet.getWriteEmbedded().getWriter().getId() == userInfo.getId());
        model.addAttribute("isGrantor", jobSheet.getJobSheetGrantor().getGrantor().getId() == userInfo.getId()
                && (userInfo.isRoleAdminWork() || userInfo.isRoleAdminProcess() || userInfo.isRoleAdminEstimate())
        );
        model.addAttribute("status", jobSheet.getStatus());
        model.addAttribute("isWriting", (jobSheet.getStatus() == JobSheetStatus.WRITING));
        model.addAttribute("isGoing", (jobSheet.getStatus() == JobSheetStatus.GOING));
        return jobSheet;
    }

    private void getJobSheetProcessItem(JobSheet jobSheet, Model model) {
        List<JobSheetProcessItem> jobSheetProcessItems = jobSheetService.findJobSheetProcessItemByJobSheetId(jobSheet.getId());
        if (jobSheetProcessItems.size() > 0) {
            for (JobSheetProcessItem jobSheetProcessItem : jobSheetProcessItems) {
                List<JobSheetProcessItemWorker> jobSheetProcessItemWorkers = jobSheetService.findJobSheetProcessItemWorkerByJobSheetProcessItemId(jobSheetProcessItem.getId());
                List<Object> list = new ArrayList<>();
                for (JobSheetProcessItemWorker jobSheetProcessItemWorker : jobSheetProcessItemWorkers) {
                    //list.add(new Object[]{"name", jobSheetProcessItemWorker.getWorkerName(), "value", jobSheetProcessItemWorker.getWorkerAmount()});
                    //list.add("{\"name\":\"" + jobSheetProcessItemWorker.getWorkerName() + "\",\"value\":" + jobSheetProcessItemWorker.getWorkerAmount() + "}");
                }
                //System.out.println(list);
                jobSheetProcessItem.setWorker(list);
            }
        }
        model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
    }

    @GetMapping("/jobSheetAdd")
    public String jobSheetAdd(Long id, Model model) {
        JobSheet jobSheet = new JobSheet();
        List<ViewJobSheetProcessItem> jobSheetProcessItems = new ArrayList<>();
        if (id == null) {
            Account account = accountService.findById(userInfo.getId());
            MySnapShot mySnapShot = new MySnapShot();
            model.addAttribute("jobSheet", jobSheet);
            model.addAttribute("snapShots", jobSheet.getJobSheetSnapShots());
            model.addAttribute("snapShot", mySnapShot);
            model.addAttribute("jobSheetFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
            model.addAttribute("user", account);
            model.addAttribute("accountGrantor", accountService.getAccountGrantor());
            model.addAttribute("accountReferences", accountService.getAccountReferences());
            model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
            model.addAttribute("alertMsg", "");
            model.addAttribute("ptype", "write");
            model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());

            getProjectName(model);
            getProjectWeather(model);
        } else {
            if (id > 0) {
                jobSheet = getJobSheet(id, model);
                if (jobSheet.getWriteEmbedded().getWriter().getId() != userInfo.getId()) {
                    throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
                }
                getProjectName(model);

                getJobSheetAndPreviewFileExtension(model);
                jobSheetProcessItems = jobSheetService.findViewJobSheetProcessItemByJobSheetId(id);
                model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
                model.addAttribute("alertMsg", "");
                model.addAttribute("ptype", "edit");
                model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());
            }
        }

        //getJobSheetTemplateList(model);
        return "project/jobSheetAdd/page";
    }

    private void getProjectName(Model model) {
        Project project = projectService.findById();

        model.addAttribute("projectName", project.getName());
        model.addAttribute("location", project.getAddress());
    }

    private void getJobSheetTemplateList(Model model) {
        SearchJobSheetTemplateVO searchJobSheetTemplateVO = new SearchJobSheetTemplateVO();
        searchJobSheetTemplateVO.setProjectId(userInfo.getProjectId());
        searchJobSheetTemplateVO.setEnabled(true);
        List<JobSheetTemplateDTO> jobSheetTemplateDTOs = jobSheetTemplateService.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);

        model.addAttribute("jobSheetTemplateList", jobSheetTemplateDTOs);
    }

    @PostMapping("/renderJobSheetSnapShotAtAdd")
    JsonObject renderJobSheetSnapShotAtAdd(@RequestBody MySnapShotShareVO mySnapShotShareVO) {
        return accountService.findMySnapShotsJobSheetProcessItemJson(mySnapShotShareVO);
        //getSnapShotJobSheetProcessItem(mySnapShotShareVO, model);
        //return "project/jobSheetAdd/snapShot";
    }

    private void getSnapShot(MySnapShotShareVO mySnapShotShareVO, Model model) {
        List<MySnapShot> snapShots = accountService.findMySnapShots(mySnapShotShareVO);
        model.addAttribute("snapShots", snapShots);
    }

    private void getSnapShotJobSheetProcessItem(MySnapShotShareVO mySnapShotShareVO, Model model) {
        MySnapShot snapShot = accountService.findMySnapShotsJobSheetProcessItem(mySnapShotShareVO);
        model.addAttribute("snapShot", snapShot);
    }

    @PostMapping("/renderJobSheetSnapShotAtUpdate")
    public String renderJobSheetSnapShotAtUpdate(@RequestBody MySnapShotShareVO mySnapShotShareVO, Model model) {

        getSnapShotJobSheetProcessItem(mySnapShotShareVO, model);
        return "project/jobSheetUpdate/snapShot";
    }

    @GetMapping("/jobSheetReAdd")
    public String jobSheetReAdd(long id, Model model) {
        getJobSheet(id, model);
        getProjectName(model);
        getJobSheetTemplateList(model);

        getJobSheetAndPreviewFileExtension(model);
        model.addAttribute("jobSheetProcessItems", jobSheetService.findJobSheetProcessItemByJobSheetId(id));
        return "project/jobSheetReAdd/page";
    }

    @GetMapping("/jobSheetUpdate")
    public String jobSheetUpdate(long id, Model model) {

        JobSheet jobSheet = getJobSheet(id, model);
        if (jobSheet.getWriteEmbedded().getWriter().getId() != userInfo.getId()) {
            throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        }

        getProjectName(model);
        getJobSheetTemplateList(model);

        getJobSheetAndPreviewFileExtension(model);
        model.addAttribute("jobSheetProcessItems", jobSheetService.findJobSheetProcessItemByJobSheetId(id));
        model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());
        return "project/jobSheetUpdate/page";
    }

    @GetMapping("/jobSheetCopy")
    public String jobSheetCopy(long id, Model model) {
        getJobSheet(id, model);
        getProjectName(model);
        getJobSheetTemplateList(model);

        getJobSheetAndPreviewFileExtension(model);
        model.addAttribute("jobSheetProcessItems", jobSheetService.findJobSheetProcessItemByJobSheetId(id));
        model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());
        return "project/jobSheetCopy/page";
    }

    private void getJobSheetAndPreviewFileExtension(Model model) {
        model.addAttribute("jobSheetFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
        model.addAttribute("previewFileExtension", String.join("||", configService.findFileExtension("PREVIEW_FILE_EXT", true, userInfo.getProjectId())));
    }

    private void getProjectWeather(Model model) {
        LocalDateTime dateTime = LocalDateTime.now();
        List<ShortPeriodForecastDTO> forecasts = weatherService.getShortDateForecast(dateTime);
        List<ShortPeriodForecastDTO> nowforecasts = weatherService.getNowShortDateForecast(dateTime);
        if (forecasts == null || forecasts.size() < 1) {
            return;
        }
        float pcp = 0, sno = 0;
        String tmx = "", tmn = "";
        for(ShortPeriodForecastDTO forecast: forecasts) {
            if (forecast.getTmx() != null) {
                tmx = forecast.getTmx() + "℃";
            }
            if (forecast.getTmn() != null) {
                tmn = forecast.getTmn() + "℃";
            }
        }
        if (nowforecasts != null && nowforecasts.size() > 1) {
            for (ShortPeriodForecastDTO nowforecast : nowforecasts) {
                float tPcp = parseFloat(nowforecast.getPcp());
                float tSno = parseFloat(nowforecast.getSno());
                // 가장 큰 범위를 표시
                if (pcp < tPcp) {
                    pcp = tPcp;
                }
                if (sno < tSno) {
                    sno = tSno;
                }
            }
        }
        model.addAttribute("temperatureMax", tmx);
        model.addAttribute("temperatureMin", tmn);
        model.addAttribute("rainfallAmount", getRainRangeText(pcp));
        model.addAttribute("snowfallAmount", getSnowRangeText(sno));

    }

    private Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch(NumberFormatException ex) {
            return 0.f;
        }
    }
    private String getRainRangeText(float value) {
        if (value <= 0) {
            return "강수없음";
        } else if (value < 1) {
            return "0.1 ~ 1.0mm 미만";
        } else if (value < 30) {
            return "1.0mm 이상 30.0mm 미만";
        } else if (value < 50) {
            return "30.0 mm 이상 50.0 mm 미만";
        } else {
            return "50.0 mm 이상";
        }
    }
    private String getSnowRangeText(float value) {
        if (value <= 0) {
            return "적설없음";
        } else if (value < 1) {
            return "0.1 ~ 1.0cm 미만";
        } else if (value < 5) {
            return "1.0cm 이상 5.0cm 미만";
        } else {
            return "5.0 cm 이상";
        }
    }

    @GetMapping("/printJobSheetDetail")
    public String printJobSheetDetail(long id, String preview, Model model) {
        getPrintJobSheet(id, preview, model);
        return "project/modal/printJobSheetDetail";
    }

    @GetMapping("/addPrevJobSheetItem")

    public String addPrevJobSheetItem(int itemBun, long id, long jobSheetId, long jobSheetProcessItemId, Model model) {
        ProcessItem processItem = this.jobSheetService.getProcessItem(id);
        List<ViewJobSheetProcessItemDTO> jobSheetProcessItems = this.jobSheetService.findAllByProcessId(id, jobSheetId, userInfo.getId());
        ViewJobSheetProcessItem viewJobSheetProcessItem = new ViewJobSheetProcessItem();
        Integer jobSheetProcessItemCnt = 0;
        if (jobSheetProcessItemId > 0) {
            viewJobSheetProcessItem = this.jobSheetService.findViewJobSheetProcessItemByProcessItemId(jobSheetProcessItemId);
            jobSheetProcessItemCnt = 1;
        } else {
            if (jobSheetProcessItems != null && jobSheetProcessItems.size() > 0) {
                viewJobSheetProcessItem = this.jobSheetService.findViewJobSheetProcessItemByProcessItemId(jobSheetProcessItems.get(0).getId());
                jobSheetProcessItemCnt = 1;
            }

        }
        if (jobSheetProcessItemCnt > 0) {
            model.addAttribute("jobSheetProcessItem", viewJobSheetProcessItem);
        } else {
            model.addAttribute("jobSheetProcessItem", "");
        }
        model.addAttribute("processItem", processItem);
        model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
        model.addAttribute("itemBun", itemBun);
        model.addAttribute("jobSheetProcessItemCnt", jobSheetProcessItemCnt);

        return "project/modal/addPrevJobSheetItem";
    }

    @GetMapping("/prevJobSheet")
    public String prevJobSheet(Model model) {
        JobSheet jobSheet = this.jobSheetService.findJobSheetAndWriterId(userInfo.getId());
        List<ViewJobSheetProcessItem> jobSheetProcessItems = new ArrayList<>();
        if (jobSheet != null && jobSheet.getId() > 0) {
            jobSheet = getJobSheet(jobSheet.getId(), model);

            getProjectName(model);
            //getJobSheetTemplateList(model);

            getJobSheetAndPreviewFileExtension(model);
            jobSheetProcessItems = jobSheetService.findViewJobSheetProcessItemByJobSheetId(jobSheet.getId());
            model.addAttribute("accountGrantor", accountService.getAccountGrantor());
            model.addAttribute("accountReferences", accountService.getAccountReferences());
            model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
            model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());
            model.addAttribute("alertmsg", "ok_prev_job_sheet");
            model.addAttribute("ptype", "write");
            return "project/jobSheetAdd/prevJobSheet";
        } else {
            jobSheet = new JobSheet();
            Account account = accountService.findById(userInfo.getId());
            MySnapShot mySnapShot = new MySnapShot();
            model.addAttribute("jobSheet", jobSheet);
            model.addAttribute("snapShots", jobSheet.getJobSheetSnapShots());
            model.addAttribute("snapShot", mySnapShot);
            model.addAttribute("jobSheetFileExtension", String.join("||", configService.findFileExtension("JOB_SHEET_FILE_EXT", true, userInfo.getProjectId())));
            model.addAttribute("user", account);
            model.addAttribute("accountGrantor", accountService.getAccountGrantor());
            model.addAttribute("accountReferences", accountService.getAccountReferences());
            model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
            model.addAttribute("maxReportDate", jobSheetService.findJobSheetAndReportDate());
            model.addAttribute("alertmsg", "no_prev_job_sheet");
            model.addAttribute("ptype", "write");

            //getJobSheetTemplateList(model);
            getProjectName(model);
            getProjectWeather(model);
            return "project/jobSheetAdd/page";
        }
    }

    @GetMapping({"/printJobSheetProcessItem"})
    public String printJobSheetProcessItem(long id, Model model) {
        getPrintJobSheetProcessItem(id, model);
        return "project/modal/printJobSheetProcessItem";
    }

    @GetMapping({"/projectPrint"})
    public String projectPrint(long id, Model model) {
        /**
         JobSheet jobSheet = this.getJobSheet(id, model);
         this.getProjectName(model);
         this.setAlertIsRead(jobSheet);
         model.addAttribute("jobSheetProcessItems", this.jobSheetService.findJobSheetProcessItemByJobSheetId(id));
         **/
        return "project/print/projectPrint";
    }

    @GetMapping({"/jobSheetPrint"})
    public String jobSheetPrint(long id, String preview, Model model) {
        getPrintJobSheet(id, preview, model);
        return "project/print/jobSheetPrint";
    }

    @GetMapping({"/jobSheetProcessItemPrint"})
    public String jobSheetProcessItemPrint(long id, Model model) {
        getPrintJobSheetProcessItem(id, model);
        return "project/print/jobSheetProcessItemPrint";
    }

    private void getPrintJobSheetProcessItem(long id, Model model) {
        ViewJobSheetProcessItem jobSheetProcessItem = this.jobSheetService.findViewJobSheetProcessItemByProcessItemId(id);
        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = this.processCostService.findProcessItemCostDetail(jobSheetProcessItem.getProcessItem().getId(), "TASK");
        BigDecimal sum = BigDecimal.ZERO;
        boolean haveFirstCount = false;
        BigDecimal firstCountValue = BigDecimal.ZERO;
        boolean isZero = false;
        String totalText = "";
        for (ProcessItemCostDetailDTO processItemCostDetailDTO : processItemCostDetailDTOs) {
            if (processItemCostDetailDTO.isFirst()) {
                haveFirstCount = true;
                firstCountValue = processItemCostDetailDTO.getCount();
            }
            if (processItemCostDetailDTO.getCount().compareTo(BigDecimal.ZERO) == 0) {
                isZero = true;
            }
            if (processItemCostDetailDTO.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {
                isZero = true;
            }
            sum = sum.add(processItemCostDetailDTO.getCost());
        }
        if (haveFirstCount) {
            if (isZero) {
                totalText = "값 또는 단가가 0인 항목이 있습니다.";
            } else {
                BigDecimal numberResult = sum.setScale(3, BigDecimal.ROUND_UP).divide(firstCountValue, 3, BigDecimal.ROUND_UP);
                totalText = Utils.num2String(numberResult.intValue()) + " = " + Utils.num2String(sum.intValue()) + " / " + firstCountValue.setScale(3, BigDecimal.ROUND_UP);
            }
        } else {
            totalText = "대표가 없습니다.";
        }
        model.addAttribute("jobSheetProcessItem", jobSheetProcessItem);
        model.addAttribute("jobSheetProcessItemWorkers", this.jobSheetService.findJobSheetProcessItemWorkerByJobSheetProcessItemId(id));
        model.addAttribute("jobSheetProcessItemDevices", this.jobSheetService.findJobSheetProcessItemDeviceByJobSheetProcessItemId(id));
        model.addAttribute("processItemCostDetail", processItemCostDetailDTOs);
        model.addAttribute("totalText", totalText);
    }

    private void getPrintJobSheet(long id, String preview, Model model) {
        Project project = projectService.findById();
        long currentProcessInfoId = processService.getCurrentVersionProcessInfo().getId();
        JobSheet jobSheet = this.getJobSheet(id, model);
        this.getProjectName(model);
        this.setAlertIsRead(jobSheet);

        long jobSheetId = 0;
        if (preview != null && preview.equals("Y")) {
            jobSheetId = jobSheet.getId();
        }
        // 총계
        List<WorkAmountDTO> workAmounts = this.workAmountService.findAllWorkAmountListForWork(project.getId(), configService.defaultSelectedLanguageConfigs(), jobSheet.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), jobSheetId);
        WorkAmountDTO workAmountTotal = this.workAmountService.findSumWorkAmountListForYear(project.getId(), jobSheet.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), jobSheetId, 0);
        
        // 계획
        List<Long> workPlanAmounts = getWorkPlanAmount(workAmounts, project.getId(), jobSheet.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        long workPlanAmountSum = 0; //workPlanAmounts.stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < workPlanAmounts.size(); i++) {
            workPlanAmountSum = workPlanAmountSum + workPlanAmounts.get(i);
        }

        // 당해년도 대비
        List<BigDecimal> workAmountContrast = getWorkAmountContrast(workAmounts, workPlanAmounts, "today");
        BigDecimal workAmountContrastSum = new BigDecimal(BigInteger.ZERO);
        BigDecimal todayAmount = new BigDecimal(workAmountTotal.getTodayAmount());
        BigDecimal totalAmount = new BigDecimal(workAmountTotal.getTotalAmount());
        if (workPlanAmountSum > 0) {
            workAmountContrastSum = todayAmount.divide(BigDecimal.valueOf(workPlanAmountSum), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workAmountContrastSum.setScale(2, RoundingMode.HALF_UP);

        // 전체누계 대비
        List<BigDecimal> workTotalContrast = getWorkAmountContrast(workAmounts, workPlanAmounts, "total");
        BigDecimal workTotalContrastSum = new BigDecimal(BigInteger.ZERO);
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            workTotalContrastSum = todayAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        workTotalContrastSum.setScale(2, RoundingMode.HALF_UP);

        // 세부내역
        List<WorkAmountDTO> jobSheetProcessItems = getJobSheetProcessItemList(workAmounts, id, jobSheet.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        for (WorkAmountDTO workAmountDTO : jobSheetProcessItems) {
            //System.out.println(workAmountDTO.getWorkNameLocale() + "---" + workAmountDTO.getWorkId());
            if (workAmountDTO.getJobSheetProcessItemDTOs().size() > 0) {
                //System.out.println(workAmountDTO.getJobSheetProcessItemDTOs().get(0).getCost() + " : " + workAmountDTO.getJobSheetProcessItemDTOs().get(0).getTodayProgressAmount());
            }
        }

        // 인원현황 총계
        List<JobSheetProcessItemWorkerDTO> jobSheetProcessItemWorkers = this.jobSheetService.findAllProjectListForWorker(id, jobSheet.getReportDate());
        BigDecimal workerAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal workerBeforeAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal workerTotalAmount = new BigDecimal(BigInteger.ZERO);
        for (JobSheetProcessItemWorkerDTO jobSheetProcessItemWorkerDTO : jobSheetProcessItemWorkers) {
            workerAmount = workerAmount.add(jobSheetProcessItemWorkerDTO.getAmount());
            if (jobSheetProcessItemWorkerDTO.getBeforeAmount() != null) {
                workerBeforeAmount = workerBeforeAmount.add(jobSheetProcessItemWorkerDTO.getBeforeAmount());
            }
        }
        workerTotalAmount = workerAmount.add(workerBeforeAmount).setScale(2, RoundingMode.HALF_UP);

        // 장비현황 총계
        List<JobSheetProcessItemDeviceDTO> jobSheetProcessItemDevices = this.jobSheetService.findAllProjectListForDevice(id, jobSheet.getReportDate());
        BigDecimal deviceAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal deviceBeforeAmount = new BigDecimal(BigInteger.ZERO);
        BigDecimal deviceTotalAmount = new BigDecimal(BigInteger.ZERO);
        for (JobSheetProcessItemDeviceDTO jobSheetProcessItemDeviceDTO : jobSheetProcessItemDevices) {
            deviceAmount = deviceAmount.add(jobSheetProcessItemDeviceDTO.getAmount());
            deviceBeforeAmount = deviceBeforeAmount.add(jobSheetProcessItemDeviceDTO.getBeforeAmount());
        }
        deviceTotalAmount = deviceAmount.add(deviceBeforeAmount).setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("workAmountTotal", workAmountTotal);
        model.addAttribute("workAmounts", workAmounts);
        model.addAttribute("workPlanAmounts", workPlanAmounts);
        model.addAttribute("workPlanAmountSum", workPlanAmountSum);
        model.addAttribute("workAmountContrast", workAmountContrast);
        model.addAttribute("workAmountContrastSum", workAmountContrastSum);
        model.addAttribute("workTotalContrast", workTotalContrast);
        model.addAttribute("workTotalContrastSum", workTotalContrastSum);
        model.addAttribute("processItemCosts", this.processCostService.getProcessItemSumCost(currentProcessInfoId));
        model.addAttribute("jobSheetProcessItems", jobSheetProcessItems);
        model.addAttribute("jobSheetProcessItemWorkers", jobSheetProcessItemWorkers);
        model.addAttribute("jobSheetProcessItemWorkerAmount", workerAmount);
        model.addAttribute("jobSheetProcessItemWorkerBeforeAmount", workerBeforeAmount);
        model.addAttribute("jobSheetProcessItemWorkerTotalAmount", workerTotalAmount);
        model.addAttribute("jobSheetProcessItemDevices", jobSheetProcessItemDevices);
        model.addAttribute("jobSheetProcessItemDeviceAmount", deviceAmount);
        model.addAttribute("jobSheetProcessItemDeviceBeforeAmount", deviceBeforeAmount);
        model.addAttribute("jobSheetProcessItemDeviceTotalAmount", deviceTotalAmount);
        model.addAttribute("preview", preview);
    }

    public List<Long> getWorkPlanAmount(List<WorkAmountDTO> workAmounts, long projectId, String reportDate) {
        List<Long> workPlanAmounts = new ArrayList<>();
        for (WorkAmountDTO workAmount : workAmounts) {
            List<WorkPlanDTO> workPlans = workPlanService.findAllWorkPlanListForWork(projectId, workAmount.getWorkId(), reportDate);
            BigDecimal totalAmount = new BigDecimal(workAmount.getAmount());
            BigDecimal planAmount = new BigDecimal(BigInteger.ZERO);
            for (WorkPlanDTO workPlan : workPlans) {
                BigDecimal amount = new BigDecimal(BigInteger.ZERO);
                if (workPlan.getMonth().equals(reportDate.substring(5,7))) {
                    amount = totalAmount.multiply(BigDecimal.valueOf(workPlan.getDayRate())).multiply(new BigDecimal(reportDate.substring(8,10))).divide(new BigDecimal("100"), RoundingMode.HALF_UP);
                    planAmount = planAmount.add(amount);
                } else {
                    amount = totalAmount.multiply(workPlan.getMonthRate()).divide(new BigDecimal("100"), RoundingMode.HALF_UP);
                    planAmount = planAmount.add(amount);
                }
                //System.out.println(totalAmount + "------" + workPlan.getDayRate() + "------" + workPlan.getMonthRate() + "------" + planAmount);
            }
            workPlanAmounts.add(planAmount.setScale(0, RoundingMode.HALF_UP).longValue());
        }
        return workPlanAmounts;
    }

    public List<Long> getWorkPlanAmountSum(List<WorkAmountDTO> workAmounts, long projectId, String reportDate) {
        List<Long> workPlanAmounts = new ArrayList<>();
        for (WorkAmountDTO workAmount : workAmounts) {
            List<WorkPlanDTO> workPlans = workPlanService.findAllWorkPlanListForWorkSum(projectId, workAmount.getWorkId());
            BigDecimal totalAmount = new BigDecimal(workAmount.getAmount());    // 당해년도
            BigDecimal planAmount = new BigDecimal(BigInteger.ZERO);
            for (WorkPlanDTO workPlan : workPlans) {
                BigDecimal amount = new BigDecimal(BigInteger.ZERO);
                if (workPlan.getMonth().equals(reportDate.substring(5,7))) {
                    amount = totalAmount.multiply(BigDecimal.valueOf(workPlan.getDayRate())).multiply(new BigDecimal(reportDate.substring(8,10))).divide(new BigDecimal("100"), RoundingMode.HALF_UP);
                    planAmount = planAmount.add(amount);
                } else {
                    amount = totalAmount.multiply(workPlan.getMonthRate()).divide(new BigDecimal("100"), RoundingMode.HALF_UP);
                    planAmount = planAmount.add(amount);
                }
                //System.out.println(totalAmount + "------" + workPlan.getDayRate() + "------" + workPlan.getMonthRate() + "------" + planAmount);
                //System.out.println(workAmount.getWorkId() + "----" + workPlan.getYear() + "----" + workPlan.getMonth() + "----" + totalAmount + "------" + workPlan.getMonthRate() + "------" + planAmount);
            }
            workPlanAmounts.add(planAmount.setScale(0, RoundingMode.HALF_UP).longValue());
        }
        return workPlanAmounts;
    }

    public List<BigDecimal> getWorkAmountContrast(List<WorkAmountDTO> workAmounts, List<Long> workPlanAmounts, String gubun) {
        List<BigDecimal> workPlanAmountContrasts = new ArrayList<>();
        int i = 0;
        for (WorkAmountDTO workAmount : workAmounts) {
            BigDecimal todayAmount = new BigDecimal(workAmount.getTodayAmount());
            BigDecimal totalAmount = new BigDecimal(workAmount.getTotalAmount());
            BigDecimal planAmountContrast = new BigDecimal(BigInteger.ZERO);
            if (gubun.equals("today")) {
                if (workPlanAmounts.get(i) > 0) {
                    planAmountContrast = todayAmount.divide(BigDecimal.valueOf(workPlanAmounts.get(i)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }
            } else if (gubun.equals("total")) {
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    planAmountContrast = todayAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }
            }
            workPlanAmountContrasts.add(planAmountContrast.setScale(2, RoundingMode.HALF_UP));
            i = i + 1;
        }
        return workPlanAmountContrasts;
    }

    public List<BigDecimal> getWorkAmountPaidCostContrast(List<WorkAmountDTO> workAmounts, List<Long> workPlanAmounts, String gubun) {
        List<BigDecimal> workPlanAmountContrasts = new ArrayList<>();
        int i = 0;
        for (WorkAmountDTO workAmount : workAmounts) {
            BigDecimal todayAmount = new BigDecimal(workAmount.getPaidCost());
            BigDecimal totalAmount = new BigDecimal(workAmount.getTotalAmount());
            BigDecimal planAmountContrast = new BigDecimal(BigInteger.ZERO);
            if (gubun.equals("today")) {
                if (workPlanAmounts.get(i) > 0) {
                    planAmountContrast = todayAmount.divide(BigDecimal.valueOf(workPlanAmounts.get(i)), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }
            } else if (gubun.equals("total")) {
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    planAmountContrast = todayAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }
            }
            workPlanAmountContrasts.add(planAmountContrast.setScale(2, RoundingMode.HALF_UP));
            i = i + 1;
        }
        return workPlanAmountContrasts;
    }

    private List<WorkAmountDTO> getJobSheetProcessItemList(List<WorkAmountDTO> workAmounts, long jobSheetId, String reportDate) {
        //List<Map.Entry<Object, Object>> jobSheetProcessItemList = new ArrayList<Map.Entry<Object, Object>>();
        List<WorkAmountDTO> saveWorkAmountList = new ArrayList<>();
        for (WorkAmountDTO workAmount : workAmounts) {
            List<JobSheetProcessItemDTO> jobSheetProcessItemLists = this.workAmountService.findSumJobSheetProcessItemListForYear(workAmount.getWorkId(), jobSheetId, reportDate);
            BigDecimal totalCost = new BigDecimal(BigInteger.ZERO);
            long totalBeforeProgressAmount = 0;
            long totalTodayProgressAmount = 0;
            long totalAfterProgressAmount = 0;
            BigDecimal totalBeforeProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal totalTodayProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal totalAfterProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal totalYearProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal itemCost = new BigDecimal(BigInteger.ZERO);
            long itemBeforeProgressAmount = 0;
            long itemTodayProgressAmount = 0;
            long itemAfterProgressAmount = 0;
            BigDecimal itemBeforeProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal itemTodayProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal itemAfterProgressRate = new BigDecimal(BigInteger.ZERO);
            BigDecimal itemYearProgressRate = new BigDecimal(BigInteger.ZERO);
            String taskName = "";
            String taskFullPath = "";
            String parentTaskFullPath = "";
            long oldParentId = 0;

            long parentId = 0;
            List<JobSheetProcessItemDTO> savedJobSheetProcessItemTotals = new ArrayList<>();
            List<JobSheetProcessItemDTO> savedJobSheetProcessItemLists = new ArrayList<>();
            if (jobSheetProcessItemLists.size() > 0) {
                for (JobSheetProcessItemDTO jobSheetProcessItem : jobSheetProcessItemLists) {
                    oldParentId = jobSheetProcessItem.getParentId();
                    totalCost = totalCost.add(jobSheetProcessItem.getCost());
                    totalBeforeProgressAmount = totalBeforeProgressAmount + jobSheetProcessItem.getBeforeProgressAmount();
                    totalTodayProgressAmount = totalTodayProgressAmount + jobSheetProcessItem.getTodayProgressAmount();
                    totalAfterProgressAmount = totalAfterProgressAmount + jobSheetProcessItem.getAfterProgressAmount();
                    totalBeforeProgressRate = totalBeforeProgressRate.add(jobSheetProcessItem.getBeforeProgressRate());
                    totalTodayProgressRate = totalTodayProgressRate.add(jobSheetProcessItem.getTodayProgressRate());
                    totalAfterProgressRate = totalAfterProgressRate.add(jobSheetProcessItem.getAfterProgressRate());
                    totalYearProgressRate = totalYearProgressRate.add(jobSheetProcessItem.getYearProgressRate());

                    if (parentId > 0 && parentId != jobSheetProcessItem.getParentId()) {
                        JobSheetProcessItemDTO savedJobSheetProcessItemTotal = new JobSheetProcessItemDTO(taskFullPath
                                , taskFullPath
                                , itemCost
                                , itemBeforeProgressAmount
                                , itemBeforeProgressRate
                                , itemTodayProgressAmount
                                , itemTodayProgressRate
                                , itemAfterProgressAmount
                                , itemAfterProgressRate
                                , parentId
                                , parentTaskFullPath
                                , itemYearProgressRate);
                        savedJobSheetProcessItemTotals.add(savedJobSheetProcessItemTotal);
                        //System.out.println("savedJobSheetProcessItemTotals----1\n" + " -----" + parentId + savedJobSheetProcessItemTotals.size());
                        itemCost = BigDecimal.ZERO;
                        itemBeforeProgressAmount = 0;
                        itemTodayProgressAmount = 0;
                        itemAfterProgressAmount = 0;
                        itemBeforeProgressRate = BigDecimal.ZERO;
                        itemTodayProgressRate = BigDecimal.ZERO;
                        itemAfterProgressRate = BigDecimal.ZERO;
                        itemYearProgressRate = BigDecimal.ZERO;
                    }
                    parentId = jobSheetProcessItem.getParentId();
                    taskName = jobSheetProcessItem.getTaskName();
                    taskFullPath = jobSheetProcessItem.getTaskFullPath();
                    parentTaskFullPath = jobSheetProcessItem.getParentTaskFullPath();

                    itemCost = itemCost.add(jobSheetProcessItem.getCost());
                    itemBeforeProgressAmount = itemBeforeProgressAmount + jobSheetProcessItem.getBeforeProgressAmount();
                    itemTodayProgressAmount = itemTodayProgressAmount + jobSheetProcessItem.getTodayProgressAmount();
                    itemAfterProgressAmount = itemAfterProgressAmount + jobSheetProcessItem.getAfterProgressAmount();
                    itemBeforeProgressRate = itemBeforeProgressRate.add(jobSheetProcessItem.getBeforeProgressRate());
                    itemTodayProgressRate = itemTodayProgressRate.add(jobSheetProcessItem.getTodayProgressRate());
                    itemAfterProgressRate = itemAfterProgressRate.add(jobSheetProcessItem.getAfterProgressRate());
                    itemYearProgressRate = itemYearProgressRate.add(jobSheetProcessItem.getYearProgressRate());
                }
                JobSheetProcessItemDTO savedJobSheetProcessItemTotal = new JobSheetProcessItemDTO(taskFullPath
                        , taskFullPath
                        , itemCost
                        , itemBeforeProgressAmount
                        , itemBeforeProgressRate
                        , itemTodayProgressAmount
                        , itemTodayProgressRate
                        , itemAfterProgressAmount
                        , itemAfterProgressRate
                        , oldParentId
                        , parentTaskFullPath
                        , itemYearProgressRate);
                savedJobSheetProcessItemTotals.add(savedJobSheetProcessItemTotal);
                //System.out.println("savedJobSheetProcessItemTotals---2\n" + " -----" + oldParentId  + savedJobSheetProcessItemTotals.size());

                parentId = 0;
                Integer i = 0;
                for (JobSheetProcessItemDTO jobSheetProcessItem : jobSheetProcessItemLists) {
                    if (parentId == 0) {
                        JobSheetProcessItemDTO savedJobSheetProcessItem = new JobSheetProcessItemDTO(savedJobSheetProcessItemTotals.get(i).getTaskName()
                                , savedJobSheetProcessItemTotals.get(i).getParentTaskFullPath()
                                , savedJobSheetProcessItemTotals.get(i).getCost()
                                , savedJobSheetProcessItemTotals.get(i).getBeforeProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getBeforeProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getTodayProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getTodayProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getAfterProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getAfterProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getParentId()
                                , savedJobSheetProcessItemTotals.get(i).getParentTaskFullPath()
                                , savedJobSheetProcessItemTotals.get(i).getYearProgressRate());
                        savedJobSheetProcessItemLists.add(savedJobSheetProcessItem);
                        i = i + 1;
                    } else if (parentId > 0 && parentId != jobSheetProcessItem.getParentId()) {
                        JobSheetProcessItemDTO savedJobSheetProcessItem = new JobSheetProcessItemDTO(savedJobSheetProcessItemTotals.get(i).getTaskName()
                                , savedJobSheetProcessItemTotals.get(i).getParentTaskFullPath()
                                , savedJobSheetProcessItemTotals.get(i).getCost()
                                , savedJobSheetProcessItemTotals.get(i).getBeforeProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getBeforeProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getTodayProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getTodayProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getAfterProgressAmount()
                                , savedJobSheetProcessItemTotals.get(i).getAfterProgressRate()
                                , savedJobSheetProcessItemTotals.get(i).getParentId()
                                , savedJobSheetProcessItemTotals.get(i).getParentTaskFullPath()
                                , savedJobSheetProcessItemTotals.get(i).getYearProgressRate());
                        savedJobSheetProcessItemLists.add(savedJobSheetProcessItem);
                        i = i + 1;
                    }

                    JobSheetProcessItemDTO savedJobSheetProcessItem = new JobSheetProcessItemDTO(jobSheetProcessItem.getTaskName()
                            , jobSheetProcessItem.getParentTaskFullPath()
                            , jobSheetProcessItem.getCost()
                            , jobSheetProcessItem.getBeforeProgressAmount()
                            , jobSheetProcessItem.getBeforeProgressRate()
                            , jobSheetProcessItem.getTodayProgressAmount()
                            , jobSheetProcessItem.getTodayProgressRate()
                            , jobSheetProcessItem.getAfterProgressAmount()
                            , jobSheetProcessItem.getAfterProgressRate()
                            , jobSheetProcessItem.getParentId()
                            , jobSheetProcessItem.getParentTaskFullPath()
                            , jobSheetProcessItem.getYearProgressRate());
                    savedJobSheetProcessItemLists.add(savedJobSheetProcessItem);
                    parentId = jobSheetProcessItem.getParentId();

                }
            }
            totalCost = totalCost.setScale(0, RoundingMode.HALF_UP);
            totalBeforeProgressRate = totalBeforeProgressRate.setScale(2, RoundingMode.HALF_UP);
            totalTodayProgressRate = totalTodayProgressRate.setScale(2, RoundingMode.HALF_UP);
            totalAfterProgressRate = totalAfterProgressRate.setScale(2, RoundingMode.HALF_UP);
            totalYearProgressRate = totalYearProgressRate.setScale(2, RoundingMode.HALF_UP);
            WorkAmountDTO saveSorkAmount = new WorkAmountDTO(workAmount.getWorkId(), workAmount.getWorkNameLocale(), savedJobSheetProcessItemLists, totalCost, totalBeforeProgressAmount, totalBeforeProgressRate, totalTodayProgressAmount, totalTodayProgressRate, totalAfterProgressAmount, totalAfterProgressRate, totalYearProgressRate);
            saveWorkAmountList.add(saveSorkAmount);
        }
        /**
        List<JobSheetProcessItem> jobSheetProcessItems = this.jobSheetService.findAllJobSheetProcessItem();
        for (JobSheetProcessItem jobSheetProcessItem : jobSheetProcessItems) {
            jobSheetProcessItemList.add(new AbstractMap.SimpleEntry<>(jobSheetProcessItem.getId(), jobSheetProcessItem.getProcessItem().getName()));
        }
         **/
        return saveWorkAmountList;
    }

    @GetMapping("/jobSheetProcessItemTable")
    public String jobSheetProcessItemTable(int itemBun, long id, Model model) {
        ViewJobSheetProcessItem jobSheetProcessItem = this.jobSheetService.findViewJobSheetProcessItemByProcessItemId(id);
        ProcessItem processItem = this.jobSheetService.getProcessItem(jobSheetProcessItem.getProcessItem().getId());

        model.addAttribute("jobSheetProcessItem", jobSheetProcessItem);
        model.addAttribute("processItem", processItem);
        model.addAttribute("itemBun", itemBun);
        return "project/modal/jobSheetProcessItemTable";
    }

    @GetMapping("/selectJobSheetList")
    public String selectJobSheetList(String phasingCodes, Model model) {
        List<JobSheetDTO> jobSheetDTOS = new ArrayList<>();
        if(phasingCodes.length()>0) {
            phasingCodes = phasingCodes.replaceAll(" ", "");
            long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();
            jobSheetDTOS = jobSheetService.findJobSheetListIdsByPhasingCodes(processId, phasingCodes.split(","));
            model.addAttribute("jobSheetList", jobSheetDTOS);
        } else {
            model.addAttribute("jobSheetList", jobSheetDTOS);
        }
        return "project/modal/selectJobSheetList";
    }

    @GetMapping("/projectInfo")
    public String projectInfo(Model model) {
        Project project = projectService.findMyProject();

        model.addAttribute("project", project);
        model.addAttribute("projectImages", project.getProjectImages());

        return "project/projectInfo/page";
    }

    /**
     @GetMapping("/uploadExcelFile")
     public String uploadExcelFile() {
     fileUploadService.ExcelToPdfPrinter();
     return "project/print/uploadExcelFile";
     }
     **/
}
