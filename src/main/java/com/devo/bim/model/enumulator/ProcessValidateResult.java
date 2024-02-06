package com.devo.bim.model.enumulator;

public enum ProcessValidateResult {
    NONE("실행전"),
    FAIL("실패"),
    SUCCESS("성공");

    private String value;

    ProcessValidateResult(String value) {
        this.value = value;
    }
}

