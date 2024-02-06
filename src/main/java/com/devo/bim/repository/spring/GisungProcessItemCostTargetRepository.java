package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungProcessItemCostTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GisungProcessItemCostTargetRepository extends JpaRepository<GisungProcessItemCostTarget, Long> {
    Optional<GisungProcessItemCostTarget> findById(long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_process_item_cost_target where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
