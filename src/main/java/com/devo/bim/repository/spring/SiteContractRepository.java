package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.SiteContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteContractRepository extends JpaRepository<SiteContract, Long> {
    @Query(name = "find_site_contract", nativeQuery = true)
    List<SiteContract> findAllBy(@Param("projectId") Long projectId);
}
