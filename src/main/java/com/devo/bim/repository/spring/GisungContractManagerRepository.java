package com.devo.bim.repository.spring;

import com.devo.bim.model.dto.GisungContractManagerDTO;
import com.devo.bim.model.entity.GisungContractManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungContractManagerRepository extends JpaRepository<GisungContractManager, Long> {
    Optional<GisungContractManager> findById(long id);

    Optional<GisungContractManager> findByIdAndProjectId(long id, long projectId);

    List<GisungContractManager> findGisungContractManagerByProjectId(long projectId);
}
