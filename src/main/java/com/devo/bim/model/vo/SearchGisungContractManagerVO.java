package com.devo.bim.model.vo;

import com.devo.bim.model.enumulator.ConvertStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchGisungContractManagerVO {
    private long projectId = 0L;
    private String searchType = "none";
    private String searchValue = "";
    private long searchUserId = 0L;
    private String searchUserDisplayName = "";
    private String SortProp = "";
    private String lang = "";
    private long gisungId = 0L;

    public SearchGisungContractManagerVO(Long projectId){
        this.projectId = projectId;
    }
}
