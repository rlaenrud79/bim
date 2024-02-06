package com.devo.bim.model.entity;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.AccountVO;

import lombok.Getter;

@Entity
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String email;
    private String password;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String userName;
    private String rank;
    private String phoneNo;
    private String mobileNo;
    private String address;
    private String photoPath;
    private Integer enabled;
    private Integer upperMenu;
    private Integer sideMenu;
    private boolean employer;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private boolean responsible;
    private String rocketChatId;
    private String rocketChatToken;
    private String viewerSetting;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "account_work",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "work_id")
    )
    private List<Work> works = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "owner")
    private List<MySnapShot> mySnapShots = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "account")
    private List<AccountGrantor> accountGrantors = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "account")
    private List<AccountReference> accountReferences = new ArrayList<>();

    private long jobSheetTemplateId;

    public Account(long id) {
        this.id = id;
    }

    public Account(){
        this.enabled = 1;
    }

    public Account(AccountVO accountVO, long writerId) {
        this.email = accountVO.getEmail();
        this.password = accountVO.getEncodedPassword();
        this.company = new Company(accountVO.getCompanyId());
        this.userName = accountVO.getUserName();
        this.rank = accountVO.getRank();
        this.phoneNo = accountVO.getPhoneNo();
        this.mobileNo = accountVO.getMobileNo();
        this.address = accountVO.getAddress();
        this.enabled = accountVO.getEnabled();
        this.responsible = accountVO.isResponsible();
        this.writeEmbedded = new WriteEmbedded(writerId);
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
        this.upperMenu = accountVO.getUpperMenu();
        this.employer = accountVO.isEmployer();

        Utils
            .getLongList(accountVO.getWorks(), ",")
            .forEach(work -> this.works.add(new Work(work)));

        Utils
            .getLongList(accountVO.getRoles(), ",")
            .forEach(role -> this.roles.add(new Role(role)));
    }

    public void setAccountPassword(String password) {
        this.password = password;
    }

    public void setWork(Work work) {
        this.works.add(work);
        work.getAccounts().add(this);
    }

    public void setRole(Role role) {
        this.roles.add(role);
        role.getAccounts().add(this);
    }

    public List<MySnapShot> getMySnapShotsByIdAsc() {
        return mySnapShots
                .stream()
                .sorted(Comparator.comparingLong(MySnapShot::getId))
                .collect(Collectors.toList());
    }

    public List<MySnapShot> getMySnapShotsByIdDesc() {
    	return mySnapShots
    			.stream()
    			.sorted(Comparator.comparingLong(MySnapShot::getId).reversed())
    			.collect(Collectors.toList());
    }

    public List<MySnapShot> getMySnapShotsContainWithRenderedModelByIdAsc(String renderedModelIdsString) {
        String[] renderedModelIds = renderedModelIdsString.split(",");
        return mySnapShots
                .stream()
                .filter(m -> m.containOneMore(renderedModelIds))
                .sorted(Comparator.comparingLong(MySnapShot::getId)).collect(Collectors.toList());
    }

    public AccountDTO getAccountDTO() {
        return new AccountDTO(this);
    }

    public boolean isRegist()
    {
        return !isEdit();
    }

    public boolean isEdit()
    {
        return this.id > 0;
    }

    public String getWorksString()
    {
        return works.stream().map(t->t.getId() +"").collect(Collectors.joining(","));
    }

    public String getWorkNamesString()
    {
        return works.stream().map(t->t.getLocaleName()).collect(Collectors.joining(","));
    }

    public String getRolesString()
    {
        return roles.stream().map(t->t.getId()+"").collect(Collectors.joining(","));
    }

    public String getRoleNamesString()
    {
        return roles.stream().map(t->t.getLocaleName()).collect(Collectors.joining(","));
    }

    public String getRoleCodesString()
    {
        return roles.stream().map(t -> t.getCode().name()).collect(Collectors.joining(","));
    }

    public boolean isRoleSystemAdmin() {
        return getRoleCodesString().contains("ROLE_ADMIN_SYSTEM");
    }

    public void putPhotoPath(String photoPath) {
        this.photoPath=photoPath;
    }

    public void putAccount(AccountVO accountVO, long writerId) {
        this.company = new Company(accountVO.getCompanyId());
        this.userName = accountVO.getUserName();
        this.rank = accountVO.getRank();
        this.phoneNo = accountVO.getPhoneNo();
        this.mobileNo = accountVO.getMobileNo();
        this.address = accountVO.getAddress();
        this.enabled = accountVO.getEnabled();
        this.responsible = accountVO.isResponsible();
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
        this.employer = accountVO.isEmployer();

        this.works.clear();
        Utils
                .getLongList(accountVO.getWorks(), ",")
                .forEach(work -> this.works.add(new Work(work)));

        this.roles.clear();
        Utils
                .getLongList(accountVO.getRoles(), ",")
                .forEach(role -> this.roles.add(new Role(role)));
    }

    public void putMyAccount(AccountVO accountVO, long writerId) {

        if(accountVO.isChangePassword()) this.password = accountVO.getEncodedPassword();

        this.userName = accountVO.getUserName();
        this.rank = accountVO.getRank();
        this.phoneNo = accountVO.getPhoneNo();
        this.mobileNo = accountVO.getMobileNo();
        this.address = accountVO.getAddress();
        this.lastModifyEmbedded = new LastModifyEmbedded(writerId);
        this.jobSheetTemplateId = accountVO.getSelectedTemplate();
        this.upperMenu = accountVO.getUpperMenu();
        this.sideMenu = accountVO.getSideMenu();
        this.employer = accountVO.isEmployer();
    }

    public boolean isSameProject(long projectId)
    {
        return company.getProject().getId() == projectId;
    }

    public boolean isMe (long accountId)
    {
        return id == accountId;
    }

    public void setRocketChatId(String id) {
        this.rocketChatId = id;
    }

    public void setRocketChatToken(String token) {
        this.rocketChatToken = token;
    }

    public void setViewerSetting(String viewerSetting) { this.viewerSetting = viewerSetting;}

    // set default value of sideMenu and upperMenu
    @PrePersist
    public void prePersist(){
        this.sideMenu = this.sideMenu == null ? 0 : this.sideMenu;
        this.upperMenu = this.upperMenu == null ? 21 : this.upperMenu;
    }
}