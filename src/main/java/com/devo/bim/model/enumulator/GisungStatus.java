package com.devo.bim.model.enumulator;

public enum GisungStatus {
    COMPLETE("승인", "project.gisung_status.complete"),
    WRITING( "작성중", "project.gisung_status.writing");

    private String value;
    private String messageProperty;

    GisungStatus(String value, String messageProperty) {
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
