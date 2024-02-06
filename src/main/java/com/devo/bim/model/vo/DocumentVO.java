package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DocumentVO {
    private long id;
    private String title;
    private long workId;
    private String description;
    private long documentCategoryId;
}
