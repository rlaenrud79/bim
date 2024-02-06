package com.devo.bim.model.enumulator;

public enum FileUploadUIType {

    NONE("없음"),
    ACCOUNT_PHOTO_FILE("개인 정보 사진 첨부"),
    ACCOUNT_STAMP_FILE("도장 사진 첨부"),
    JOB_SHEET_FILE("공사일지 파일 첨부"),
    MODELING_FILE("BIM 모델 파일"),
    MODELING_IFC_FILE("BIM 모델 IFC 파일"),
    //MODELING_VIEW_POINT_MODEL_FILE("뷰포인트 도면 추가"),
    MODELING_SNAP_SHOT_MODEL_FILE("스냅샷 도면 추가"),
    DOCUMENT_FILE("문서자료관리 파일 첨부"),
    GISUNG_REPORT_SURVEY_FILE("기성문서관리 기성검사조사 파일 첨부"),
    GISUNG_REPORT_PART_SURVEY_FILE("기성문서관리 기성부분검사원 파일 첨부"),
    GISUNG_REPORT_AGGREGATE_FILE("기성문서관리 기성부분집계표(전체) 파일 첨부"),
    GISUNG_REPORT_PART_AGGREGATE_FILE("기성문서관리 기성부분집계표(연차) 파일 첨부"),
    GISUNG_REPORT_ACCOUNT_FILE("기성문서관리 기성부분내역서(연차) 파일 첨부"),
    GISUNG_REPORT_ETC_FILE("기성문서관리 기타 파일 첨부"),
    GISUNG_PAYMENT_FILE("기성결제승인문서 파일 첨부"),
    GISUNG_STAMP_FILE("도장 사진 첨부"),
    CO_WORK_ISSUE_REPORT("협업 이슈 작업 내용 첨부 파일"),
    CO_WORK_VIEW_POINT_MODEL_FILE("협업 뷰포인트 도면 추가"),
    CO_WORK_SNAP_SHOT_MODEL_FILE("협업 스냅샷 도면 추가"),
    ISSUE_REPORT("일반 이슈 작업 내용 첨부 파일"),
    NOTIFICATION_FILE("공지 사항 첨부 파일"),
    BULLETIN_FILE("게시판 첨부 파일"),
    PROCESS_FILE("공정 파일"),
    PROJECT_IMAGE_FILE("사업 이미지 파일"),
    PHASING_CODE_FILE("공정 코드 파일"),
    GISUNG_LIST_EXCEL_FILE("기성 리스트 엑셀 첨부"),
    PROCESS_ITEM_COST_DETAIL_EXCEL_FILE("복합단가 업로드 엑셀 첨부"),
    PROCESS_ITEM_EXCEL_FILE("공정 업로드 엑셀 첨부");

    private String value;

    FileUploadUIType(String value) {
        this.value = value;
    }
}

