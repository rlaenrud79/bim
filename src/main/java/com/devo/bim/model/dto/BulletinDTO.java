package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Account;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BulletinDTO {
    private long id;
    private long projectId;
    private String title;
    private int viewCount;
    private LocalDateTime writeDate;
    private AccountDTO writerDTO;
    private long replyCount;
    private long likesCount;

    public BulletinDTO(long id
            , long projectId
            , String title
            , int viewCount
            , LocalDateTime writeDate
            , Account writer
            , long replyCount
            , long likesCount) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.viewCount = viewCount;
        this.writeDate = writeDate;
        this.writerDTO = new AccountDTO(writer);
        this.replyCount = replyCount;
        this.likesCount = likesCount;
    }
}
