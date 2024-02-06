package com.devo.bim.model.enumulator;

public enum AlertType {

    NONE("없음", "common.alert.alert_type.none", ""),
    NOTICE("공지", "common.alert.alert_type.notice", "/coWork/notificationView?noticeId=%s"),
    ISSUE("일반이슈", "common.alert.alert_type.issue", "/coWork/issueView?issueId=%s"),
    JOB_SHEET("공사일지", "common.alert.alert_type.job_sheet", "/project/jobSheetView?id=%s"),
    CO_WORK("협업", "common.alert.alert_type.co_work", "/coWork/modelingView/%s"),
    CHATTING("채팅", "common.alert.alert_type.chatting", "/coWork/modelingView/%s"),
    CO_WORK_ISSUE("협업이슈", "common.alert.alert_type.co_work_issue", "/coWork/modelingViewIssue/%s"),
    PROCESS_ITEM("공정", "common.alert.alert_type.process_item", "/cost/index");

    private String value;
    private String messageProperty;
    private String path;

    AlertType(String value, String messageProperty, String path) {
        this.value = value;
        this.messageProperty = messageProperty;
        this.path = path;
    }

    public String getMessageProperty() {
        return messageProperty;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }
}

