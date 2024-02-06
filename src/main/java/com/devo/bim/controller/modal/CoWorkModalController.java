package com.devo.bim.controller.modal;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.DocumentCategoryDTO;
import com.devo.bim.model.dto.SearchUserDTO;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.ScheduleVO;
import com.devo.bim.model.vo.SearchDocumentCategoryVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/coWorkModal")
@RequiredArgsConstructor
public class CoWorkModalController extends AbstractController {
    private final SearchUserService searchUserService;
    private final CoWorkService coWorkService;
    private final CoWorkJoinerService coWorkJoinerService;
    private final CoWorkIssueService coWorkIssueService;
    private final ConfigService configService;
    private final ScheduleService scheduleService;
    private final WorkService workService;
    
    @GetMapping("/addModelFile/{coWorkSnapShotId}")
    public String addModelFile(@PathVariable Long coWorkSnapShotId, Model model) {
    	
    	model.addAttribute("coWorkSnapShotId", coWorkSnapShotId);
    	model.addAttribute("snapShotFileExtension", String.join("||", configService.findFileExtension("VIEW_POINT_FILE_EXT", true, userInfo.getProjectId())));
    	return "coWork/modal/addModelFile";
    }

    @GetMapping("/addCoWorkIssue/{coWorkId}")
    public String addCoWorkIssue(@PathVariable Long coWorkId, Model model) {

        return coWorkIssueItem(new CoWorkIssue(coWorkId, userInfo.getId()), model);
    }

    @NotNull
    private String coWorkIssueItem(CoWorkIssue coWorkIssue, Model model) {
        List<SearchUserDTO> searchUserDTOs = searchUserService.findCoWorkJoinerDTOs(coWorkIssue.getCoWork().getId());
        List<Long> fixedIds = new ArrayList<>();
        fixedIds.add(coWorkIssue.getWriteEmbedded().getWriter().getId());
        searchUserDTOs.forEach(t->{
            t.setSelected(coWorkIssue.joinerIds());
            t.setFixed(fixedIds);
        });
        model.addAttribute("coWorkIssue", coWorkIssue);
        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("modalScopeId", "coWorkIssueItem");
        model.addAttribute("formElementIdForUserIds", "coWorkIssueJoiner");
        model.addAttribute("formElementIdForModal", "");
        return "coWork/modal/coWorkIssueItem";
    }

    @GetMapping("/updateCoWorkIssue/{coWorkIssueId}")
    public String updateCoWorkIssue(@PathVariable Long coWorkIssueId, Model model) {
        CoWorkIssue coWorkIssue = coWorkService.findByCoWorKIssueId(coWorkIssueId);
        return coWorkIssueItem(coWorkIssue, model);
    }

    @GetMapping("/addIssueReport/{issueId}")
    public String addIssueReport(@PathVariable Long issueId, Model model) {
        // 작업내용 등록 권한 존재 여부 체크

        model.addAttribute("issueFileExtension", String.join("||", configService.findFileExtension("ISSUE_FILE_EXT", true, userInfo.getProjectId())));
        return "coWork/modal/addIssueReport";
    }

    @GetMapping("/coWorkIssueReport/{coWorkIssueId}")
    public String coWorkIssueReport(@PathVariable Long coWorkIssueId, Model model) {

        model.addAttribute("coWorkIssueId",coWorkIssueId);
        model.addAttribute("coWorkIssueFileExtension", String.join("||", configService.findFileExtension("CO_WORK_ISSUE_FILE_EXT", true, userInfo.getProjectId())));
        return "coWork/modal/coWorkIssueReport";
    }

    @GetMapping("/coWorkItem/{coWorkId}")
    public String coWorkItem(@PathVariable Long coWorkId, Model model) {
        CoWork coWork = coWorkService.findByCoWorkId(coWorkId);

        List<SearchUserDTO> searchUserDTOs = searchUserService.findSearchUserDTOs();
        searchUserDTOs.forEach(t->t.setSelected(coWork.joinerIds()));
        searchUserDTOs.forEach(t->t.setFixed(coWork.allJoinerIds()));

        model.addAttribute("coWork", coWork);
        model.addAttribute("searchUserDTOs", searchUserDTOs);
        model.addAttribute("modalScopeId", "coWorkItem");
        model.addAttribute("formElementIdForUserIds", "coWorkJoiner");
        model.addAttribute("formElementIdForModal", "");

        return "coWork/modal/coWorkItem";
    }

    @GetMapping("/coWorkJoinerList/{coWorkId}")
    public String coWorkJoinerList(@PathVariable Long coWorkId, Model model) {
        model.addAttribute("coWorkJoiners", coWorkJoinerService.findAccountDTOByCoWorkId(coWorkId));
        return "coWork/modal/coWorkJoinerList";
    }

    @GetMapping("/coWorkModelingList/{coWorkId}")
    public String coWorkModelingList(@PathVariable Long coWorkId, Model model) {
        model.addAttribute("coWorkModelings", coWorkService.findModelingByCoWorkId(coWorkId));
        return "coWork/modal/coWorkModelingList";
    }

    @GetMapping("/coWorkIssueList/{coWorkId}")
    public String coWorkIssueList(@PathVariable Long coWorkId, Model model) {
        model.addAttribute("coWorkIssues", coWorkIssueService.findByCoWorkId(coWorkId));
        return "coWork/modal/coWorkIssueList";
    }

    @GetMapping("/addSchedule")
    public String addSchedule(Model model) {
        model.addAttribute("scheduleVO", new ScheduleVO(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        model.addAttribute("scheduleTypes", configService.findScheduleConfigs().stream().filter(t -> "HOLIDAY".equalsIgnoreCase(t.getCode()) == false ).collect(Collectors.toList()));
        return "coWork/modal/addSchedule";
    }

    @GetMapping("/updateSchedule")
    public String updateSchedule(@RequestParam(required = false, defaultValue = "0") long mScheduleId
            , Model model) {
        Schedule scheduleItem = scheduleService.findById(mScheduleId);

        if(scheduleItem.getWriteEmbedded().getWriter().getId() != userInfo.getId()) {
            return "redirect:/coWork/viewSchedule?mScheduleId=" + mScheduleId;
        }

        model.addAttribute("scheduleItem", scheduleItem);
        model.addAttribute("scheduleTypes", configService.findScheduleConfigs());
        return "coWork/modal/updateSchedule";
    }

    @GetMapping("/viewSchedule")
    public String viewSchedule(@RequestParam(required = false, defaultValue = "0") long mScheduleId
           , Model model) {

        Schedule scheduleItem = scheduleService.findById(mScheduleId);

        model.addAttribute("scheduleItem", scheduleItem);
        model.addAttribute("isTotalWorkCnt", isTotalWorkCnt(scheduleItem));
        return "coWork/modal/viewSchedule";
    }

    private boolean isTotalWorkCnt(Schedule scheduleItem) {
        return workService.findUseWorkAll().size() == scheduleItem.getWorks().size();
    }
}
