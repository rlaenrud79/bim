package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItemCategory {
    @Id
    private String code;

    private String name;
    private String upCode;
    private long display;
    private String lang;
    private boolean enabled;

    public void setProcessItemCategory(String code, String name, String upCode, long display, String lang) {
        this.code = code;
        this.name = name;
        this.upCode = upCode;
        this.display = display;
        this.lang = lang;
        this.enabled = true;
    }

    public void setProcessItemCategory(String name) {
        this.name = name;
    }

    public void setProcessItemCategoryUpdateEnabeld(boolean enabled) {
        this.enabled = enabled;
    }
}
