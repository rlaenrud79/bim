package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyRoleDTO {
    private long id;
    private long projectId;
    private String roleName;
    private String roleNameLocale;
    private int sortNo;
    private boolean roleEnabled;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private LocalDateTime lastModifyDate;


    public CompanyRoleDTO(int sortNo){
        this.sortNo = sortNo;
    }

    public CompanyRoleDTO(long id, long projectId, String roleName, String roleNameLocale, int sortNo, boolean roleEnabled
            , Account writer
            , LocalDateTime writeDate, LocalDateTime lastModifyDate){
        this.id = id;
        this.projectId = projectId;
        this.roleName = roleName;
        this.roleNameLocale = roleNameLocale;
        this.sortNo = sortNo;
        this.roleEnabled = roleEnabled;
        this.writerDTO =  new AccountDTO(writer);
        this.writeDate = writeDate;
        this.lastModifyDate = lastModifyDate;
    }

    public String getWriteDate(DateFormatEnum dateFormatEnum){
        return Utils.getDateTimeByNationAndFormatType(this.writeDate, dateFormatEnum);
    }

    public String getLastModifyDate(DateFormatEnum dateFormatEnum){
        return Utils.getDateTimeByNationAndFormatType(this.lastModifyDate, dateFormatEnum);
    }

    public String getRoleEnabledString(){
        if(this.roleEnabled) return "Y";
        return "N";
    }
}
