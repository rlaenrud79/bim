package com.devo.bim.model.enumulator;

public enum JobSheetStatus {
    GOING("진행중", "project.job_sheet_status.going"),
    REJECT("반려", "project.job_sheet_status.reject"),
    COMPLETE("승인", "project.job_sheet_status.complete"),
    WRITING( "작성중", "project.job_sheet_status.writing");

    private String value;
    private String messageProperty;

    JobSheetStatus(String value, String messageProperty) {
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

