package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.GisungProcessItemCostDetail;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemCostDetail;
import com.devo.bim.model.entity.VmProcessItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessItemCostDetailDTO {
    private long id;
    private String rowState;
    private String taskName;
    private String phasingCode;
    private long processItemCodeDetailId;
    private long processItemId;
    private String code;
    private String name;
    private boolean isFirst;
    private BigDecimal count;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal cost;
    private boolean isChecked = true;

    private BigDecimal jobSheetProgressCount = Utils.getDefaultDecimal();
    private long jobSheetProgressAmount;
    private BigDecimal paidProgressCount = Utils.getDefaultDecimal();
    private BigDecimal paidCost = Utils.getDefaultDecimal();
    private BigDecimal progressCount = Utils.getDefaultDecimal();
    private BigDecimal progressCost = Utils.getDefaultDecimal();
    private BigDecimal sumProgressCount;
    private BigDecimal sumProgressCost;
    private BigDecimal remindProgressCount;
    private BigDecimal remindProgressCost;

    private ProcessItemCostDetailOrigin originData;

    public ProcessItemCostDetailDTO(ProcessItemCostDetail processItemCostDetail, String rowState) {
        this.rowState = rowState; //CRUD

        this.id = processItemCostDetail.getId();
        this.processItemCodeDetailId = "C".equals(rowState) ? 0 : processItemCostDetail.getId();
        this.processItemId = "C".equals(rowState) ? 0 : processItemCostDetail.getProcessItem().getId();
        this.code = processItemCostDetail.getCode();
        this.name = processItemCostDetail.getName();
        this.isFirst = processItemCostDetail.isFirst();
        this.count = processItemCostDetail.getCount();
        this.unit = processItemCostDetail.getUnit();
        this.unitPrice = processItemCostDetail.getUnitPrice();
        this.cost = processItemCostDetail.getCost();

        this.originData = new ProcessItemCostDetailOrigin(processItemCostDetail);
    }

    public ProcessItemCostDetailDTO(ProcessItem processItem, ProcessItemCostDetail processItemCostDetail
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost) {
        this.taskName = processItemCostDetail.getProcessItem().getTaskName();
        this.phasingCode = processItemCostDetail.getProcessItem().getPhasingCode();
        this.processItemCodeDetailId = "C".equals(rowState) ? 0 : processItemCostDetail.getId();
        this.processItemId = "C".equals(rowState) ? 0 : processItemCostDetail.getProcessItem().getId();
        this.code = processItemCostDetail.getCode();
        this.name = processItemCostDetail.getName();
        this.isFirst = processItemCostDetail.isFirst();
        this.count = processItemCostDetail.getCount();
        this.unit = processItemCostDetail.getUnit();
        this.unitPrice = processItemCostDetail.getUnitPrice();
        this.cost = processItemCostDetail.getCost();
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;

        this.originData = new ProcessItemCostDetailOrigin(processItemCostDetail);
    }

    public ProcessItemCostDetailDTO(VmProcessItem vmProcessItem, ProcessItemCostDetail processItemCostDetail
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost) {
        this.taskName = processItemCostDetail.getProcessItem().getTaskName();
        this.phasingCode = processItemCostDetail.getProcessItem().getPhasingCode();
        this.processItemCodeDetailId = "C".equals(rowState) ? 0 : processItemCostDetail.getId();
        this.processItemId = "C".equals(rowState) ? 0 : processItemCostDetail.getProcessItem().getId();
        this.code = processItemCostDetail.getCode();
        this.name = processItemCostDetail.getName();
        this.isFirst = processItemCostDetail.isFirst();
        this.count = processItemCostDetail.getCount();
        this.unit = processItemCostDetail.getUnit();
        this.unitPrice = processItemCostDetail.getUnitPrice();
        this.cost = processItemCostDetail.getCost();
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;

        this.originData = new ProcessItemCostDetailOrigin(processItemCostDetail);
    }

    public ProcessItemCostDetailDTO(ProcessItemCostDetail processItemCostDetail){
        this.taskName = processItemCostDetail.getProcessItem().getTaskName();
        this.phasingCode = processItemCostDetail.getProcessItem().getPhasingCode();
        this.processItemCodeDetailId = "C".equals(rowState) ? 0 : processItemCostDetail.getId();
        this.processItemId = "C".equals(rowState) ? 0 : processItemCostDetail.getProcessItem().getId();
        this.code = processItemCostDetail.getCode();
        this.name = processItemCostDetail.getName();
        this.isFirst = processItemCostDetail.isFirst();
        this.count = processItemCostDetail.getCount();
        this.unit = processItemCostDetail.getUnit();
        this.unitPrice = processItemCostDetail.getUnitPrice();
        this.cost = processItemCostDetail.getCost();

        this.originData = new ProcessItemCostDetailOrigin(processItemCostDetail);
    }

    private class ProcessItemCostDetailOrigin {
        private String code;
        private String name;
        private boolean isFirst;
        private BigDecimal count;
        private String unit;
        private BigDecimal unitPrice;

        public ProcessItemCostDetailOrigin(ProcessItemCostDetail processItemCostDetail) {
            this.code = processItemCostDetail.getCode();
            this.name = processItemCostDetail.getName();
            this.isFirst = processItemCostDetail.isFirst();
            this.count = processItemCostDetail.getCount();
            this.unit = processItemCostDetail.getUnit();
            this.unitPrice = processItemCostDetail.getUnitPrice();
        }
    }

    public ProcessItemCostDetailDTO(String taskName, String phasingCode, ProcessItemCostDetail processItemCostDetail
            , BigDecimal progressCount
            , BigDecimal progressCost) {
        this.taskName = taskName;
        this.phasingCode = phasingCode;
        this.processItemCodeDetailId = "C".equals(rowState) ? 0 : processItemCostDetail.getId();
        this.processItemId = "C".equals(rowState) ? 0 : processItemCostDetail.getProcessItem().getId();
        this.code = processItemCostDetail.getCode();
        this.name = processItemCostDetail.getName();
        this.isFirst = processItemCostDetail.isFirst();
        this.count = processItemCostDetail.getCount();
        this.unit = processItemCostDetail.getUnit();
        this.unitPrice = processItemCostDetail.getUnitPrice();
        this.cost = processItemCostDetail.getCost();
        this.progressCount = progressCount;
        this.progressCost = progressCost;

        this.originData = new ProcessItemCostDetailOrigin(processItemCostDetail);
    }

    public ProcessItemCostDetailDTO(long id
            , String taskName
            , String phasingCode
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal sumProgressCount
            , BigDecimal sumProgressCost
            , BigDecimal remindProgressCount
            , BigDecimal remindProgressCost
            , long processItemId) {
        this.id = id;
        this.taskName = taskName;
        this.phasingCode = phasingCode;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.sumProgressCount = sumProgressCount;
        this.sumProgressCost = sumProgressCost;
        this.remindProgressCount = remindProgressCount;
        this.remindProgressCost = remindProgressCost;
        this.processItemId = processItemId;
    }

    public ProcessItemCostDetailDTO(long id
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal sumProgressCount
            , BigDecimal sumProgressCost
            , BigDecimal remindProgressCount
            , BigDecimal remindProgressCost) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.sumProgressCount = sumProgressCount;
        this.sumProgressCost = sumProgressCost;
        this.remindProgressCount = remindProgressCount;
        this.remindProgressCost = remindProgressCost;
    }

    public ProcessItemCostDetailDTO(long id
            , String code
            , String name
            , BigDecimal count
            , String unit
            , BigDecimal unitPrice
            , BigDecimal cost
            , boolean isFirst
            , BigDecimal jobSheetProgressCount
            , long jobSheetProgressAmount
            , BigDecimal paidProgressCount
            , BigDecimal paidCost
            , BigDecimal progressCount
            , BigDecimal progressCost
            , BigDecimal sumProgressCount
            , BigDecimal sumProgressCost
            , BigDecimal remindProgressCount
            , BigDecimal remindProgressCost) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.cost = cost;
        this.isFirst = isFirst;
        this.jobSheetProgressCount = jobSheetProgressCount;
        this.jobSheetProgressAmount = jobSheetProgressAmount;
        this.paidProgressCount = paidProgressCount;
        this.paidCost = paidCost;
        this.progressCount = progressCount;
        this.progressCost = progressCost;
        this.sumProgressCount = sumProgressCount;
        this.sumProgressCost = sumProgressCost;
        this.remindProgressCount = remindProgressCount;
        this.remindProgressCost = remindProgressCost;
    }

    public boolean isNew() {
        return "C".equals(rowState);
    }

    public boolean isUpdate() {
        return "U".equals(rowState);
    }


}
