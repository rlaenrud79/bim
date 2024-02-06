package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.vo.GisungTableVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungTable extends ObjectModelHelper<GisungTable> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String contents;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    public void setGisungTableAtAddGisungTable(UserInfo userInfo, Gisung gisung, GisungTableVO gisungTableVO) {
        this.projectId = userInfo.getProjectId();
        this.gisung = gisung;
        this.contents = gisungTableVO.getContents();
    }

    public void setGisungTableAtUpdateGisungTable(GisungTableVO gisungTableVO) {
        this.contents = gisungTableVO.getContents();
    }
}
