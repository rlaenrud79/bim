package com.devo.bim.model.enumulator;

public enum CalculateStatus {
    READY("준비"),
    GOING("산정중"),
    COMPLETE("완료");

    private String value;

    CalculateStatus(String value) {
        this.value = value;
    }
}

