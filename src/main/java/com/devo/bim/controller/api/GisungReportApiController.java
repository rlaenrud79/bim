package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.vo.GisungReportVO;
import com.devo.bim.service.GisungReportService;
import com.devo.bim.service.JobSheetService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/gisungReportApi")
@RequiredArgsConstructor
public class GisungReportApiController extends AbstractController {
    private final GisungReportService gisungReportService;
    private final JobSheetService jobSheetService;

    @PostMapping("/postGisungReport")
    public JsonObject postGisungReport(@RequestBody GisungReportVO gisungReportVO) {
        return gisungReportService.postGisungReport(gisungReportVO);
    }

    @DeleteMapping("/deleteGisungReport")
    public JsonObject deleteGisungReport(long id) {
        return gisungReportService.deleteGisungReport(id);
    }

    @GetMapping("/getJobSheetGisungReport")
    public JsonObject jobSheetGisungReport(String startDate, String endDate) {
        return jobSheetService.jobSheetGisungReport(startDate, endDate);
    }

    @PutMapping("/putGisungReport")
    public JsonObject putGisungReport(@RequestBody GisungReportVO gisungReportVO) {
        return gisungReportService.putGisungReport(gisungReportVO);
    }
}
