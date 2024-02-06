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
public class ProcessWorker {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="project_id")
    private Project project;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="process_id")
    private ProcessInfo processInfo;
    private String workerName;
    private BigDecimal workerAmount = Utils.getDefaultDecimal();

    public void setWorkerAmount(BigDecimal workerAmount) {
        this.workerAmount = workerAmount;
    }

    public void setProcessWorkerAtAddJobSheetProcessItemWorker(long projectId, ProcessInfo processInfo, String workerName, BigDecimal workerAmount) {
        this.project = new Project(projectId);
        this.processInfo = processInfo;
        this.workerName = workerName;
        this.workerAmount = workerAmount;
    }
}
