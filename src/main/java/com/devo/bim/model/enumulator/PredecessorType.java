package com.devo.bim.model.enumulator;

public enum PredecessorType {

    NONE("", "process.predecessor_type.none"),
    FS("0", "process.predecessor_type.finish_to_start"),
    SS("1", "process.predecessor_type.start_to_start"),
    FF("2", "process.predecessor_type.finish_to_finish"),
    SF("3", "process.predecessor_type.start_to_finish");

    private String value;
    private String messageProperty;

    PredecessorType(String value, String messageProperty) {
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

