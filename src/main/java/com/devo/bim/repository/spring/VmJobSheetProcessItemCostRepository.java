package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.VmJobSheetProcessItemCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmJobSheetProcessItemCostRepository extends JpaRepository<VmJobSheetProcessItemCost, Long> {
    List<VmJobSheetProcessItemCost> findVmJobSheetProcessItemCostByJobSheetId(long jobSheetId);
}
