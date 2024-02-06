package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.TaskDTO;
import com.devo.bim.model.enumulator.TaskType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;

@Data
@NoArgsConstructor
public class ProcessItemVO {
    private long id;
    private long processId;
    private String taskName;
    private String phasingCode;
    private int taskDepth;
    private long parentId;
    private String startDate;
    private String endDate;
    private String plannedStartDate;
    private String plannedEndDate;
    private String description;
    private String taskFullPath = "";
    private int duration;
    private String firstCountFormula = "";
    private BigDecimal firstCount = Utils.getDefaultDecimal();
    private String firstCountUnit = "";
    private long workId;
    private String cate1 = "";
    private String cate2 = "";
    private String cate3 = "";
    private String cate4 = "";
    private String cate5 = "";
    private String cate6 = "";
    @Enumerated(STRING)
    private TaskType ganttTaskType;
    private String code = "";
    private List<String> olist = new ArrayList();
    private List<Long> processItemCostDetailIds = new ArrayList();
}
