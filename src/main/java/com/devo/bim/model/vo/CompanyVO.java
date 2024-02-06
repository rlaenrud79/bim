package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CompanyVO {

    private long companyId;
    private String companyName;
    private String telNo;
    private long companyRoleId;
    private long isHead;
    private boolean isDisplay;
    private boolean isEnabled;

    private List<Long> companyWorks = new ArrayList<>();

    private long projectId;
    private long userId;
}
