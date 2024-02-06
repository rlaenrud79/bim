package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.WorkPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
    Optional<WorkPlan> findByIdAndProjectId(long id, long projectId);

    Optional<WorkPlan> findByWorkIdAndYearAndMonth(long workId, String year, String month);

    @Query(value = "select COALESCE(sum(month_rate), 0.0000) from work_plan\n" +
            " where year=:year and month=:month", nativeQuery = true)
    Optional<BigDecimal> getWorkPlanByMonthRateSum(String year, String month);

    @Query(value = "select COALESCE(avg(day_rate), 0.0000) from work_plan\n" +
            " where year=:year and month=:month", nativeQuery = true)
    Optional<BigDecimal> getWorkPlanByDayRateSum(String year, String month);
}
