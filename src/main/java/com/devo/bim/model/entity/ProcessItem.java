package com.devo.bim.model.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.devo.bim.model.vo.ProcessItemVO;
import org.apache.commons.lang3.StringUtils;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.ProcessItemCostDTO;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "process_id")
    private ProcessInfo processInfo;

    private String taskName;
    private int taskDepth;
    private String phasingCode;

    private int duration;
    private String plannedStartDate;
    private String plannedEndDate;
    private String startDate;
    private String endDate;

    private String description;

    @Enumerated(STRING)
    private ProcessValidateResult validate;

    private boolean includeHoliday = true;
    private boolean progressTarget;
    private BigDecimal progressRate;

    @Enumerated(STRING)
    private TaskType ganttTaskType;
    private boolean ganttOpen;
    private String ganttHolder;
    private int ganttSortNo;

    @OneToMany(fetch = LAZY, mappedBy = "processItem")
    private List<ProcessItemLink> processItemLinks = new ArrayList<>();

    private int rowNum;
    private int parentRowNum;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private String taskFullPath = "";

    private Long parentId = 0L;

    @Enumerated(STRING)
    private TaskStatus taskStatus;

    private String firstCountFormula = "";
    private BigDecimal firstCount = Utils.getDefaultDecimal();
    private String firstCountUnit = "";
    private BigDecimal complexUnitPrice = Utils.getDefaultDecimal();
    private BigDecimal cost = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal paidProgressRate = Utils.getDefaultDecimal();
    private BigDecimal prevPaidCost = Utils.getDefaultDecimal();
    private BigDecimal prevPaidProgressRate = Utils.getDefaultDecimal();
    private boolean isBookmark;
    private Long costWriterId;
    private LocalDateTime costWriteDate;
    private Long costLastModifierId;
    private LocalDateTime costLastModifyDate;
    private String exchangeIds="";
    private Long progressAmount = 0L;

    /**
    @OneToOne(fetch = LAZY)
    @JoinColumns({
            @JoinColumn(name = "cate1"),
            @JoinColumn(name = "cate2"),
            @JoinColumn(name = "cate3"),
            @JoinColumn(name = "cate4"),
            @JoinColumn(name = "cate5"),
            @JoinColumn(name = "cate6"),
            @JoinColumn(name = "cate7")
    })
    private ProcessCategory processCategory;
     **/
    private String cate1;
    private String cate2;
    private String cate3;
    private String cate4;
    private String cate5;
    private String cate6;


    @OneToMany(fetch = LAZY, mappedBy = "processItem")
    private List<ProcessItemCostDetail> ProcessItemCostDetails = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "processItem")
    private List<ProcessItemCostPay> ProcessItemCostPays = new ArrayList<>();

    public ProcessItem(long id) {
        this.id = id;
    }

    public ProcessItem(long processInfoId, Project project) {
        this.processInfo = new ProcessInfo(processInfoId);
        this.taskName = project.getName().trim();
        this.taskDepth = 0;
        this.phasingCode = "";
        this.duration = 0;

        if (project.getStartDate() == null) {
            this.plannedStartDate = "";
            this.startDate = "";
        } else {
            this.plannedStartDate = project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.startDate = project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (project.getEndDate() == null) {
            this.plannedEndDate = "";
            this.endDate = "";
        } else {
            this.plannedEndDate = project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.endDate = project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        this.description = "";
        this.validate = ProcessValidateResult.NONE;
        this.includeHoliday = true;
        this.progressTarget = true;
        this.progressRate = Utils.getDefaultDecimal();
        this.ganttTaskType = TaskType.PROJECT;
        this.ganttOpen = true;
        this.ganttHolder = "";
        this.ganttSortNo = 0;
        this.rowNum = 0;
        this.parentRowNum = -1;
        this.work = null;
        this.taskFullPath = project.getName();
        this.taskStatus = TaskStatus.REG;
        this.progressAmount = 0L;
    }

    public void setAddDataAtPostGanttDataForTask(long processId, int taskDepth, int ganttSortNo, String taskFullPath) {
        this.processInfo = new ProcessInfo(processId);
        this.taskDepth = taskDepth;
        this.ganttSortNo = ganttSortNo;
        this.taskFullPath = taskFullPath;
        this.ganttTaskType = (this.ganttTaskType == TaskType.WORK) ? TaskType.PROJECT : this.ganttTaskType;
    }

    public void setTaskDepth(int taskDepth) {
        this.taskDepth = taskDepth;
    }

    public void setGanttTaskType(TaskType project) {

        if (!StringUtils.isEmpty(this.phasingCode)) {
            this.ganttTaskType = TaskType.TASK;
            return;
        }
        if (StringUtils.isEmpty(this.phasingCode) && StringUtils.isEmpty(this.startDate)) {
            this.ganttTaskType = TaskType.PROJECT;
            return;
        }
        if (StringUtils.isEmpty(this.phasingCode) && !StringUtils.isEmpty(this.startDate) && this.duration == 0) {
            this.ganttTaskType = TaskType.MILESTONE;
            return;
        }
        this.ganttTaskType = TaskType.TASK;
    }

    public void setProcessItemId(long id) {
        this.id = id;
    }

    public void setWorkIdAndTaskNameAtPostProcessItem(Long workId, String taskName, ProcessItem parentProcessItem) {
        this.work = (workId == null) ? null : new Work(workId);
        this.taskName = taskName;
        this.parentId = parentProcessItem.getId();
    }

    public void setProcessItemDataAtUpdateTaskAtPutGanttData(ProcessItem processItem, boolean isAutoSchedule) {
        if (processItem.getId() > 0) id = processItem.getId();
        if (processItem.processInfo.getId() != processInfo.getId() && processItem.processInfo.getId() > 0) {
            this.processInfo = new ProcessInfo(processItem.getProcessInfo().getId());
        }
        this.taskName = processItem.getTaskName();
        this.phasingCode = processItem.getPhasingCode();
        this.duration = processItem.getDuration();
        this.startDate = Utils.parseLocalDateTimeStart(processItem.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.endDate = Utils.parseLocalDateTimeStart(processItem.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (StringUtils.isEmpty(processItem.getDescription())) this.description = processItem.getDescription();
        //this.validate = ProcessValidateResult.NONE;
        this.progressRate = processItem.getProgressRate();
        this.ganttTaskType = (processItem.getGanttTaskType() == TaskType.WORK) ? TaskType.PROJECT : processItem.getGanttTaskType();
        this.ganttOpen = processItem.isGanttOpen();
        if (StringUtils.isEmpty(processItem.ganttHolder)) this.ganttHolder = processItem.getGanttHolder();
        if (!isAutoSchedule) this.parentId = processItem.getParentId();
        if (!isAutoSchedule) this.taskFullPath = processItem.taskFullPath;
        // this.progressAmount = processItem.progressAmount;
        if (processItem.getProgressAmount() >= 0) {
            this.progressAmount = processItem.getProgressAmount();
            this.progressRate = processItem.getProgressRate();
        } else {
            this.progressAmount = 0L;
            this.progressRate = Utils.getDefaultDecimal();
        }
    }

    public void setGanttSortNoForUpdateTaskAtPutGanttData(int ganttSortNo, int newTaskDepth, String newTaskFullPath) {
        this.ganttSortNo = ganttSortNo;
        this.taskDepth = newTaskDepth;
        this.taskFullPath = newTaskFullPath;
    }

    public void setTaskStatusDELAtUpdateTaskStatusAtDeleteGanttData() {
        taskStatus = TaskStatus.DEL;
    }

    public ProcessItem updateComplexUnitPrice(BigDecimal complexUnitPrice, long accountId) {
        this.complexUnitPrice = complexUnitPrice;
        this.costLastModifierId = accountId;
        this.costLastModifyDate = LocalDateTime.now();
        this.cost = complexUnitPrice.multiply(this.firstCount).setScale(0, RoundingMode.DOWN);
        return this;
    }

    public ProcessItem updateFirstCountFormula(ProcessItemCostDTO processItemCostDTO, long accountId) {
        this.firstCountFormula = processItemCostDTO.getFirstCountFormula();
        this.firstCount = processItemCostDTO.getFirstCount();
        updateTaskCost(processItemCostDTO, accountId);
        return this;
    }

    private void setCostLastModify(long accountId) {
        this.costLastModifierId = accountId;
        this.costLastModifyDate = LocalDateTime.now();
    }

    private void setCostByCalculation() {
        this.cost = complexUnitPrice.multiply(this.firstCount).setScale(0, RoundingMode.DOWN);
    }

    public void updateFirstCountUnit(ProcessItemCostDTO processItemCostDTO, long accountId) {
        this.firstCountUnit = processItemCostDTO.getFirstCountUnit();
        setCostLastModify(accountId);
    }

    public void updateComplexUnitPrice(ProcessItemCostDTO processItemCostDTO, long accountId) {
        this.complexUnitPrice = processItemCostDTO.getComplexUnitPrice();
        updateTaskCost(processItemCostDTO, accountId);
    }

    private void updateTaskCost(ProcessItemCostDTO processItemCostDTO, long accountId){
        setCostByCalculation();
        setCostLastModify(accountId);
        processItemCostDTO.setCalculateCostResult("[성공]");
        processItemCostDTO.setTaskCost(this.getCost().toBigInteger());
    }

    public String getTitle() {
        return this.work.getWorkName() + " - " + this.taskName + (StringUtils.isEmpty(this.phasingCode) ? "" : " [" + this.phasingCode + "]");
    }

    public boolean setProcessItemBookmark(boolean isBookmark) {
        this.isBookmark = isBookmark;
        return isBookmark;
    }

    public void setProcessItemValidate(List<ModelingAttribute> modelingAttributes) {

        List<ModelingAttribute> list = modelingAttributes.stream().filter(s -> s.getAttributeValue().equalsIgnoreCase(this.phasingCode)).collect(Collectors.toList());

        if(list.size() > 0) {
            this.validate = ProcessValidateResult.SUCCESS;
            this.exchangeIds = list.stream()
                    .filter(t->t.getModelingAssembly().getExchangeId() != null && !t.getModelingAssembly().getExchangeId().isBlank())
                    .map(o -> o.getModelingAssembly().getExchangeId())
                    .collect(Collectors.joining("|,|"));
        }
        else this.validate = ProcessValidateResult.FAIL;
    }

    public void updateCost(BigDecimal sumCost, BigDecimal sumProgressRate, BigDecimal paidCost, BigDecimal paidProgressRate, long accountId) {
        this.paidCost = sumCost;
        this.paidProgressRate = sumProgressRate;
        this.prevPaidCost = paidCost;
        this.prevPaidProgressRate = paidProgressRate;

        if (costWriteDate == null) {
            this.costWriterId = accountId;
            this.costWriteDate = LocalDateTime.now();
        }

        this.costLastModifierId = accountId;
        this.costLastModifyDate = LocalDateTime.now();
    }

    public void setTaskFullPathAtUpdateTaskAtPutGanttData(String[] arrayTaskFullPath) {
        for (int idx = 0; idx < arrayTaskFullPath.length; idx++) {
            this.taskFullPath += arrayTaskFullPath[idx];
            if (idx < arrayTaskFullPath.length - 1) this.taskFullPath += " > ";
        }
    }

    public void setProgressRate(BigDecimal progressRate) {
        this.progressRate = progressRate;
    }
    public void setProgressAmount(Long progressAmount) {
        this.progressAmount = progressAmount;
    }

    public void setFirstCount(BigDecimal firstCount ){ this.firstCount = firstCount;}

    public void setProcessItemDataAtUpdateTaskAtPutData(ProcessItemVO processItemVO) {
        //if (processItem.getId() > 0) id = processItem.getId();
        //if (processItem.processInfo.getId() != processInfo.getId() && processItem.processInfo.getId() > 0) {
        //    this.processInfo = new ProcessInfo(processItem.getProcessInfo().getId());
        //}
        this.taskName = processItemVO.getTaskName();
        this.phasingCode = processItemVO.getPhasingCode();
        this.duration = processItemVO.getDuration();
        if (!StringUtils.isEmpty(processItemVO.getStartDate())) this.startDate = processItemVO.getStartDate(); //Utils.parseLocalDateTimeStart(processItemVO.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!StringUtils.isEmpty(processItemVO.getEndDate())) this.endDate = processItemVO.getEndDate(); //Utils.parseLocalDateTimeStart(processItemVO.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!StringUtils.isEmpty(processItemVO.getPlannedStartDate())) this.plannedStartDate = processItemVO.getPlannedStartDate();
        if (!StringUtils.isEmpty(processItemVO.getPlannedEndDate())) this.plannedEndDate = processItemVO.getPlannedEndDate();
        if (!StringUtils.isEmpty(processItemVO.getDescription())) this.description = processItemVO.getDescription();
        //this.validate = ProcessValidateResult.NONE;
        this.ganttTaskType = processItemVO.getGanttTaskType();
        this.ganttOpen = false;
        this.parentRowNum = 0;
        this.parentId = processItemVO.getParentId();
        this.taskDepth = processItemVO.getTaskDepth();
        this.taskFullPath = processItemVO.getTaskFullPath();
        this.firstCountFormula = processItemVO.getFirstCountFormula();
        this.firstCount = processItemVO.getFirstCount();
        this.progressRate = Utils.getDefaultDecimal();
        this.work = new Work(processItemVO.getWorkId());
        this.cate1 = processItemVO.getCate1();
        this.cate2 = processItemVO.getCate2();
        this.cate3 = processItemVO.getCate3();
        this.cate4 = processItemVO.getCate4();
        this.cate5 = processItemVO.getCate5();
        this.cate6 = processItemVO.getCate6();
    }

    public void setProcessItemDataAtUpdateAtPutData(ProcessItemVO processItemVO) {
        //if (processItem.getId() > 0) id = processItem.getId();
        //if (processItem.processInfo.getId() != processInfo.getId() && processItem.processInfo.getId() > 0) {
        //    this.processInfo = new ProcessInfo(processItem.getProcessInfo().getId());
        //}
        this.taskName = processItemVO.getTaskName();
        this.phasingCode = processItemVO.getPhasingCode();
        this.duration = processItemVO.getDuration();
        if (!StringUtils.isEmpty(processItemVO.getStartDate())) this.startDate = processItemVO.getStartDate(); //Utils.parseLocalDateTimeStart(processItemVO.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!StringUtils.isEmpty(processItemVO.getEndDate())) this.endDate = processItemVO.getEndDate(); //Utils.parseLocalDateTimeStart(processItemVO.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!StringUtils.isEmpty(processItemVO.getPlannedStartDate())) this.plannedStartDate = processItemVO.getPlannedStartDate();
        if (!StringUtils.isEmpty(processItemVO.getPlannedEndDate())) this.plannedEndDate = processItemVO.getPlannedEndDate();
        if (!StringUtils.isEmpty(processItemVO.getDescription())) this.description = processItemVO.getDescription();
        this.taskFullPath = processItemVO.getTaskFullPath();
        this.firstCountFormula = processItemVO.getFirstCountFormula();
        this.firstCount = processItemVO.getFirstCount();
        this.work = new Work(processItemVO.getWorkId());
    }

}
