package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSheetTemplateVO {

    private long id;
    private String title;
    private String contents;
    private boolean enable;
}
