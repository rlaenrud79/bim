package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungProcessItemTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GisungProcessItemTargetRepository extends JpaRepository<GisungProcessItemTarget, Long> {
    Optional<GisungProcessItemTarget> findById(long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_process_item_target where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
