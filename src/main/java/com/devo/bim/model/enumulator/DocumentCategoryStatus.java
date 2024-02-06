package com.devo.bim.model.enumulator;

public enum DocumentCategoryStatus {
    USE("사용"),
    DEL("미사용");

    private String value;

    DocumentCategoryStatus(String value) {
        this.value = value;
    }
}

