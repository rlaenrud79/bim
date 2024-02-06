package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GisungTableVO {
    private long id;
    private long gisungId;
    private String contents;
    private long projectId;
    private List<String> contentsList;
}
