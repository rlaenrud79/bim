package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class WorkName {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String languageCode;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="work_id")
    Work work;

    public WorkName(String languageCode, String name) {
        this.languageCode = languageCode;
        this.name = name;
    }

    public void setWorkId(long workId) {
        this.work = new Work(workId);
    }

    public void setNameAtUpdateWork(String languageCode, String name) {
        this.languageCode = languageCode;
        this.name = name;
    }
}
