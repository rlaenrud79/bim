package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Locale;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CompanyRoleName {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String languageCode;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="company_role_id")
    CompanyRole companyRole;

    public CompanyRoleName(String languageCode, String name){
        this.languageCode = languageCode;
        this.name = name;
    }

    public CompanyRoleName(long companyRoleId, String languageCode, String name){
        this.companyRole = new CompanyRole(companyRoleId);
        this.languageCode = languageCode;
        this.name = name;
    }

    public void setCompanyRoleId(long companyRoleId){
        this.companyRole = new CompanyRole(companyRoleId);
    }

    public void setNameAtUpdateCompanyRole(String languageCode, String name){
        this.languageCode = languageCode;
        this.name = name;
    }

    public void setCompanyRoleNameAtSearchUserDTO(long id, String languageCode, String name, long companyRoleId){
        this.id = id;
        this.languageCode = languageCode;
        this.name = name;
        this.companyRole = new CompanyRole(companyRoleId);
    }

    public Locale getLanguageCodeLocale(){
        if("EN".equalsIgnoreCase(this.languageCode)) return Locale.ENGLISH;
        if("KO".equalsIgnoreCase(this.languageCode)) return Locale.KOREAN;
        if("ZH".equalsIgnoreCase(this.languageCode)) return Locale.CHINESE;
        return Locale.ENGLISH;
    }


}
