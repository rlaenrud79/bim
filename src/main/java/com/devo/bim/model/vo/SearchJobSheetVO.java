package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchJobSheetVO {

    private long projectId = 0L;
    private String searchDivisionType = "none";
    private String searchDivisionValue = "";
    private String searchUserType = "none";
    private long searchUserValue = 0L;
    private String searchUserDisplayName = "";
    private String startDateString = "";
    private String endDateString = "";
    private String SortProp = "";
    private Boolean keepCondition = false;

    @Override
    public String toString() {
        return "projectId=" + projectId +
                "&searchDivisionType=" + searchDivisionType +
                "&searchDivisionValue=" + searchDivisionValue +
                "&searchUserType=" + searchUserType +
                "&searchUserValue=" + searchUserValue +
                "&searchUserDisplayName=" + searchUserDisplayName +
                "&startDateString=" + startDateString +
                "&endDateString=" + endDateString +
                "&SortProp=" + SortProp +
                "&keepCondition=" + keepCondition;
    }
}
