package com.devo.bim.model.enumulator;

public enum PageSizeUseType {

    NONE("없음"),
    JOB_SHEET_LIST("공사일지"),
    BULLETIN_LIST("게시판"),
    CO_WORK_LIST("협업"),
    ISSUE_LIST("이슈"),
    NOTIFICATION_LIST("공지"),
    DOCUMENT_LIST("문서자료관리"),
    SEARCH_USER_LIST("사용자 검색"),
    PROCESS_CODE_VALIDATION_RESULT_LIST("공정 코드 검증"),
    ADMIN_COMPANY_LIST("관계사 목록"),
    STATISTICS_USER_LIST("통계 사용자"),
    ADMIN_USER_LIST("사용자 목록");

    private String value;

    PageSizeUseType(String value) {
        this.value = value;
    }
}

