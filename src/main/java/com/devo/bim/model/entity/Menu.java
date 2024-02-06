package com.devo.bim.model.entity;

import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Menu extends ObjectModelHelper<Menu> {
    @Id
    private String code;
    private boolean enabled;
}
