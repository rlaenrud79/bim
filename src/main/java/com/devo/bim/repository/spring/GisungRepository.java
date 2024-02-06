package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.Gisung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GisungRepository extends JpaRepository<Gisung, Long> {
    Optional<Gisung> findByIdAndProjectId(long id, long projectId);

    Optional<Gisung> findByProjectIdAndYearAndMonth(long projectId, String year, String month);

    @Query(value = "select COALESCE(max(gisung_no), 1) from gisung where project_id=:projectId", nativeQuery = true)
    Optional<Integer> getGisungMaxGisungNo(long projectId);
}
