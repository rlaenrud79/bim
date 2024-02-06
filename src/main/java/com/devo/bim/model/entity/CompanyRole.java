package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CompanyRole {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String name;
    private Integer sortNo;
    private boolean enabled;

    @OneToMany(fetch = LAZY, mappedBy = "companyRole")
    private List<Company> companies = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "companyRole")
    private List<CompanyRoleName> companyRoleNames = new ArrayList<>();

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public String getLocaleName(String languageCode){
        return companyRoleNames.stream().filter(t -> languageCode.equalsIgnoreCase(t.getLanguageCode())).findFirst().get().getName();
    }

    public CompanyRole(long companyRoleId){
        this.id = companyRoleId;
    }

    public void addCompanyRoleName(CompanyRoleName companyRoleName){
        this.companyRoleNames.add(companyRoleName);
    }

    public void setCompanyRoleAtAddCompanyRole(long projectId, int sortNo, String engName, long accountId){
        this.projectId = projectId;
        this.name = engName;
        this.sortNo = sortNo;
        this.enabled = true;
        this.writeEmbedded = new WriteEmbedded(accountId);
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public void setCompanyRoleAtUpdateCompanyRole(int sortNo, String engName, long accountId){
        this.name = engName;
        this.sortNo = sortNo;
        this.enabled = true;
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public void setEnabledCompanyRole(boolean enabled){
        this.enabled = enabled;
    }

    public void setSortNoAtAdminService(int sortNo){
        this.sortNo = sortNo;
    }

    public String getCompanyRoleName(){
        return this.companyRoleNames.stream()
                .filter(t -> LocaleContextHolder.getLocale() == t.getLanguageCodeLocale())
                .findFirst()
                .orElseGet(CompanyRoleName::new)
                .getName();
    }

    public String getCompanyRoleName(String languageCode) {
        return this.companyRoleNames.stream()
                .filter(t-> languageCode.equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(CompanyRoleName::new)
                .getName();
    }
}
