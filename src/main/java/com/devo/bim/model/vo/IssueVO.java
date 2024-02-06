package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IssueVO {

    private long issueId;

    private String title;
    private String contents;
    private String requestDate;
    private int priority;
    private String status;
    private List<Long> issueManagerIds = new ArrayList<>();

    private long projectId;
    private long userId;
}
