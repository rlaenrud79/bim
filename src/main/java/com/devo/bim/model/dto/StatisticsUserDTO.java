package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.CompanyRoleName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@RequiredArgsConstructor
public class StatisticsUserDTO {

    private String userName;
    private String email;
    private String companyName;
    private String rank;
    private String menuCode;
    private String menuName;
    private String workNames;
    private LocalDateTime accessDate;

    public StatisticsUserDTO(Account account, String menuCode, LocalDateTime accessDate)
    {
        this.userName = account.getUserName();
        this.email = account.getEmail();
        this.companyName = account.getCompany().getName();
        this.rank = account.getRank();
        this.menuCode = menuCode;
        this.accessDate = accessDate;
        this.workNames = account.getWorkNamesString();
    }

}
