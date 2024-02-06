package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GisungAggregationRepository extends JpaRepository<GisungAggregation, Long> {
    Optional<GisungAggregation> findById(long id);

    List<GisungAggregation> findByYear(String year);

    List<GisungAggregation> findByYearAndDocumentNo(String year, Integer documentNo);

    @Query(value = "select * from gisung_aggregation where year=:year and gtype=:gtype and document_no=:documentNo order by id asc", nativeQuery = true)
    List<GisungAggregation> findByYearAndGtype(String year, String gtype, Integer documentNo);

    @Query(value = "select distinct year from gisung_aggregation where project_id=:projectId " +
            "order by year desc", nativeQuery = true)
    List<Map<String, Object>> findGisungAggregationByYear(long projectId);
}
