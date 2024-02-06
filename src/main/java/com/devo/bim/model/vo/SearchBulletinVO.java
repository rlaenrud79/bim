package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchBulletinVO {
    private long projectId = 0L;
    private String searchType = "none";
    private String searchValue = "";
    private long searchUserId = 0L;
    private String searchUserDisplayName = "";
    private String startDateString = "";
    private String endDateString = "";
    private String SortProp = "";
    private Boolean keepCondition = false;

    @Override
    public String toString() {
        return "projectId=" + projectId +
                "&searchType=" + searchType +
                "&searchValue=" + searchValue +
                "&searchUserId=" + searchUserId +
                "&searchUserDisplayName=" + searchUserDisplayName +
                "&startDateString=" + startDateString +
                "&endDateString=" + endDateString +
                "&SortProp=" + SortProp +
                "&keepCondition=" + keepCondition;
    }
}
