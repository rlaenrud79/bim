package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ConfigGroup {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String code;
    private int sortNo;
    private String description;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @OneToMany(fetch = EAGER, mappedBy = "configGroup")
    private List<Config> configs = new ArrayList<>();

    public ConfigGroup(String code, int sortNo, long writerId){
        this.code = code;
        this.sortNo = sortNo;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }
}
