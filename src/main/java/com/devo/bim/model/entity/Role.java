package com.devo.bim.model.entity;

import com.devo.bim.model.enumulator.RoleCode;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @Enumerated(STRING)
    private RoleCode code;

    @OneToMany(fetch = LAZY, mappedBy = "role")
    private List<RoleName> roleNames = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="account_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts = new ArrayList<>();

    public String getLocaleName(){
        return roleNames.stream().filter(t -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(t.getLanguageCode())).findFirst().orElseGet(RoleName::new).getName();
    }

    public Role(long id){
        this.id = id;
    }

    public boolean isAdminSystem(){
        return code == RoleCode.ROLE_ADMIN_SYSTEM;
    }
}
