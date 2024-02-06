package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungContractorVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungContractor extends ObjectModelHelper<Gisung> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private String company;
    private String staff;
    private String name;
    private String company2;
    private String name2;
    private Integer documentNo;
    private String stampPath;
    private String stampPath2;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public void setGisungContractor(UserInfo userInfo, Gisung gisung, GisungContractorVO gisungContractorVO) {
        this.gisung = gisung;
        this.company = gisungContractorVO.getCompany();
        this.staff = gisungContractorVO.getStaff();
        this.name = gisungContractorVO.getName();
        this.documentNo = gisungContractorVO.getDocumentNo();
        this.stampPath = gisungContractorVO.getStampPath();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungContractor2(UserInfo userInfo, Gisung gisung, GisungContractorVO gisungContractorVO) {
        this.gisung = gisung;
        this.company = gisungContractorVO.getCompany();
        this.name = gisungContractorVO.getName();
        this.stampPath = gisungContractorVO.getStampPath();
        this.company2 = gisungContractorVO.getCompany2();
        this.name2 = gisungContractorVO.getName2();
        this.stampPath2 = gisungContractorVO.getStampPath2();
        this.documentNo = gisungContractorVO.getDocumentNo();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }
}
