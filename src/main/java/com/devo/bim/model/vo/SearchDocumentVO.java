package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchDocumentVO {
    private long projectId = 0L;
    private long searchWorkId = 0L;
    private String searchType = "none";
    private String searchValue = "";
    private long searchUserId = 0L;
    private String searchUserDisplayName = "";
    private String startDateString = "";
    private String endDateString = "";
    private String SortProp = "";
    private String lang = "";
    private long searchCategory = 0L;
}
