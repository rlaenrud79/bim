package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungContractManagerVO;
import com.devo.bim.model.vo.GisungTableVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungContractManager extends ObjectModelHelper<Gisung> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private long projectId;
    private String company;
    private String damName;
    private String stampPath;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public void putStampPath(String stampPath) {
        this.stampPath=stampPath;
    }

    public void setGisungContractManager(UserInfo userInfo, GisungContractManagerVO gisungContractManagerVO) {
        this.projectId = userInfo.getProjectId();
        this.company = gisungContractManagerVO.getCompany();
        this.damName = gisungContractManagerVO.getDamName();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungContractManagerAtUpdateGisungContractManager(UserInfo userInfo, GisungContractManagerVO gisungContractManagerVO) {
        this.company = gisungContractManagerVO.getCompany();
        this.damName = gisungContractManagerVO.getDamName();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
