package com.devo.bim.model.vo;

import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemLink;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProcessFileVO {
    private String fileName;
    private List<ProcessItem> processItems = new ArrayList<>();
    private List<ProcessItemLink> processItemLinks = new ArrayList<>();
}

