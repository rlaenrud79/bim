package com.devo.bim.model.enumulator;

public enum IssueStatus {
    WRITE("작성중","co_work.modal.add_co_work_issue.issue_status_write"),
    REQUEST("의뢰","co_work.modal.add_co_work_issue.issue_status_request"),
    GOING("진행중","co_work.modal.add_co_work_issue.issue_status_going"),
    COMPLETE("완료","co_work.modal.add_co_work_issue.issue_status_complete");

    private String value;
    private String messageProperty;

    IssueStatus(String value, String messageProperty) {
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

