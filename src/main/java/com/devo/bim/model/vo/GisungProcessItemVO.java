package com.devo.bim.model.vo;

import com.devo.bim.model.entity.GisungProcessItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GisungProcessItemVO {
    private long gisungId;
    private String status;
    private List<GisungProcessItem> data;
}
