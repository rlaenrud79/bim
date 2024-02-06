package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetProcessItemWorker {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="job_sheet_id")
    private JobSheet jobSheet;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="job_sheet_process_item_id")
    private JobSheetProcessItem jobSheetProcessItem;

    private long processItemId;

    private long projectId;
    private long processId;
    private long processWorkerId;
    private String workerName;
    private BigDecimal workerAmount = Utils.getDefaultDecimal();
    private BigDecimal beforeWorkerAmount = Utils.getDefaultDecimal();
    /**
     public JobSheetProcessItemWorker(JobSheetProcessItemWorker jobSheetProcessItemWorker){
     this.projectId = jobSheetProcessItemWorker.getProjectId();
     this.workerName = jobSheetProcessItemWorker.getWorkerName();
     this.workerAmount = jobSheetProcessItemWorker.getWorkerAmount();
     this.beforeWorkerAmount = jobSheetProcessItemWorker.getBeforeWorkerAmount();
     }
     **/
    public void setJobSheetProcessItemWorkerAtAddJobSheetProcessItem(JobSheet jobSheet, JobSheetProcessItem jobSheetProcessItem, long processItemId, long projectId, long processId, String workerName, BigDecimal workerAmount, BigDecimal beforeWorkerAmount, long processWorkerId) {
        this.jobSheet = jobSheet;
        this.jobSheetProcessItem = jobSheetProcessItem;
        this.processItemId = processItemId;
        this.projectId = projectId;
        this.processId = processId;
        this.workerName = workerName;
        this.workerAmount = workerAmount;
        this.beforeWorkerAmount = beforeWorkerAmount;
        this.processWorkerId = processWorkerId;
    }
    /**
     public void setJobSheetProcessItemWorkerAtAddWorkerName(long projectId, String workerName, BigDecimal workerAmount, BigDecimal beforeWorkerAmount) {
     this.projectId = projectId;
     this.workerName = workerName;
     this.workerAmount = workerAmount;
     this.beforeWorkerAmount = beforeWorkerAmount;
     }
     **/
}
