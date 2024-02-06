package com.devo.bim.model.enumulator;

public enum ProcessCategoryDefaultCode {
    CATE1("00000"),
    CATE2("00000"),
    CATE3("00000"),
    CATE4("00000"),
    CATE5("00000"),
    CATE6("00000");

    private String value;

    ProcessCategoryDefaultCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
