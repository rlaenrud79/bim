package com.devo.bim.model.enumulator;

public enum CoWorkStatus {

    GOING("진행중", "co_work.co_work_status.co_work_going"),
    COMPLETE("종결", "co_work.co_work_status.co_work_complete");

    private String value;
    private String messageProperty;

    CoWorkStatus(String value, String messageProperty) {
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
