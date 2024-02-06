package com.devo.bim.model.enumulator;

public enum FileDownloadUIType {

    NONE("없음"),
    MODELING_FILE("BIM 모델 파일"),
    MODELING_IFC_FILE("BIM 모델 IFC 파일"),
    //MODELING_VIEW_POINT_MODEL_FILE("뷰포인트 도면"),
    MODELING_SNAP_SHOT_MODEL_FILE("스냅샷 도면 추가"),
    //CO_WORK_VIEW_POINT_MODEL_FILE("협업 뷰포인트 도면"),
    CO_WORK_SNAP_SHOT_MODEL_FILE("협업 스냅샷 도면"),
    ISSUE_REPORT_FILE("이슈 리포트 파일"),
    CO_WORK_ISSUE_REPORT_FILE("협업 이슈 작업 내역 첨부 파일"),
    JOB_SHEET_FILE("공사일지 파일"),
    BULLETIN_FILE("게시판 첨부 파일"),
    DOCUMENT_FILE("문서자료관리 파일 첨부"),
    GISUNG_REPORT_SURVEY_FILE("기성문서관리 기성검사조사 파일 첨부"),
    GISUNG_REPORT_PART_SURVEY_FILE("기성문서관리 기성부분검사원 파일 첨부"),
    GISUNG_REPORT_AGGREGATE_FILE("기성문서관리 기성부분집계표(전체) 파일 첨부"),
    GISUNG_REPORT_PART_AGGREGATE_FILE("기성문서관리 기성부분집계표(연차) 파일 첨부"),
    GISUNG_REPORT_ACCOUNT_FILE("기성문서관리 기성부분내역서(연차) 파일 첨부"),
    GISUNG_REPORT_ETC_FILE("기성문서관리 기타 파일 첨부"),
    GISUNG_PAYMENT_FILE("기성결제승인문서 파일 첨부"),
    NOTIFICATION_FILE("공지 파일"),
    PROCESS_TEMPLATE("공정 템플릿"),
    GISUNG_EXCEL_FILE("기성전체 Excel 파일"),

    GISUNG_LIST_EXCEL_FILE("기성 리스트 Excel 파일"),
    PROCESS_ITEM_COST_DETAIL_EXCEL_FILE("복합단가 업로드 Excel 파일"),
    PROCESS_ITEM_EXCEL_FILE("공정 업로드 Excel 파일");

    private String value;

    FileDownloadUIType(String value) {
        this.value = value;
    }
}

