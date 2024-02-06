package com.devo.bim.model.dto;

import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.CompanyRoleType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class CompanyDTO {

    private long id;
    private long projectId;
    private String name;

    private CompanyRoleType roleType;
    private CompanyRole companyRole;
    private String companyRoleName;

    private String telNo;
    private AccountDTO responsibleUserDTO;

    private LocalDateTime writeDate;
    private LocalDateTime lastModifyDate;

    private String status;
    private boolean isDisplay;

    private String worksNames;
    private String responsibleUserName;

    private List<Work> companyWorks = new ArrayList<>();

    public CompanyDTO(Company company, Account account) {
        this.id = company.getId();
        this.projectId = company.getProject().getId();
        this.name = company.getName();
        this.roleType = company.getRoleType();
        this.companyRole = company.getCompanyRole();
        this.companyRoleName = company.getCompanyRole().getCompanyRoleName();

        this.telNo = company.getTelNo();
        if(!Objects.isNull(account)) {
            this.responsibleUserDTO = new AccountDTO(account);
            this.responsibleUserName = this.responsibleUserDTO.getUserName();
        }
        else {
            this.responsibleUserDTO = new AccountDTO();
            this.responsibleUserName = "";
        }

        this.writeDate = company.getWriteEmbedded().getWriteDate();
        this.lastModifyDate = company.getLastModifyEmbedded().getLastModifyDate();

        this.status = company.getStatus();
        this.isDisplay = company.isDisplay();
        this.worksNames = company.getWorksNames();

        for (Work work : company.getWorks()) {
            this.companyWorks.add(work);
        }
    }
}
