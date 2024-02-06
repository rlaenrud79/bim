package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.WorkPlanVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class WorkPlan {
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
    private String month;
    private BigDecimal monthRate = BigDecimal.ZERO;
    private double dayRate;

    public WorkPlan(long workPlanId){
        this.id = workPlanId;
    }

    public void setWorkPlanAtAddWorkPlan(WorkPlanVO workPlanVO, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.work = new Work(workPlanVO.getWorkId());
        this.year = workPlanVO.getYear();
        this.month = workPlanVO.getMonth();
        this.monthRate = workPlanVO.getMonthRate();
        if (monthRate.compareTo(BigDecimal.ZERO) > 0) {
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(workPlanVO.getYear()), Integer.parseInt(workPlanVO.getMonth()));
            BigDecimal dayDecimal = new BigDecimal(yearMonthObject.lengthOfMonth());
            dayDecimal = workPlanVO.getMonthRate().divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP).divide(dayDecimal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            this.dayRate = dayDecimal.stripTrailingZeros().doubleValue();
        } else {
            this.dayRate = 0;
        }
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setWorkPlanAtUpdateWorkPlan(WorkPlanVO workPlanVO, UserInfo userInfo) {
        this.work = new Work(workPlanVO.getWorkId());
        this.year = workPlanVO.getYear();
        this.month = workPlanVO.getMonth();
        this.monthRate = workPlanVO.getMonthRate();
        if (monthRate.compareTo(BigDecimal.ZERO) > 0) {
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(workPlanVO.getYear()), Integer.parseInt(workPlanVO.getMonth()));
            BigDecimal dayDecimal = new BigDecimal(yearMonthObject.lengthOfMonth());
            dayDecimal = workPlanVO.getMonthRate().divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP).divide(dayDecimal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            this.dayRate = dayDecimal.stripTrailingZeros().doubleValue();
        } else {
            this.dayRate = 0;
        }
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
