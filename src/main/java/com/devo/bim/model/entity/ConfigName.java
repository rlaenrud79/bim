package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ConfigName {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String languageCode;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "config_id")
    Config config;

}
