package com.devo.bim.model.vo;

import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemLink;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TaskPostPutVO {
    private String entity;
    private ProcessItem processItem;
    private ProcessItemLink processItemLink;
    private long id;
    private int localIndex;
}
