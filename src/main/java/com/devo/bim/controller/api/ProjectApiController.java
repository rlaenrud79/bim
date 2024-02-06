package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.model.vo.JobSheetGrantorVO;
import com.devo.bim.model.vo.JobSheetVO;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/projectApi")
@RequiredArgsConstructor
public class ProjectApiController extends AbstractController {

    private final JobSheetService jobSheetService;
    private final JobSheetGrantorService jobSheetGrantorService;
    private final JobSheetFileService jobSheetFileService;
    private final JobSheetSnapShotService jobSheetSnapShotService;
    private final AccountService accountService;

    @PostMapping("/postJobSheet")
    public JsonObject postJobSheet(@RequestBody JobSheetVO jobSheetVO) {
        return jobSheetService.postJobSheet(jobSheetVO);
    }

    @PutMapping("/deleteJobSheet")
    public JsonObject deleteJobSheet(long id) {
        return jobSheetService.deleteJobSheet(id);
    }

    @DeleteMapping("/deleteSelJobSheet")
    public JsonObject deleteSelJobSheet(@RequestBody List<JobSheetVO> jobSheetVO) {
        return jobSheetService.deleteSelJobSheet(jobSheetVO);
    }

    @PostMapping("/denyJobSheet")
    public JsonObject denyJobSheet(@RequestBody JobSheetGrantorVO jobSheetGrantorVO) {
        return jobSheetService.denyJobSheet(jobSheetGrantorVO);
    }

    @PostMapping("/approveJobSheet")
    public JsonObject approveJobSheet(@RequestBody JobSheetGrantorVO jobSheetGrantorVO) {
        return jobSheetService.approveJobSheet(jobSheetGrantorVO);
    }

    @PutMapping("/deleteJobSheetReply")
    public JsonObject deleteJobSheetReply(@RequestBody JobSheetGrantorVO jobSheetGrantorVO) {
        return jobSheetGrantorService.deleteJobSheetReply(jobSheetGrantorVO);
    }

    @DeleteMapping("/deleteJobSheetFile")
    public JsonObject deleteJobSheetFile(long id) {
        return jobSheetFileService.deleteJobSheetFile(id);
    }

    @DeleteMapping("/deleteJobSheetSnapShot")
    public JsonObject deleteJobSheetSnapShot(long id) {
        return jobSheetSnapShotService.deleteJobSheetSnapShot(id);
    }

    @PutMapping("/putJobSheet")
    public JsonObject putJobSheet(@RequestBody JobSheetVO jobSheetVO) {
        return jobSheetService.putJobSheet(jobSheetVO);
    }

    @PutMapping("/putJobSheetWritingStatus")
    public JsonObject putJobSheetStatus(long id) {
        return jobSheetService.putJobSheetWritingStatus(id);
    }

    @PostMapping("/copyJobSheet")
    public JsonObject copyJobSheet(@RequestBody JobSheetVO jobSheetVO) {
        return jobSheetService.copyJobSheet(jobSheetVO);
    }

    @PostMapping("/reAddJobSheet")
    public JsonObject reAddJobSheet(@RequestBody JobSheetVO jobSheetVO) {
        return jobSheetService.reAddJobSheet(jobSheetVO);
    }

    @PostMapping("/renderJobSheetSnapShotAtAdd")
    public JsonObject renderJobSheetSnapShotAtAdd(@RequestBody MySnapShotShareVO mySnapShotShareVO) {
        return accountService.findMySnapShotsJobSheetProcessItemJson(mySnapShotShareVO);
    }

    /**
     @PostMapping("/getJobSheetPrev")
     public JsonObject getJobSheetPrev(@RequestBody List<JsonObject> processItemJsonObject) {
     return jobSheetService.getJobSheetPrev(processItemJsonObject);
     }
     **/
}
