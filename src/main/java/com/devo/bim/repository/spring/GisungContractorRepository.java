package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungContractorRepository extends JpaRepository<GisungContractor, Long> {
    Optional<GisungContractor> findById(long id);

    List<GisungContractor> findGisungContractorByGisungId(long gisungId);

    List<GisungContractor> findGisungContractorByGisungIdAndDocumentNo(long gisungId, Integer documentNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_contractor where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
