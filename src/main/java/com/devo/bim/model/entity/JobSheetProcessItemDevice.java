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
public class JobSheetProcessItemDevice {
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
    private long processDeviceId;
    private String deviceName;
    private BigDecimal deviceAmount = Utils.getDefaultDecimal();
    private BigDecimal beforeDeviceAmount = Utils.getDefaultDecimal();

    public void setJobSheetProcessItemDeviceAtAddJobSheetProcessItem(JobSheet jobSheet, JobSheetProcessItem jobSheetProcessItem, long processItemId, long projectId, long processId, String deviceName, BigDecimal deviceAmount, BigDecimal beforeDeviceAmount, long processDeviceId) {
        this.jobSheet = jobSheet;
        this.jobSheetProcessItem = jobSheetProcessItem;
        this.processItemId = processItemId;
        this.projectId = projectId;
        this.processId = processId;
        this.deviceName = deviceName;
        this.deviceAmount = deviceAmount;
        this.beforeDeviceAmount = beforeDeviceAmount;
        this.processDeviceId = processDeviceId;
    }
}
