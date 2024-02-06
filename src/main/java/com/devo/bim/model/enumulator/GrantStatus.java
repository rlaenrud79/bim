package com.devo.bim.model.enumulator;

public enum GrantStatus {
    READY("결재중", "project.job_sheet_view.grant_status_ready"),
    REJECT("반려", "project.job_sheet_view.grant_status_reject"),
    APPROVE("승인", "project.job_sheet_view.grant_status_approve");

    private String value;
    private String messageProperty;

    GrantStatus(String value, String messageProperty) {
        this.value = value;
        this.messageProperty = messageProperty;
    }

    public String getMessageProperty() {
        return messageProperty;
    }

    public String getValue() {
        return value;
    }
}

