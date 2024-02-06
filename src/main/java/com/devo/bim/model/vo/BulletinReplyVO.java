package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BulletinReplyVO {
    private long id;
    private long bulletinId;
    private String contents;
}
