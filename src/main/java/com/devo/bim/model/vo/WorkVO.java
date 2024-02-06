package com.devo.bim.model.vo;

import com.devo.bim.model.entity.WorkName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class WorkVO {

    private long workId;
    private int sortNo;
    private int upId;
    private List<WorkName> workNames = new ArrayList<>();

    public String getWorkName(String languageCode) {
        return this.workNames
                .stream()
                .filter(t-> languageCode.equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(WorkName::new)
                .getName();
    }
}
