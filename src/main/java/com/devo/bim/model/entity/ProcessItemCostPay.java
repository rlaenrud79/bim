package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.PaidCostAllDTO;
import com.devo.bim.model.dto.ProcessItemCostPayDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItemCostPay {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "processItemId")
    private ProcessItem processItem;

    private int costNo;
    private LocalDateTime costDate;
    private BigDecimal progressRate;
    private BigDecimal cost;
    private BigDecimal sumProgressRate;
    private BigDecimal sumCost;
    private String description;
    private BigDecimal taskProgressRate;
    private BigDecimal taskCost;
    @Embedded
    private WriteEmbedded writeEmbedded;
    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public int getIntPercent(String strVariable, int value){
        BigDecimal multiplyValue = new BigDecimal(value);

        if(strVariable.equalsIgnoreCase("progressRate")) return this.progressRate.multiply(multiplyValue).intValue();
        if(strVariable.equalsIgnoreCase("sumProgressRate")) return  this.sumProgressRate.multiply(multiplyValue).intValue();
        if(strVariable.equalsIgnoreCase("taskProgressRate")) return this.taskProgressRate.multiply(multiplyValue).intValue();
        return 0;
    }

    public ProcessItemCostPay(ProcessItem processItem) {
        this.processItem = processItem;
        this.costNo = 0;
        this.costDate = LocalDateTime.now();
        this.progressRate = Utils.isEmpty(null);
        this.cost = Utils.isEmpty(null);
        this.sumProgressRate = Utils.isEmpty(null);
        this.sumCost = Utils.isEmpty(null);
        this.taskProgressRate = Utils.isEmpty(null);
        this.taskCost = Utils.isEmpty(null);
        this.description = "";
    }

    public ProcessItemCostPay(ProcessItem processItem, ProcessItemCostPayDTO processItemCostPayDTO, long accountId) {
        this.processItem = processItem;
        update(processItemCostPayDTO, accountId);
        this.writeEmbedded = new WriteEmbedded(accountId);
    }

    public void update(ProcessItemCostPayDTO processItemCostPayDTO, long accountId) {
        this.costNo = processItemCostPayDTO.getCostNo();
        this.costDate = Utils.parseLocalDateTimeStart(processItemCostPayDTO.getCostDate());
        this.progressRate = new BigDecimal((processItemCostPayDTO.getProgress() / 100f)+"");
        this.cost = new BigDecimal(processItemCostPayDTO.getCost());
        this.sumProgressRate = new BigDecimal((processItemCostPayDTO.getSumProgress() / 100f)+"");
        this.sumCost = new BigDecimal(processItemCostPayDTO.getSumCost());
        this.description = processItemCostPayDTO.getDescription();
        this.taskProgressRate = new BigDecimal((processItemCostPayDTO.getTaskProgress()/ 100f)+"");
        this.taskCost = processItemCostPayDTO.getTaskCost();

        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public ProcessItemCostPay(ProcessItem processItem, Integer costNo, GisungProcessItem gisungProcessItem, long accountId) {
        this.processItem = processItem;
        update(gisungProcessItem, accountId, costNo);
        this.writeEmbedded = new WriteEmbedded(accountId);
    }

    public void update(GisungProcessItem gisungProcessItem, long accountId, Integer costNo) {
        this.costNo = costNo;
        this.costDate = LocalDateTime.now();
        this.progressRate = gisungProcessItem.getProgressRate();
        this.cost = gisungProcessItem.getCost();
        this.sumProgressRate = gisungProcessItem.getPaidProgressRate().add(gisungProcessItem.getProgressRate());
        this.sumCost = gisungProcessItem.getCost().add(gisungProcessItem.getPaidCost());
        this.description = "";
        this.taskProgressRate = new BigDecimal((100/ 100f)+"");
        this.taskCost = gisungProcessItem.getTaskCost();

        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public ProcessItemCostPayDTO getPredictedCostPayAtCurrent() {
        return createPredictedCostPayAtCurrent(this.costNo + 1,Utils.getDateTimeByNationAndFormatType(LocalDateTime.now(), DateFormatEnum.YEAR_MONTH_DAY),"");
    }

    public ProcessItemCostPayDTO getPredictedCostPayAtCurrent(PaidCostAllDTO paidCostAllDTO) {
        return createPredictedCostPayAtCurrent(paidCostAllDTO.getCostNo(),paidCostAllDTO.getCostDate(),paidCostAllDTO.getDescription());
    }

    private ProcessItemCostPayDTO createPredictedCostPayAtCurrent(int costNo, String costDate, String description)
    {
        ProcessItemCostPayDTO processItemCostPayDTO = new ProcessItemCostPayDTO();

        BigDecimal completeProgressRate = new BigDecimal("100");
        BigDecimal taskProgressRate = processItem.getProgressRate().multiply(new BigDecimal("100"));
        BigDecimal taskCost = Utils.isEmpty(processItem.getCost());
        BigDecimal sumProgressRate = this.sumProgressRate.multiply(new BigDecimal("100"));

        boolean isCompleteTask = taskProgressRate.compareTo(completeProgressRate) >= 0; // taskProgressRate >= 100

        // subtract(100) : 100 - sumProgressRate
        BigDecimal thisProgressRate = isCompleteTask ? completeProgressRate.subtract(sumProgressRate) : taskProgressRate.subtract(sumProgressRate).setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal thisCost = isCompleteTask ? taskCost.subtract(this.sumCost) : taskCost.multiply(thisProgressRate).multiply(new BigDecimal(0.01));
        BigDecimal thisSumProgressRate = isCompleteTask ? completeProgressRate : taskProgressRate;
        BigDecimal thisSumCost = isCompleteTask ? taskCost : this.sumCost.add(thisCost);

        processItemCostPayDTO.setProcessItemCostPayId(0);
        processItemCostPayDTO.setProcessItemId(this.processItem.getId());
        processItemCostPayDTO.setCostNo(costNo);
        processItemCostPayDTO.setCostDate(costDate);
        processItemCostPayDTO.setDescription(description);
        processItemCostPayDTO.setProgressRate(thisProgressRate);
        processItemCostPayDTO.setCost(thisCost.toBigInteger());
        processItemCostPayDTO.setSumProgressRate(thisSumProgressRate);
        processItemCostPayDTO.setSumCost(thisSumCost.toBigInteger());
        processItemCostPayDTO.setTaskProgressRate(taskProgressRate);

        processItemCostPayDTO.setPropertyByConverting();

        return processItemCostPayDTO;
    }
}
