package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Cost;
import com.devo.bim.model.entity.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {
    Optional<Cost> findByIdAndProjectId(long costId, long projectId);

    @EntityGraph(attributePaths = {"costSnapShots"})
    List<Cost> findByProjectId(long projectId);
}
