package com.devo.bim.service;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.dto.AlertDTO;
import com.devo.bim.model.entity.Alert;
import com.devo.bim.model.entity.JobSheet;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.repository.dsl.AlertDslRepository;
import com.devo.bim.repository.spring.AlertRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlertService extends AbstractService {

    private final AlertRepository alertRepository;
    private final AlertDslRepository alertDslRepository;

    public void saveAlert(boolean isNew, long itemId, long receiverId, String title, AlertType alertType){
        saveAlert(isNew, itemId, receiverId, title, alertType, false, null, null);
    };

    public void saveAlert(boolean isNew, long itemId, long receiverId, String title, AlertType alertType, boolean isPopup, LocalDateTime popupStartDate, LocalDateTime popupEndDate){

        alertRepository.save(
                new Alert(isNew ? title : proc.translate("system.alert_service_re_alert") + " " + title
                , userInfo.getProjectId()
                , receiverId
                , itemId
                , alertType
                , isPopup
                , popupStartDate
                , popupEndDate)
        );
    };

    public void insertAlertForIssue(long projectId, long receiverId, String title, long issueId, String actionType) {

        title = "INSERT".equalsIgnoreCase(actionType) ? title :  proc.translate("system.alert_service_re_alert") + " " + title;

        Alert alert = new Alert(title
                , projectId, receiverId
                , "/coWork/issueView?issueId=" + issueId
                , AlertType.ISSUE
                , issueId, true);
        alertRepository.save(alert);
    }

    public void insertAlertForNotice(long projectId, long receiverId, Notice notice, String actionType) {

        String title = "INSERT".equalsIgnoreCase(actionType) ? notice.getTitle() : proc.translate("system.alert_service_re_alert") + " " + notice.getTitle();

        Alert alert = new Alert(title
                , projectId, receiverId
                , "/coWork/notificationView?noticeId=" + notice.getId()
                , AlertType.NOTICE
                , notice.getId(), true
                , notice.isPopup(), notice.getStartDate(), notice.getEndDate());

        alertRepository.save(alert);
    }

    public void insertAlertForProcessItem(long projectId, long receiverId, ProcessItem processItem, String actionType) {

        Alert alert = new Alert( getAlertTitleForProcessItem(actionType, processItem)
                , projectId, receiverId
                , "/cost/index"
                , AlertType.PROCESS_ITEM
                , 0L, true);

        alertRepository.save(alert);
    }

    private String getAlertTitleForProcessItem(String actionType, ProcessItem processItem){

        if("PROCESS_ITEM_INSERT".equalsIgnoreCase(actionType)) {
            return "[" + proc.translate("system.alert_service.new_process_item") + "]" + processItem.getTaskFullPath();
        }

        if("PROCESS_ITEM_DELETE".equalsIgnoreCase(actionType)) {
            return  "[" + proc.translate("system.alert_service.delete_process_item") + "]" + processItem.getTaskFullPath();
        }

        if("NEW_VERSION".equalsIgnoreCase(actionType)) {
            return  "[" + proc.translate("system.alert_service.new_version") + "]" + proc.translate("system.alert_service.new_version_title");
        }

        return proc.translate("system.alert_service.new_alert");
    }

    public void insertAlertForJobSheet(long projectId, long receiverId, JobSheet jobSheet, String actionType) {
        String title = "INSERT".equalsIgnoreCase(actionType) ? jobSheet.getTitle() : proc.translate("system.alert_service_re_alert") + " " + jobSheet.getTitle();

        Alert alert = new Alert(title
                , projectId
                , receiverId
                , "/project/jobSheetView?id=" + jobSheet.getId()
                , AlertType.JOB_SHEET
                , jobSheet.getId()
                , true
        );
        alertRepository.save(alert);
    }

    @Transactional
    public void setIsReadTrue(long projectId, long receiverId, long refId, AlertType alertType) {
        Alert savedAlert = alertDslRepository.findByProjectIdAndReceiverIdAndRefIdAndAlertType(projectId, receiverId, refId, alertType);
        if (savedAlert != null) savedAlert.setIsReadTrue();
    }

    @Transactional
    public void setIsReadTrueForProcessItem() {
        List<Alert> alerts = findAlertByReceiverId(AlertType.PROCESS_ITEM);
        if(alerts.size() > 0) {
            alerts.forEach(t -> {
                t.setIsReadTrue();
            });
        }
    }

    @Transactional
    public Alert setIsReadTrueAndReturn(long projectId, long receiverId, long refId, AlertType alertType) {
        Alert savedAlert = alertDslRepository.findByProjectIdAndReceiverIdAndRefIdAndAlertType(projectId, receiverId, refId, alertType);
        if (Objects.isNull(savedAlert)) return new Alert();
        savedAlert.setIsReadTrue();
        return savedAlert;
    }

    @Transactional
    public void setDisabledAlert(long projectId, long refId, AlertType alertType) {
        List<Alert> savedAlerts = alertRepository.findByProjectIdAndRefIdAndType(projectId, refId, alertType);

        savedAlerts.forEach(t -> {
            t.setAlertEnabledAtAlertService(false);
            alertRepository.save(t);
        });
    }

    @Transactional
    public void setDisabledAlert(long projectId, long refId, long receiver, AlertType alertType) {
        List<Alert> savedAlerts = alertRepository.findByProjectIdAndRefIdAndReceiverIdAndType(projectId, refId, receiver, alertType);

        savedAlerts.forEach(t -> {
            t.setAlertEnabledAtAlertService(false);
            alertRepository.save(t);
        });
    }

    @Transactional
    public void setDisabledPreviousAlert(long projectId, long id, AlertType alertType) {
        List<Alert> savedAlerts = alertRepository.findByProjectIdAndRefIdAndType(projectId, id, alertType);
        if (savedAlerts.size() > 0) {
            savedAlerts.forEach(t -> {
                t.setAlertEnabledAtAlertService(false);
                alertRepository.save(t);
            });
        }
    }

    public List<Alert> findAlertByReceiverId(AlertType alertType, long topCount) {
        return alertDslRepository.findAlertByReceiverId(userInfo.getProjectId(), userInfo.getId(), alertType, topCount);
    }

    public List<Alert> findAlertByReceiverId(AlertType alertType) {
        return alertDslRepository.findAlertByReceiverId(userInfo.getProjectId(), userInfo.getId(), alertType);
    }

    @Transactional
    public AlertDTO findById(long alertId) {
        Alert savedAlert = alertRepository.findById(alertId).orElseGet(Alert::new);

        if (savedAlert.getId() == 0) return new AlertDTO(false, "system.alert_service.no_alert");
        if (savedAlert.getReceiverId() != userInfo.getId()) return new AlertDTO(false, "system.alert_service.no_receiver");

        savedAlert.setIsReadTrue();

        return new AlertDTO(true, savedAlert);
    }

    public List<AccountDTO> findConfirmUserByRefIdAndType(long projectId, long refId, AlertType alertType) {
        return alertDslRepository.findConfirmUserByRefIdAndType(projectId, refId, alertType);
    }

    public Alert findAlertByAlertTypeAndRefIdAndReceiverIdAndProjectId(AlertType alertType, long refId, long receiverId, long projectId) {
        return alertDslRepository.findByProjectIdAndReceiverIdAndRefIdAndAlertType(projectId, receiverId, refId, alertType);
    }

    @Transactional
    public JsonObject putNoPopup(long alertId) {
        // 1. alert 조회
        Alert savedAlert = alertRepository.findByIdAndProjectIdAndReceiverId(alertId, userInfo.getProjectId(), userInfo.getId()).orElseGet(Alert::new);

        // 2. 존재여부 체크
        if (savedAlert.getId() == 0) return proc.getResult(false, "system.alert_service.update_alert.no_alert");

        // 3. 권한 체크
        if (!haveRightForUpdate(savedAlert)) return proc.getResult(false, "system.alert_service.update_alert.no_have_right_update");

        try {
            // 4. alert 수정
            savedAlert.setNoPopupTrueAtPutNoPopup();

            return proc.getResult(true, "");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean haveRightForUpdate(Alert alert) {
        if (alert.getReceiverId() == userInfo.getId()) return true;
        return false;
    }
}
