package com.devo.bim.model.enumulator;

public enum GisungCompareResult {
    NONE( "", ""),
    AP( "공정추가", "project.gisung_status.comprea_result_AP"),
    DW( "실적보다 작게 측정", "project.gisung_status.comprea_result_DW"),
    OV( "실적보다 높게 측정", "project.gisung_status.comprea_result_OV");

    private String value;
    private String messageProperty;

    GisungCompareResult(String value, String messageProperty) {
        this.value = value;
        this.messageProperty = messageProperty;
    }

    public String getMessageProperty() {
        return messageProperty;
    }

    public String getValue() {
        return value;
    }

    public static String getValueOrDefault(GisungCompareResult compareResult) {
        return compareResult != null ? compareResult.getValue() : "";
    }
}
