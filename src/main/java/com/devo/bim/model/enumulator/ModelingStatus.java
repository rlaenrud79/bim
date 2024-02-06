package com.devo.bim.model.enumulator;

public enum ModelingStatus {
    USED("도면 파일 존재, 선택 가능"),
    UNUSED("도면 파일 존재, 선택 불가"),
    DEL("도면 파일 삭제, 선택 불가");

    private String value;

    ModelingStatus(String value) {
        this.value = value;
    }
}

