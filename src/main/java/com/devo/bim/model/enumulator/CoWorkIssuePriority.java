package com.devo.bim.model.enumulator;

public enum CoWorkIssuePriority {
    NONE("선택 안함",""),
    EMERGENCY("긴급","co_work.modal.add_co_work_issue.issue_priority_ultra"),
    HIGH("상","co_work.modal.add_co_work_issue.issue_priority_high"),
    MIDDLE("중", "co_work.modal.add_co_work_issue.issue_priority_middle"),
    LOW("하", "co_work.modal.add_co_work_issue.issue_priority_low");

    private String value;
    private String messageProperty;

    CoWorkIssuePriority(String value, String messageProperty) {
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

