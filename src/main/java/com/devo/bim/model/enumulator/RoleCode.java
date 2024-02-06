package com.devo.bim.model.enumulator;

public enum RoleCode {
    ROLE_ADMIN_PROJECT("사업 관리자"),
    ROLE_ADMIN_PROCESS("공정 관리자"),
    ROLE_ADMIN_WORK("공종 관리자"),
    ROLE_ADMIN_ESTIMATE("내역 관리자"),
    ROLE_USER_NORMAL("일반 사용자"),
    ROLE_ADMIN_SYSTEM("시스템 관리자");

    private String value;
    RoleCode(String value) {
        this.value = value;
    }

}
