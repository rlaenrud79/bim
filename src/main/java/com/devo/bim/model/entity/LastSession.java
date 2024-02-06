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
public class LastSession {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long accountId;
    private long projectId;
    private String lastSessionId;

    public LastSession (long accountId, long projectId, String lastSessionId) {
        this.accountId = accountId;
        this.projectId = projectId;
        this.lastSessionId = lastSessionId;
    }

    public void  setLsatSession (long accountId, long projectId, String lastSessionId) {
        this.accountId = accountId;
        this.projectId = projectId;
        this.lastSessionId = lastSessionId;
    }

    public void setLastSessionId(String lastSessionId){
        this.lastSessionId = lastSessionId;
    }
}

