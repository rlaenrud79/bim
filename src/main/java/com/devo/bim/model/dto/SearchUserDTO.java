package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.CompanyRoleName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class SearchUserDTO {

    private AccountDTO accountDTO;
    private List<CompanyRoleName> companyRoleNames = new ArrayList<>();
    private boolean isSelected = false;
    private boolean isFixed = false;
    private String isSearchResult = "FALSE";


    public SearchUserDTO(Account account){
        this.accountDTO = new AccountDTO(account);
    }

    public void addCompanyRoleName(long companyRoleNameId, String languageCode, String companyRoleName){
       CompanyRoleName tmpCompanyRoleName = new CompanyRoleName();
       tmpCompanyRoleName.setCompanyRoleNameAtSearchUserDTO(companyRoleNameId, languageCode, companyRoleName, this.accountDTO.getCompanyRoleId());
       this.companyRoleNames.add(tmpCompanyRoleName);
    }

    public String getCompanyRoleNameLocale(Locale userLocale){

        String finalLocaleCode = getLocaleCode(userLocale);

        return this.companyRoleNames.stream()
                .filter(t -> finalLocaleCode.equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(CompanyRoleName::new)
                .getName();
    }

    @NotNull
    private String getLocaleCode(Locale userLocale) {

        if (userLocale == Locale.ENGLISH) return  "EN";
        if (userLocale == Locale.KOREAN) return "KO";
        if (userLocale == Locale.CHINESE) return  "ZH";
        return "EN";

    }

    public void setSelected(List<Long> userIds)
    {
        this.isSelected = userIds.stream().filter(t->t==this.accountDTO.getUserId()).count() > 0;
    }

    public void setFixed(List<Long> userIds)
    {
        this.isFixed = userIds.stream().filter(t->t==this.accountDTO.getUserId()).count() > 0;
    }

    public void setSearchResultWithWork(List<Long> workIds){
        if(StringUtils.isEmpty(this.accountDTO.getWorkIds())) this.isSearchResult = "TRUE";
        else {
            List<String> accountWorkIds = Arrays.stream(this.accountDTO.getWorkIds().split(",")).collect(Collectors.toList());
            workIds.forEach(t -> {
                if (accountWorkIds.stream().filter(s -> Long.parseLong(s) == t).count() > 0) this.isSearchResult = "TRUE";
            });
        }
    }

    public void setSearchResultWithRoles(String accountRoles){
        List<String> roles = Arrays.stream(this.accountDTO.getAccountRoles().split(",")).collect(Collectors.toList());
        roles.forEach(t -> {
            if(accountRoles.contains(t)) this.isSearchResult = "TRUE";
        });
    }

    public void setSearchResulTrue(){
        this.isSearchResult = "TRUE";
    }
}
