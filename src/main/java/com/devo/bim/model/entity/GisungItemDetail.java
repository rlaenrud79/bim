package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.vo.GisungItemDetailVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungItemDetail extends ObjectModelHelper<GisungItemDetail> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private long gisungId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_item_id")
    private GisungItem gisungItem;

    private String itemDetail01;
    private String itemDetail02;
    private String itemDetail03;
    private String itemDetail04;
    private String itemDetail05;
    private String itemDetail06;
    private String netCheck;
    private String gtype;

    public void setGisungItemDetail(UserInfo userInfo, GisungItem gisungItem, GisungItemDetailVO gisungItemDetailVO) {
        this.projectId = userInfo.getProjectId();
        this.gisungItem = gisungItem;
        this.gisungId = gisungItem.getGisung().getId();
        this.itemDetail01 = gisungItemDetailVO.getItemDetail01();
        this.itemDetail02 = gisungItemDetailVO.getItemDetail02();
        this.itemDetail03 = gisungItemDetailVO.getItemDetail03();
        this.itemDetail04 = gisungItemDetailVO.getItemDetail04();
        this.itemDetail05 = gisungItemDetailVO.getItemDetail05();
        this.itemDetail06 = gisungItemDetailVO.getItemDetail06();
        this.netCheck = gisungItemDetailVO.getNetCheck();
        this.gtype = gisungItemDetailVO.getGtype();
    }
}
