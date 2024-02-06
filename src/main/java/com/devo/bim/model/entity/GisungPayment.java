package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungPaymentVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungPayment extends ObjectModelHelper<GisungPayment> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String title;
    private String description;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "gisungPayment")
    private List<GisungPaymentFile> gisungPaymentFiles = new ArrayList<>();

    public GisungPayment(long gisungPaymentId) {
        this.id = gisungPaymentId;
    }

    public void setGisungPaymentAtAddGisungPayment(GisungPaymentVO gisungPaymentVO, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.title = gisungPaymentVO.getTitle();
        this.description = gisungPaymentVO.getDescription();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungPaymentAtUpdateGisungPayment(GisungPaymentVO gisungPaymentVO, UserInfo userInfo) {
        this.title = gisungPaymentVO.getTitle();
        this.description = gisungPaymentVO.getDescription();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
