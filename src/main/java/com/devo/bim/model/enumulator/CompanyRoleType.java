package com.devo.bim.model.enumulator;

public enum CompanyRoleType {
    HEAD("발주사"),
    LEAD("원도급사"),
    PARTNER("관계사");

    private String value;

    CompanyRoleType(String value) {
        this.value = value;
    }
}
