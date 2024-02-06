package com.devo.bim.model.vo;

import com.devo.bim.model.entity.CompanyRoleName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CompanyRoleVO {

    private long id;
    private int sortNo;
    private List<CompanyRoleName> companyRoleNames = new ArrayList<>();

    public String getCompanyRoleName(String languageCode) {
        return this.companyRoleNames
                .stream()
                .filter(t-> languageCode.equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(CompanyRoleName::new)
                .getName();
    }
}
