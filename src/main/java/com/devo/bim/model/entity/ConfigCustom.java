package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.ConfigCustomVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ConfigCustom {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "configId")
    private Config config;

    private String customValue;
    private long projectId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Transient
    private String source = "CUSTOM";

    public ConfigCustom(Config config){
        this.config = config;
        this.customValue = config.getDefaultValue();
        this.source = "BASE";
    }

    public void setConfigCustomAtAddConfigCustom(UserInfo userInfo, ConfigCustomVO configCustomVO) {
        this.config = new Config(configCustomVO.getConfigId());
        this.customValue = configCustomVO.getCustomValue();
        this.projectId = userInfo.getProjectId();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setConfigCustomAtUpdateConfigCustom(ConfigCustomVO configCustomVO) {
        this.customValue = configCustomVO.getCustomValue();
    }
}
