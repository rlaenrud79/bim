package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.IssueDTO;
import com.devo.bim.model.entity.Issue;
import com.devo.bim.model.entity.IssueManager;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.vo.*;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/coWorkApi")
@RequiredArgsConstructor
public class CoWorkApiController extends AbstractController {

    private final IssueService issueService;
    private final CoWorkService coWorkService;
    private final IssueReportService issueReportService;
    private final ExcelFormatterService excelFormatterService;
    private final NoticeService noticeService;
    private final BulletinService bulletinService;
    private final BulletinReplyService bulletinReplyService;
    private final BulletinLikesService bulletinLikesService;
    private final BulletinFileService bulletinFileService;
    private final ScheduleService scheduleService;
    private final AccountService accountService;

    @PostMapping("/postIssue")
    public JsonObject postIssue(@RequestBody IssueVO issueVO){
        initIssueVO(issueVO);
        Issue issue = setNewIssue(issueVO);
        List<IssueManager> issueManagers = setNewIssueManagers(issueVO);
        return issueService.postIssue(issue, issueManagers);
    }

    @PutMapping("/putIssue")
    public JsonObject putIssue(@RequestBody IssueVO issueVO){
        initIssueVO(issueVO);
        Issue issue = setNewIssue(issueVO);
        List<IssueManager> issueManagers = setNewIssueManagers(issueVO);
        return issueService.putIssue(issue, issueManagers);
    }

    private void initIssueVO(@RequestBody IssueVO issueVO) {
        issueVO.setProjectId(userInfo.getProjectId());
        issueVO.setUserId(userInfo.getId());
    }

    @PutMapping("/putCoWork")
    public JsonObject putCoWork(@RequestBody CoWorkVO coWorkVO)
    {
        return coWorkService.putCoWork(coWorkVO);
    }

    @PutMapping("/completeCoWork/{coWorkId}")
    public JsonObject completeCoWork(@PathVariable long coWorkId)
    {
        return coWorkService.completeCoWork(coWorkId);
    }

    @NotNull
    private Issue setNewIssue(@RequestBody IssueVO issueVO) {
        return new Issue(issueVO);
    }

    @NotNull
    private List<IssueManager> setNewIssueManagers(@RequestBody IssueVO issueVO) {
        List<IssueManager> issueManagers = new ArrayList<>();
        for (Long issueManagerId : issueVO.getIssueManagerIds()) {
            if(issueVO.getIssueId() == 0) issueManagers.add(new IssueManager(issueManagerId));
            else issueManagers.add(new IssueManager(issueVO.getIssueId(), issueManagerId));
        }
        return issueManagers;
    }

    @DeleteMapping("/deleteIssue/{issueId}")
    public JsonObject deleteIssue(@PathVariable long issueId) {
        return issueService.deleteIssue(issueId);
    }

