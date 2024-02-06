package com.devo.bim.model.enumulator;

public enum ConvertStatus {
    NONE(""),
    READY("준비"),
    REQUEST("변환요청"),
    CONVERTING("변환중"),
    CONVERT_SUCCESS("완료"),
    CONVERT_FAIL("변환 실패");

    private String value;

    ConvertStatus(String value) {
        this.value = value;
    }
}

