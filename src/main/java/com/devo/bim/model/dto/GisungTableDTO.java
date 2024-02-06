package com.devo.bim.model.dto;

import com.devo.bim.model.entity.GisungCover;
import com.devo.bim.model.entity.GisungTable;
import lombok.Data;

@Data
public class GisungTableDTO {
    private long id;
    private long gisungId;
    private long projectId;
    private String contents;

    public GisungTableDTO(GisungTable gisungTable) {
        this.id = gisungTable.getId();
        this.gisungId = gisungTable.getGisung().getId();
        this.projectId = gisungTable.getProjectId();
        this.contents = gisungTable.getContents();
    }
}
