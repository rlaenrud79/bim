package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SearchStatisticsUserVO {
    private long workId;
    private long accessorId;
    private String startDate = Utils.todayString();
    private String endDate = Utils.todayString();
}
