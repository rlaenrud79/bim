package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GisungCoverVO {
    private long id;
    private long gisungId;
    private String title;
    private String subTitle;
    private String date;
    private String projectName;
}
