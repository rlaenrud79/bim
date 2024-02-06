package com.devo.bim.controller.modal;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ViewJobSheetProcessItem;
import com.devo.bim.model.vo.SearchProcessItemCategoryVO;
import com.devo.bim.model.vo.SearchScheduleVO;
import com.devo.bim.repository.spring.ProcessInfoRepository;
import com.devo.bim.service.AlertService;
import com.devo.bim.service.JobSheetService;
import com.devo.bim.service.MainService;
import com.devo.bim.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/mainModal")
@RequiredArgsConstructor
public class MainModalController extends AbstractController {

    private final MainService mainService;
    private final AlertService alertService;
    private final ProcessService processService;
    private final JobSheetService jobSheetService;
    private final ProcessInfoRepository processInfoRepository;

    @GetMapping("/notificationView")
    public String notificationView(
            @RequestParam(required = true, defaultValue = "0") long alertId
            , @RequestParam(required = true, defaultValue = "0") int totalCnt
            , @RequestParam(required = true, defaultValue = "0") int no
            , Model model){

        model.addAttribute("userPopUpNoticeDTO", mainService.getNoticeAlertById(alertId));
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("no", no);
        return "main/modal/notificationView";
    }

    @GetMapping("/mainDateProcessList/{date}")
    public String mainDateProcessList(@PathVariable String date, Model model) {

        long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();
        model.addAttribute("processItemList", processService.getProcessItemDateList(processId, date));
        return "main/modal/calendarList";
    }

    @GetMapping("/mainScheduleList")
    public String mainScheduleList(@RequestParam(required = true, defaultValue = "") String tab
            , @RequestParam(required = true, defaultValue = "") String date, Model model) {
        long currentProcessInfoId = processService.getCurrentVersionProcessInfo().getId();

        if (tab != null && date != null) {
            if (tab.equals("J")) {
                List<ViewJobSheetProcessItem> jobSheetProcessItemList = jobSheetService.getJobSheetProcessItemReportDateListLimit(date);
                model.addAttribute("processItemList", jobSheetProcessItemList);
                model.addAttribute("calendarHtml", jobSheetService.generateCalendarHtml(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))));
            } else {
                List<ProcessItem> processItemList = processService.getProcessItemDateListLimit(currentProcessInfoId, date);
                model.addAttribute("processItemList", processItemList);
                model.addAttribute("calendarHtml", processService.generateCalendarHtml(currentProcessInfoId, Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))));
            }
        }
        model.addAttribute("tab", tab);
        model.addAttribute("todayString", Utils.convertDate(date));
        model.addAttribute("todayWeekString", Utils.convertDateWithDayOfWeek(date));
        model.addAttribute("curDate", date);
        model.addAttribute("prevDate", Utils.getPreviousDate(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("nextDate", Utils.getNextDate(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "main/schedule";
    }
}
