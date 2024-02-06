package com.devo.bim.model.entity;

import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Alert extends ObjectModelHelper<Alert> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String title;
    private long projectId;
    private long receiverId;
    private boolean isRead;
    private LocalDateTime readDate;
    private String path;

    @Enumerated(STRING)
    private AlertType type;


    private long refId;
    private boolean enabled = true;  // 공지, 게시판, 이슈, 작업일보 원본 데이터 생성 시 false
    private LocalDateTime writeDate = LocalDateTime.now();
    private boolean noPopup;

    private boolean isPopup;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Alert(String title, long projectId, long receiverId, String path, AlertType type, long refId, boolean enabled){
        this.title = title;
        this.projectId = projectId;
        this.receiverId = receiverId;
        this.path = path;
        this.type = type;
        this.refId = refId;
        this.enabled = enabled;
    }

    public Alert(String title, long projectId, long receiverId, String path, AlertType type, long refId, boolean enabled
            , boolean isPopup, LocalDateTime startDate, LocalDateTime endDate){
        this.title = title;
        this.projectId = projectId;
        this.receiverId = receiverId;
        this.path = path;
        this.type = type;
        this.refId = refId;
        this.enabled = enabled;
        this.isPopup = isPopup;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Alert(String title, long projectId, long receiverId, long itemId, AlertType alertType, boolean isPopup, LocalDateTime popupStartDate, LocalDateTime popupEndDate) {
        this.title = title;
        this.projectId = projectId;
        this.receiverId = receiverId;
        this.path = String.format(alertType.getPath(), "" + itemId);
        this.type = alertType;
        this.refId = itemId;
        this.enabled = true;
        this.isPopup = isPopup;
        this.startDate = popupStartDate;
        this.endDate = popupEndDate;
    }

    public void setAlertEnabledAtAlertService(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIsReadTrue(){
        this.isRead = true;
        this.readDate = LocalDateTime.now();
    }

    public String getElapseTime(){
        String returnDate = "";

        LocalDateTime nowDateTime = LocalDateTime.now();
        long diffDay = ChronoUnit.DAYS.between(this.writeDate, nowDateTime);
        long diffHour = ChronoUnit.HOURS.between(this.writeDate, nowDateTime);
        long diffMin = ChronoUnit.MINUTES.between(this.writeDate, nowDateTime) % 60;

        if(diffDay > 0 ) returnDate += diffDay + "d ";
        if(diffHour > 0) returnDate += diffHour + "h ";
        returnDate += diffMin + "m";

        return returnDate;
    }

    public boolean isNewTab(){
        if(this.type == AlertType.CO_WORK || this.type == AlertType.CHATTING || this.type == AlertType.CO_WORK_ISSUE) return true;
        return false;
    }

    public void setNoPopupTrueAtPutNoPopup(){
        this.isRead = true;
        this.noPopup = true;
    }
}
