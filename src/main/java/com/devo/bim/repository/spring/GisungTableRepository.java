package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungTableRepository extends JpaRepository<GisungTable, Long> {
    Optional<GisungTable> findById(long id);

    List<GisungTable> findGisungTableByGisungId(long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_table where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
