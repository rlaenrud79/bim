package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class RoleName{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String languageCode;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="role_id")
    Role role;

    public void setRoleNameAtSearchDTO(long id, String languageCode, String name, long roleId) {
        this.id = id;
        this.languageCode = languageCode;
        this.name = name;

        this.role = new Role(roleId);
    }
}
