package com.devo.bim.model.enumulator;

public enum WorkStatus {
    USE("사용"),
    DEL("미사용");

    private String value;

    WorkStatus(String value) {
        this.value = value;
    }
}

