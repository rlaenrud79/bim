package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class LanguageModel extends ObjectModelHelper<LanguageModel> {

    @Resource
    UserInfo userInfo;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String languageCode;
    private String name;

    public boolean pickMe(){
        return LocaleContextHolder.getLocale().getLanguage().equals(languageCode);
    }
}
