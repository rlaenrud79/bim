package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BulletinVO {
    private long id;
    private String title;
    private String contents;
}
