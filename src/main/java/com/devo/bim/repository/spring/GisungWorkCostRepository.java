package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungWorkCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GisungWorkCostRepository extends JpaRepository<GisungWorkCost, Long> {
    List<GisungWorkCost> findGisungWorkCostByGisungId(long gisungId);

    Optional<GisungWorkCost> findByWorkIdAndGisungNo(long workId, Integer gisungNo);

    Optional<GisungWorkCost> findByWorkIdAndGisungId(long workId, long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_work_cost where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);

    @Query(value = "select COALESCE(sum(paid_cost), 0.00) from gisung_work_cost\n" +
            " where project_id=:projectId and year=:year and work_id=:workId and gisung_id < :gisungId", nativeQuery = true)
    BigDecimal getPrevPaidCost(long projectId, String year, long workId, long gisungId);
}
