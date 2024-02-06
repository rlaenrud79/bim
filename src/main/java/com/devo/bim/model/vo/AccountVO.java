package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountVO {

    private long id;

    private String email;
    private String password;
    private long companyId;

    private String userName;
    private String rank;
    private String phoneNo;
    private String mobileNo;
    private String address;
    private Integer enabled;
    private boolean responsible;

    private String works;
    private String roles;

    private String newPassword;
    private String encodedPassword;

    private boolean isChangePassword = false;

    private long userGrantorId;
    private String userReferencesIds;
    private long selectedTemplate;

    private Integer upperMenu;
    private Integer sideMenu;
    private boolean employer;

}