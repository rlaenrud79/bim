package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungItemRepository extends JpaRepository<GisungItem, Long> {
    Optional<GisungItem> findById(long id);

    Optional<GisungItem> findByGisungIdAndDocumentNo(long gisungId, Integer no);

    List<GisungItem> findGisungItemByGisungId(long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_item where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
