package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItemCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSheetProcessItemCostRepository extends JpaRepository<JobSheetProcessItemCost, Long> {
    List<JobSheetProcessItemCost> findJobSheetProcessItemCostByJobSheetId(long jobSheetId);
}
