package com.devo.bim.model.enumulator;

public enum TaskStatus {
    REG("사용중"),
    DEL("삭제");

    private String value;

    TaskStatus(String value) {
        this.value = value;
    }
}

