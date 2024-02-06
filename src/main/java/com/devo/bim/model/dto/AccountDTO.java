package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Role;
import com.devo.bim.model.enumulator.CompanyRoleType;
import com.devo.bim.model.enumulator.RoleCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AccountDTO {

    private long userId;
    private String email;
    private String userName;
    private String rank;
    private String phoneNo;
    private String mobileNo;
    private String photoPath;
    private boolean responsible;
    private String accountRoles = "";

    private long companyId;
    private String companyName;
    private CompanyRoleType companyRoleType;
    private long companyRoleId;
    private String companyRoleName;

    private LocalDateTime lastModifyDate;
    private Integer enabled;

    private String workNames = "";
    private String workIds = "";

    private boolean isRoleAdminProcess = false;

    public AccountDTO(Account account){
        this.userId = account.getId();
        this.email =  StringUtils.isBlank(account.getEmail()) ? "" : account.getEmail();
        this.userName =  StringUtils.isBlank(account.getUserName()) ? "" : account.getUserName();
        this.rank = account.getRank();
        this.phoneNo =  StringUtils.isBlank(account.getPhoneNo()) ? "" : account.getPhoneNo();
        this.mobileNo =  StringUtils.isBlank(account.getMobileNo()) ? "" : account.getMobileNo();
        this.photoPath = account.getPhotoPath();
        this.responsible = account.isResponsible();

        this.accountRoles = setAccountRoles(account.getRoles());

        this.companyId = account.getCompany().getId();
        this.companyName =  StringUtils.isBlank(account.getCompany().getName()) ? "" : account.getCompany().getName();
        this.companyRoleType = account.getCompany().getRoleType();
        this.companyRoleId = account.getCompany().getCompanyRole().getId();
        this.companyRoleName =  StringUtils.isBlank(account.getCompany().getCompanyRole().getCompanyRoleName()) ? "" : account.getCompany().getCompanyRole().getCompanyRoleName();

        this.lastModifyDate = account.getLastModifyEmbedded().getLastModifyDate();
        this.enabled = account.getEnabled();
        this.workNames = account.getWorkNamesString();
        this.workIds = account.getWorksString();
        this.isRoleAdminProcess = account.getRoles()
                .stream()
                .filter(t -> RoleCode.ROLE_ADMIN_PROCESS == t.getCode())
                .collect(Collectors.toList())
                .size() > 0;
    }

    private String setAccountRoles(List<Role> Roles){
        String result = "";
        for (Role role : Roles) {
            result += role.getCode() + ",";
        }
        return result;
    }
}
