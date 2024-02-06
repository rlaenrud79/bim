package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungItemVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungItem extends ObjectModelHelper<GisungItem> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private String item01;
    private String item02;
    private String item03;
    private String item04;
    private String item05;
    private String item06;
    private String item07;
    private String item08;
    private String item09;
    private String item10;
    private String item11;
    private String item12;
    private String item13;
    private Integer documentNo;
    private String filenameList;

    public void setGisungItemAtAddGisungItem(UserInfo userInfo, Gisung gisung, GisungItemVO gisungItemVO) {
        this.projectId = userInfo.getProjectId();
        this.gisung = gisung;
        this.title = gisungItemVO.getTitle();
        this.item01 = gisungItemVO.getItem01();
        this.item02 = gisungItemVO.getItem02();
        this.item03 = gisungItemVO.getItem03();
        this.item04 = gisungItemVO.getItem04();
        this.item05 = gisungItemVO.getItem05();
        this.item06 = gisungItemVO.getItem06();
        this.item07 = gisungItemVO.getItem07();
        this.item08 = gisungItemVO.getItem08();
        this.item09 = gisungItemVO.getItem09();
        this.item10 = gisungItemVO.getItem10();
        this.item11 = gisungItemVO.getItem11();
        this.item12 = gisungItemVO.getItem12();
        this.item13 = gisungItemVO.getItem13();
        this.documentNo = gisungItemVO.getDocumentNo();
        this.filenameList = gisungItemVO.getFilenameList();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungItemAtUpdateGisungItem(UserInfo userInfo, GisungItemVO gisungItemVO) {
        this.title = gisungItemVO.getTitle();
        this.item01 = gisungItemVO.getItem01();
        this.item02 = gisungItemVO.getItem02();
        this.item03 = gisungItemVO.getItem03();
        this.item04 = gisungItemVO.getItem04();
        this.item05 = gisungItemVO.getItem05();
        this.item06 = gisungItemVO.getItem06();
        this.item07 = gisungItemVO.getItem07();
        this.item08 = gisungItemVO.getItem08();
        this.item09 = gisungItemVO.getItem09();
        this.item10 = gisungItemVO.getItem10();
        this.item11 = gisungItemVO.getItem11();
        this.item12 = gisungItemVO.getItem12();
        this.item13 = gisungItemVO.getItem13();
        this.filenameList = gisungItemVO.getFilenameList();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
