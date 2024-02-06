package com.devo.bim.model.vo;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchCodeValidationVO {
    private long projectId;

    private boolean mSuccess = false;
    private boolean mFail = false;
    private boolean mNone = false;

    private String mSearchType = "";
    private String mSearchText = "";
}
