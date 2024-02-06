package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungCover;
import com.devo.bim.model.entity.GisungReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungReportRepository extends JpaRepository<GisungReport, Long> {
    Optional<GisungReport> findByIdAndProjectId(long id, long projectId);

    List<GisungReport> findGisungReportByGisungId(long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_report where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
