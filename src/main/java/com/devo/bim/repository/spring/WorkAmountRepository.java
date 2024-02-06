package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.WorkAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WorkAmountRepository extends JpaRepository<WorkAmount, Long> {
    Optional<WorkAmount> findByIdAndProjectId(long id, long projectId);

    Optional<WorkAmount> findByWorkIdAndYear(long workId, String year);

    @Query(value = "select COALESCE(sum(amount), 0.0000) from work_amount\n" +
            " where project_id=:projectId and year=:year", nativeQuery = true)
    Optional<BigDecimal> getWorkAmountByAmountSum(long projectId, String year);
}
