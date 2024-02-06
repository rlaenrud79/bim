package com.devo.bim.model.vo;

import com.devo.bim.model.entity.GisungContractor;
import com.devo.bim.model.entity.GisungItemDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GisungItemVO {
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
    private List<GisungItemDetail> data;
    private Integer documentNo;
    private List<GisungContractor> contractorData;
    private String filenameList;
    private Integer gisungNo;
}
