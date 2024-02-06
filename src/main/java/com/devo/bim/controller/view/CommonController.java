package com.devo.bim.controller.view;

import com.devo.bim.exception.InternalServerErrorException;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.dto.AlertDTO;
import com.devo.bim.model.dto.ChattingAlertDTO;
import com.devo.bim.model.entity.Alert;
import com.devo.bim.model.entity.CoWorkIssue;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.service.AccountService;
import com.devo.bim.service.AlertService;
import com.devo.bim.service.ChatAPIService;
import com.devo.bim.service.CoWorkIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController extends AbstractController {

    private final AccountService accountService;
    private final AlertService alertService;
    private final CoWorkIssueService coWorkIssueService;
    private final ChatAPIService chatAPIService;

    @GetMapping("/zoomInImage")
    public String zoomInImage(Model model) {

        return "common/modal/zoomInImage";
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam(required = true, defaultValue = "0") long userId, Model model) {
        model.addAttribute("writerDTO", new AccountDTO(accountService.findById(userId)));

        return "common/userInfo";
    }

    @GetMapping("/userInfoAtField")
    public String userInfoAtField(@RequestParam(required = true, defaultValue = "0") long userId, Model model) {
        System.out.println("userId = " + userId);
        if (userId == 0) {
            userId = userInfo.getId();
        }
        model.addAttribute("writerDTO", new AccountDTO(accountService.findById(userId)));

        return "common/userInfoAtField";
    }

    @GetMapping("/userInfoAtFields")
    public String userInfoAtFields(@RequestParam(required = true, defaultValue = "0") String userIds, Model model) {

        List<Long> userIdList = Arrays.asList(userIds.split(","))
                .stream()
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());

        List<AccountDTO> accountDTOS = accountService.findAccountDTOsByIds(userIdList);
        model.addAttribute("writerDTOs", accountDTOS);

        return "common/userInfoAtFields";
    }

    @GetMapping("/alertLayer")
    public String alertLayer(Model model) {
        model.addAttribute("alerts", alertService.findAlertByReceiverId(AlertType.NONE, 50L));

        return "common/alertLayer";
    }

    @GetMapping("/chattingLayer")
    public String chattingLayer(Model model) {
        getUserChatInfo(model);
        if (chatAPIService.procAuth(userInfo)) {
            model.addAttribute("rocketChatId", userInfo.getRocketChatId());
            model.addAttribute("rocketChatToken", userInfo.getRocketChatToken());
        }

        return "common/chattingLayer";
    }

    private void getUserChatInfo(Model model) {
        List<ChattingAlertDTO> chattingRooms = chatAPIService.getUserChatInfo();
        model.addAttribute("chattingRooms", chattingRooms);

        if (chattingRooms.size() > 0) {
            int totalUnreadCount = chattingRooms.stream().mapToInt(ChattingAlertDTO::getUnread).sum();
            model.addAttribute("totalUnreadCount", totalUnreadCount > 99 ? "99+" : totalUnreadCount);
        } else {
            model.addAttribute("totalUnreadCount", 0);
        }
    }

    @GetMapping("/chattingList")
    public String chattingList(Model model) {
        getUserChatInfo(model);

        return "common/chattingList";
    }

    @GetMapping("moveToItem/{alertId}")
    public String moveToItem(@PathVariable long alertId) {

        AlertDTO alertDTO = alertService.findById(alertId);
        if (!alertDTO.isSuccess()) new InternalServerErrorException(proc.translate(alertDTO.getReturnMessage()));
        if (Objects.isNull(alertDTO.getAlert())) new NotFoundException(proc.translate("system.moveToItem.no_alert"));
        return "redirect:" + alertDTO.getAlert().getPath();
    }

    @GetMapping("moveToIssue/{issueId}")
    public String moveToIssue(@PathVariable(required = true) long issueId) {

        Alert savedAlert = alertService.setIsReadTrueAndReturn(userInfo.getProjectId(), userInfo.getId(), issueId, AlertType.ISSUE);

        if (savedAlert.getId() != 0) return "redirect:" + savedAlert.getPath();
        return "redirect:/coWork/issueView?issueId=" + issueId;
    }

    @GetMapping("moveToCoWorkIssue/{coWorkIssueId}")
    public String moveToCoWorkIssue(@PathVariable(required = true) long coWorkIssueId) {

        Alert savedAlert = alertService.setIsReadTrueAndReturn(userInfo.getProjectId(), userInfo.getId(), coWorkIssueId, AlertType.CO_WORK);

        if (savedAlert.getId() != 0) return "redirect:" + savedAlert.getPath();

        CoWorkIssue coWorkIssue = coWorkIssueService.findByCoWorkIssueId(coWorkIssueId);
        return "redirect:/coWork/modelingView/" + coWorkIssue.getCoWork().getId() + "?tabType=issueArea&tabItemId=" + coWorkIssueId;
    }

    @GetMapping("moveToNotice/{noticeId}")
    public String moveToNotice(@PathVariable(required = true) long noticeId) {

        Alert savedAlert = alertService.setIsReadTrueAndReturn(userInfo.getProjectId(), userInfo.getId(), noticeId, AlertType.NOTICE);

        if (savedAlert.getId() != 0) return "redirect:" + savedAlert.getPath();
        return "redirect:/coWork/notificationView?noticeId=" + noticeId;
    }

}

