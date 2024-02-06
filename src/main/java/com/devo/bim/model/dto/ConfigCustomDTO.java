package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Config;
import com.devo.bim.model.entity.ConfigCustom;
import com.devo.bim.model.entity.ConfigName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigCustomDTO {

    // config
    private long configId;
    private String configCode;
    private String configDefaultValue;
    private String configDescription;
    private String configScope;
    private String configDataOption;
    private String configInputType;

    // config custom
    private long id;
    private long projectId;
    private String customValue;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;

    // config name
    private String configLocaleName;

    public ConfigCustomDTO(Config config, ConfigCustom configCustom, ConfigName configName) {
        this.configId = config.getId();
        this.configCode = config.getCode();
        this.configDefaultValue = config.getDefaultValue();
        this.configDescription = config.getDescription();
        this.configScope = config.getScope();
        this.configDataOption = config.getDataOption();
        this.configInputType = config.getInputType();

        if (configCustom != null) {
            this.id = configCustom.getId();
            this.projectId = configCustom.getProjectId();
            this.customValue = configCustom.getCustomValue();
            this.writeDate = configCustom.getWriteEmbedded().getWriteDate();
            this.writerDTO = new AccountDTO(configCustom.getWriteEmbedded().getWriter());
        }

        if (configName != null) {
            this.configLocaleName = configName.getName();
        }
    }
}
