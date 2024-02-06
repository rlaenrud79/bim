package com.devo.bim.model.enumulator;

public enum TaskType {

    PROJECT("project", "process.task_type.project"),
    TASK("task", "process.task_type.task"),
    MILESTONE("milestone", "process.task_type.milestone"),
    WORK("work", "process.task_type.work");

    private String value;
    private String messageProperty;

    TaskType(String value, String messageProperty) {
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
