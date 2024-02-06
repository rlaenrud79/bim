package com.devo.bim.model.enumulator;

public enum ExcelDownloadCaseType {

    NONE("없음"),
    ISSUE_LIST("일반이슈 목록"),
    USER_LIST("사용자 목록"),
    STATISTICS_USER_LIST("화면 접속 목록"),
    COMPANY_LIST("관계사 목록"),
    PROCESS("공정"),
    PHASING_CODE_VALIDATION_RESULT("페이징코드 검증 결과"),
    PROCESS_ITEM_COST("내역목록"),
    PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST("내역목록의 복합단가"),
    PROCESS_ITEM_COST_DETAIL("복합단가"),
    GISUNG_PROCESS_ITEM_COST("기성목록"),
    GISUNG_PROCESS_ITEM_COST_DETAIL_IN_GISUNG_PROCESS_ITEM("기성목록의 복합단가"),
    GISUNG_PROCESS_ITEM_COST_TOTAL("내역목록"),
    GISUNG_PROCESS_ITEM_COST_DETAIL_IN_PROCESS_ITEM_COST_TOTAL("내역목록의 복합단가"),
    PROCESS_WORKER_DEVICE_DETAIL("인원 장비 상세"),
    GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE("기성리스트 파일");
    
    private String value;

    ExcelDownloadCaseType(String value) {
        this.value = value;
    }
}

