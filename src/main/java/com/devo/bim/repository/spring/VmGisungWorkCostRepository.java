package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.VmGisungWorkCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmGisungWorkCostRepository extends JpaRepository<VmGisungWorkCost, Long> {
    List<VmGisungWorkCost> findByGisungId(long gisungId);

    List<VmGisungWorkCost> findByGisungIdAndWorkId(long gisungId, long workId);
}
