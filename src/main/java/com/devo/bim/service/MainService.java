package com.devo.bim.service;

import com.devo.bim.model.dto.MainIssueNoticeDTO;
import com.devo.bim.model.dto.UserPopUpNoticeDTO;
import com.devo.bim.model.entity.Alert;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.repository.dsl.AlertDslRepository;
import com.devo.bim.repository.dsl.MainIssueNoticeDTODslRepository;
import com.devo.bim.repository.spring.AlertRepository;
import com.devo.bim.repository.spring.NoticeRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService extends AbstractService {

    private final AlertDslRepository alertDslRepository;
    private final AlertRepository alertRepository;
    private final NoticeRepository noticeRepository;
    private final MainIssueNoticeDTODslRepository mainIssueNoticeDTODslRepository;

    public JsonObject getUserPopUpNotice(){
        return proc.getResult(true, getAlertNoticesPopupByReceiverId());
    }

    public UserPopUpNoticeDTO getNoticeAlertById(long alertId) {

        Alert savedAlert = getAlertNoticePopupById(alertId);
        Notice savedNotice = getNoticeByRefId(savedAlert.getRefId());

        return new UserPopUpNoticeDTO(savedAlert, savedNotice.getContents());
    }

    private Notice getNoticeByRefId(long noticeId) {
        return noticeRepository.findById(noticeId).orElseGet(Notice::new);
    }

    private List<Alert> getAlertNoticesPopupByReceiverId() {
        return alertDslRepository.findAlertNoticesPopupByReceiverId(userInfo.getProjectId(), userInfo.getId());
    }

    private Alert getAlertNoticePopupById(long alertId) {
        return alertRepository.findByIdAndProjectIdAndReceiverId(alertId, userInfo.getProjectId(), userInfo.getId()).orElseGet(Alert::new);
    }

    public List<MainIssueNoticeDTO> getMainCoWorkIssues(int maxSize){
        return mainIssueNoticeDTODslRepository.findMainCoWorkIssueByWriterOrIssueJoiner("CO_WORK_ISSUE", userInfo.getId(), userInfo.getProjectId(), userInfo.isRoleAdminProject(), maxSize);
    }

    public List<MainIssueNoticeDTO> getMainIssues(int maxSize){
        return mainIssueNoticeDTODslRepository.findMainIssueByWriterOrIssueManager("ISSUE", userInfo.getId(), userInfo.getProjectId(), userInfo.isRoleAdminProject(), maxSize);
    }

    public List<MainIssueNoticeDTO> getNotices(int maxSize){
        return mainIssueNoticeDTODslRepository.findMainNoticeByWriterOrSameWork("NOTICE", userInfo.getId(), userInfo.getProjectId(), userInfo.isRoleAdminProject(), maxSize);
    }
}
