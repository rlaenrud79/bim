package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchDocumentCategoryVO {
    private long projectId;
    private String lang;

    private String SortProp;
    private String searchValue;
}
