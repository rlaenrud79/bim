package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Gisung;
import lombok.Data;

@Data
public class GisungItemDTO {
    private long id;
    private long gisungId;
    private String title;
    private long projectId;
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
    private Integer gisungNo;

    public GisungItemDTO(long id
    , Gisung gisung
    , String title
    , long projectId
    , String item01
    , String item02
    , String item03
    , String item04
    , String item05
    , String item06
    , String item07
    , String item08
    , String item09
    , String item10
    , String item11
    , String item12
    , String item13
    , Integer documentNo
    , String filenameList) {
        this.id = id;
        this.gisungId = gisung.getId();
        this.title = title;
        this.projectId = projectId;
        this.item01 = item01;
        this.item02 = item02;
        this.item03 = item03;
        this.item04 = item04;
        this.item05 = item05;
        this.item06 = item06;
        this.item07 = item07;
        this.item08 = item08;
        this.item09 = item09;
        this.item10 = item10;
        this.item11 = item11;
        this.item12 = item12;
        this.item13 = item13;
        this.documentNo = documentNo;
        this.filenameList = filenameList;
    }
}
