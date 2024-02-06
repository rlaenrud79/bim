package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungCoverRepository extends JpaRepository<GisungCover, Long> {
    Optional<GisungCover> findById(long id);

    List<GisungCover> findGisungCoverByGisungId(long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_cover where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);

}
