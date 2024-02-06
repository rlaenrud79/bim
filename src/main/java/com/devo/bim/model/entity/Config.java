package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.ScheduleVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Config {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "configGroupId")
    private ConfigGroup configGroup;

    private String code;
    private String defaultValue;

    @Embedded
    private WriteEmbedded writeEmbedded;

    private int sortNo;
    private boolean enabled;

    private String inputType;
    private String description;
    private String scope;
    private String dataOption;

    @OneToMany(fetch = LAZY, mappedBy = "config")
    private List<ConfigName> configNames = new ArrayList<>();

    @OneToMany(fetch = EAGER, mappedBy = "config")
    private List<ConfigCustom> configCustoms = new ArrayList<>();

    public Config(long id) {
        this.id = id;
    }

    public String getLocaleName(){
        String localeName = configNames.stream()
                .filter(t -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(ConfigName::new)
                .getName();

        return StringUtils.isEmpty(localeName) ? this.code : localeName;
    }

    public String getScheduleColor() {
        if (this.code.equalsIgnoreCase("HOLIDAY")) return "#dc3545";
        if (this.code.equalsIgnoreCase("WORK")) return "#28a745";
        if (this.code.equalsIgnoreCase("OUTSIDE"))return "#ffc107";
        if (this.code.equalsIgnoreCase("ANNIVERSARY")) return "#b2b3b5";
        if (this.code.equalsIgnoreCase("DAYOFF"))return "#6610f2";
        if (this.code.equalsIgnoreCase("EVENT")) return"#fd7e14";
        return "#6c757d";
    }
}
