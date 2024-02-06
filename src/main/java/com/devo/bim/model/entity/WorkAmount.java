package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.WorkAmountVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class WorkAmount extends ObjectModelHelper<WorkAmount> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private String year;
    private long amount;
    private long prevAmount;
    private long totalAmount;

    //public WorkAmount(long workAmountId){
    //    this.id = workAmountId;
    //}

    public void setWorkAmountAtAddWorkAmount(WorkAmountVO workAmountVO, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.work = new Work(workAmountVO.getWorkId());
        this.year = workAmountVO.getYear();
        this.totalAmount = workAmountVO.getTotalAmount();
        this.prevAmount = workAmountVO.getPrevAmount();
        this.amount = workAmountVO.getAmount();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setWorkAmountAtUpdateWorkAmount(WorkAmountVO workAmountVO, UserInfo userInfo) {
        this.work = new Work(workAmountVO.getWorkId());
        this.year = workAmountVO.getYear();
        this.totalAmount = workAmountVO.getTotalAmount();
        this.prevAmount = workAmountVO.getPrevAmount();
        this.amount = workAmountVO.getAmount();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