    @GetMapping("/issueListExcel")
    public void issueListExcel(@ModelAttribute SearchIssueVO searchIssueVO
            , HttpServletResponse response) throws Exception {

        String fileName = "IssueList";
        String sheetName = "Issue List";
        searchIssueVO.setProjectId(userInfo.getProjectId());
        List<IssueDTO> issueDTOs = issueService.findIssueDTOsBySearchCondition(searchIssueVO);

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.ISSUE_LIST, fileName, sheetName, issueDTOs, response);
    }

    @PostMapping("/postIssueReport")
    public JsonObject postIssueReport(@RequestBody IssueReportVO issueReportVO){
        return issueReportService.postIssueReport(issueReportVO);
    }

    @PostMapping("/postCoWorkIssueReport")
    public JsonObject postCoWorkIssueReport(@RequestBody CoWorkIssueReportVO coWorkIssueReportVO){
        return coWorkService.postCoWorkIssueReport(coWorkIssueReportVO);
    }

    @PostMapping("/postNotification")
    public JsonObject postNotification(@RequestBody NoticeVO noticeVO){

        setDefaultValueNoticeVO(noticeVO);

        Notice notice = setNewNotice(noticeVO);
        List<Work> noticeWorks = setNewNoticeWorks(noticeVO);

        // Notice 공종 셋팅
        for (Work noticeWork : noticeWorks) {
            notice.setWork(noticeWork);
        }

        return noticeService.postNotice(notice);
    }

    @PostMapping("/putNotification")
    public JsonObject putNotification(@RequestBody NoticeVO noticeVO){

        setDefaultValueNoticeVO(noticeVO);

        Notice notice = setNewNotice(noticeVO);
        List<Work> noticeWorks = setNewNoticeWorks(noticeVO);

        // Notice 공종 셋팅
        for (Work noticeWork : noticeWorks) {
            notice.setWork(noticeWork);
        }

        return noticeService.putNotice(notice);
    }

    private void setDefaultValueNoticeVO(@RequestBody NoticeVO noticeVO) {
        noticeVO.setProjectId(userInfo.getProjectId());
        noticeVO.setUserId(userInfo.getId());
    }

    @DeleteMapping("/deleteNotification/{noticeId}")
    public JsonObject deleteNotification(@PathVariable long noticeId) {
        return noticeService.deleteNotice(noticeId);
    }

    @NotNull
    private Notice setNewNotice(@RequestBody NoticeVO noticeVO) {
        return new Notice(noticeVO);
    }

    @NotNull
    private List<Work> setNewNoticeWorks(@RequestBody NoticeVO noticeVO) {
        List<Work> noticeWorks = new ArrayList<>();
        for (Long noticeWorkId : noticeVO.getNoticeWorkIds()) {
            noticeWorks.add(new Work(noticeWorkId));
        }
        return noticeWorks;
    }

    @PostMapping("/postBulletin")
    public JsonObject postBulletin(@RequestBody BulletinVO bulletinVO) {
        return bulletinService.postBulletin(bulletinVO);
    }

    @PostMapping("/deleteBulletin")
    public JsonObject deleteBulletin(long id) {
        return bulletinService.deleteBulletin(id);
    }

    @DeleteMapping("/deleteSelBulletin")
    public JsonObject deleteSelBulletin(@RequestBody List<BulletinVO> bulletinVO) {
        return bulletinService.deleteSelBulletin(bulletinVO);
    }

    @PostMapping("/postBulletinReply")
    public JsonObject postBulletinReply(@RequestBody BulletinReplyVO bulletinReplyVO) {
        return bulletinReplyService.postBulletinReply(bulletinReplyVO);
    }

    @PostMapping("/deleteBulletinReply")
    public JsonObject deleteBulletinReply(long id) {
        return bulletinReplyService.deleteBulletinReply(id);
    }

    @PostMapping("/postBulletinLike")
    public JsonObject postBulletinLike(@RequestBody BulletinLikesVO bulletinLikesVO) {
        return bulletinLikesService.postBulletinLikes(bulletinLikesVO);
    }

    @PostMapping("/deleteBulletinFile")
    public JsonObject deleteBulletinFile(long id) {
        return bulletinFileService.deleteBulletinFile(id);
    }

    @PostMapping("/putBulletin")
    public JsonObject putBulletin(@RequestBody BulletinVO bulletinVO) {
        return bulletinService.putBulletin(bulletinVO);
    }

    @GetMapping("/getEvent/{yearMonth}")
    public JsonObject getEvent(@PathVariable String yearMonth){
        return scheduleService.getUserEventByYearMonth(yearMonth);
    }

    @PostMapping("/postSchedule")
    public JsonObject postSchedule(@RequestBody ScheduleVO scheduleVO){

        setUserAndProject(scheduleVO);
        return scheduleService.postScheduleAtUser(scheduleVO);
    }

    @PostMapping("/putSchedule")
    public JsonObject putSchedule(@RequestBody ScheduleVO scheduleVO){

        setUserAndProject(scheduleVO);
        return scheduleService.putScheduleAtUser(scheduleVO);
    }

    @DeleteMapping("/deleteSchedule/{scheduleId}")
    public JsonObject deleteSchedule(@PathVariable long scheduleId) {
        return scheduleService.deleteScheduleAtUser(scheduleId);
    }

    private void setUserAndProject(ScheduleVO scheduleVO) {
        scheduleVO.setUserId(userInfo.getId());
        scheduleVO.setProjectId(userInfo.getProjectId());

        for (Long userWorksId : accountService.getUserWorksIds(userInfo.getId())) {
            scheduleVO.getScheduleWorkIds().add(userWorksId);
        }
    }

    @GetMapping("/checkSession")
    public JsonObject checkSession() {
        return proc.getMessageResult(true, "pong");
    }
}
