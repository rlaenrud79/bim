package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Company;
import com.devo.bim.model.entity.CompanyRole;
import com.devo.bim.model.entity.CompanyRoleName;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CompanyRoleNameDTO {

    private long companyId;
    private long companyRoleId;
    private long companyRoleNameId;
    private String languageCode;
    private String companyRoleName;

    public CompanyRoleNameDTO(Company company, CompanyRole companyRole, CompanyRoleName companyRoleName){
        this.companyId = company.getId();
        this.companyRoleId = companyRole.getId();
        this.companyRoleNameId = companyRoleName.getId();
        this.languageCode = companyRoleName.getLanguageCode();
        this.companyRoleName = companyRoleName.getName();
    }
}
