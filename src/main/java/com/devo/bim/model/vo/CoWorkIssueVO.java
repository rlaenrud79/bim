package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CoWorkIssueVO {

    private long id;
    private long coWorkId;
    private String title;
    private String joinerIds;
    private String requestDate;
    private String priority;
    private String status;
    private String contents;
}
