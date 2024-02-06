package com.devo.bim.model.dto;

import com.devo.bim.model.entity.GisungCover;
import lombok.Data;

@Data
public class GisungCoverDTO {
    private long id;
    private long gisungId;
    private long projectId;
    private String title;
    private String subTitle;
    private String date;
    private String projectName;

    public GisungCoverDTO(GisungCover gisungCover) {
        this.id = gisungCover.getId();
        this.gisungId = gisungCover.getGisung().getId();
        this.title = gisungCover.getTitle();
        this.projectId = gisungCover.getProjectId();
        this.subTitle = gisungCover.getSubTitle();
        this.date = gisungCover.getDate();
        this.projectName = gisungCover.getProjectName();
    }
}
