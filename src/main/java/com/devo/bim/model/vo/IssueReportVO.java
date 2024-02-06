package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IssueReportVO {
    private long issueId;
    private String contents;
}
