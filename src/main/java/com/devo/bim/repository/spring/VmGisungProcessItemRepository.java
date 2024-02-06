package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungProcessItem;
import com.devo.bim.model.entity.VmGisungProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmGisungProcessItemRepository extends JpaRepository<VmGisungProcessItem, Long> {
    List<VmGisungProcessItem> findByGisungId(long gisungId);

    List<VmGisungProcessItem> findByGisungIdAndWorkId(long gisungId, long workId);
}
