package com.devo.bim.model.enumulator;

public enum ProcessValidateStatus {
    READY("준비"),
    GOING("검증중"),
    COMPLETE("완료");

    private String value;

    ProcessValidateStatus(String value) {
        this.value = value;
    }
}

