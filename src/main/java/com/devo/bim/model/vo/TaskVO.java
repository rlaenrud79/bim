package com.devo.bim.model.vo;

import com.devo.bim.model.entity.ProcessItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TaskVO {
    private long newProcessInfoId;
    private List<ProcessItem> processItems = new ArrayList<>();
    private String description;
}
