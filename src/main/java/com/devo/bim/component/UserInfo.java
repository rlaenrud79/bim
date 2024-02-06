package com.devo.bim.component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.devo.bim.model.enumulator.CompanyRoleType;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.DateFormatEnum;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Enumerated;

import static javax.persistence.EnumType.STRING;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
@ToString
public class UserInfo implements Serializable {
    //***************************************************************
    // 주의 : UserInfo 는 Session 사용자 Object 입니다.
    //       Session 에 속성으로 Object 를 사용하면 Tomcat Restart 시
    //       오류가 발생합니다.
    //       변수는 String 유형을 제외하고 기본형만 사용하세요.
    //***************************************************************
    private static final long serialVersionUID = 1L;

    private long id = 0;
    private String email;
    private long companyId;
    private String userName;
    private String rank;
    private String phoneNo;
    private String mobileNo;
    private String address;
    private String photoPath;
    private long projectId;

    @Enumerated(STRING)
    private String companyRoleType;
    private long companyRoleId;
    private String sessionId;

    private String roleCodesString;
    private String rocketChatId;
    private String rocketChatToken;
    private String viewerSetting;
    private boolean isValidLicense = true;
    private String productProvider = "";
    private Integer upperMenu;
    private Integer sideMenu;
    private boolean isEmployer = true;          // 발주처 계정여부

    public void setUserInfo(Account loginAccount, String sessionId, String productProvider) {
        this.id = loginAccount.getId();
        this.email = loginAccount.getEmail();
        this.projectId = loginAccount.getCompany().getProject().getId();
        this.companyId = loginAccount.getCompany().getId();
        this.companyRoleType = loginAccount.getCompany().getRoleType().toString();
        this.companyRoleId = loginAccount.getCompany().getCompanyRole().getId();
        this.sessionId = sessionId;
        this.isValidLicense = loginAccount.getCompany().getProject().isValidLicense();
        this.productProvider = productProvider;
        this.isEmployer = loginAccount.isEmployer();

        setUserInfo(loginAccount);
    }

    public void setUserInfo(Account loginAccount) {
        this.userName = loginAccount.getUserName();
        this.rank = loginAccount.getRank();
        this.phoneNo = loginAccount.getPhoneNo();
        this.mobileNo = loginAccount.getMobileNo();
        this.address = loginAccount.getAddress();
        this.photoPath = loginAccount.getPhotoPath();

        this.roleCodesString = loginAccount.getRoles().stream().map(t -> t.getCode().name()).collect(Collectors.joining(","));
        this.rocketChatId = loginAccount.getRocketChatId();
        this.rocketChatToken = loginAccount.getRocketChatToken();
        this.viewerSetting = loginAccount.getViewerSetting();
        this.upperMenu = loginAccount.getUpperMenu();
        this.sideMenu = loginAccount.getSideMenu();
    }

    public String getNameAndEmail() {
        return userName + "(" + email + ")";
    }

    public String getUserCreateText() {
        return "create by " + getNameAndEmail() + " on " + Utils.getDateTimeByNationAndFormatType(LocalDateTime.now(), DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
    }

    public boolean isMe(long accountId) {
        return this.id == accountId;
    }

    public boolean isRoleAdminProject() {
        return roleCodesString.contains("ROLE_ADMIN_PROJECT");
    }

    public boolean isRoleAdminProcess() {
        return roleCodesString.contains("ROLE_ADMIN_PROCESS");
    }

    public boolean isRoleAdminEstimate() {
        return roleCodesString.contains("ROLE_ADMIN_ESTIMATE");
    }

    public boolean isRoleAdminWork() {
        return roleCodesString.contains("ROLE_ADMIN_WORK");
    }

    public boolean isRoleUserNormal() {
        return roleCodesString.contains("ROLE_USER_NORMAL");
    }

    public boolean isRoleAdminSystem() {
        return roleCodesString.contains("ROLE_ADMIN_SYSTEM");
    }

    public void setRocketChatToken(String token){
        this.rocketChatToken = token;
    }

    public void setViewerSetting(String viewerSetting) { this.viewerSetting = viewerSetting; }

    public boolean isCompanyRoleTypeHead() {
        return companyRoleType.contains("HEAD");
    }
    public boolean isCompanyRoleTypeLead() {
        return companyRoleType.contains("LEAD");
    }
    public boolean isCompanyRoleTypePartner() { return companyRoleType.contains("PARTNER"); }

}