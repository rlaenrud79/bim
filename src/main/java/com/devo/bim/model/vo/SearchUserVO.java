package com.devo.bim.model.vo;

import lombok.Data;

@Data
public class SearchUserVO {

    private long projectId;
    private String formElementIdForUserId = "";
    private String formElementNameForUserName = "";
}
