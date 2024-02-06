package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.vo.GisungCoverVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungCover extends ObjectModelHelper<GisungCover> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private String subTitle;
    private String date;
    private String projectName;

    public void setGisungCoverAtAddGisungCover(UserInfo userInfo, Gisung gisung, GisungCoverVO gisungCoverVO) {
        this.projectId = userInfo.getProjectId();
        this.gisung = gisung;
        this.title = gisungCoverVO.getTitle();
        this.subTitle = gisungCoverVO.getSubTitle();
        this.date = gisungCoverVO.getDate();
        this.projectName = gisungCoverVO.getProjectName();
    }

    public void setGisungCoverAtUpdateGisungCover(UserInfo userInfo, GisungCoverVO gisungCoverVO) {
        this.title = gisungCoverVO.getTitle();
        this.subTitle = gisungCoverVO.getSubTitle();
        this.date = gisungCoverVO.getDate();
        this.projectName = gisungCoverVO.getProjectName();
    }
}
