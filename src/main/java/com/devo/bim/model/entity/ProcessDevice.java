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
public class ProcessDevice {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="process_id")
    private ProcessInfo processInfo;
    private String deviceName;
    private BigDecimal deviceAmount = Utils.getDefaultDecimal();

    public void setDeviceAmount(BigDecimal deviceAmount) {
        this.deviceAmount = deviceAmount;
    }

    public void setProcessDeivceAtAddJobSheetProcessItemDevice(long projectId, ProcessInfo processInfo, String deviceName, BigDecimal deviceAmount) {
        this.project = new Project(projectId);
        this.processInfo = processInfo;
        this.deviceName = deviceName;
        this.deviceAmount = deviceAmount;
    }
}
