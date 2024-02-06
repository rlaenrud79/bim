package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NoticeVO {
    private long noticeId;

    private String title;
    private String contents;

    private boolean isPopup;
    private String startDate;
    private String endDate;

    private List<Long> noticeWorkIds = new ArrayList<>();

    private long projectId;
    private long userId;
}
