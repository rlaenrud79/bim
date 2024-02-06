package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRoleRepository extends JpaRepository<CompanyRole, Long> {

    Optional<CompanyRole> findByIdAndProjectId(long companyRoleId, long projectId);
    Optional<CompanyRole> findByProjectIdAndSortNo(long projectId, int sortNo);

    @Modifying
    @Query(value = "update company_role set sort_no = sort_no + 1 where project_id = :projectId and sort_no >= :sortNo", nativeQuery = true)
    void addOneGOESortNo(long projectId, long sortNo);

    @Modifying
    @Query(value = "update company_role set sort_no = sort_no - 1 where project_id = :projectId and sort_no > :savedSortNo and sort_no <= :newSortNo ", nativeQuery = true)
    void minusOneGTSavedSortNoAndLOESortNo(long projectId, int savedSortNo, int newSortNo);

    @Modifying
    @Query(value = "update company_role set sort_no = sort_no + 1 where project_id = :projectId and sort_no < :savedSortNo and sort_no >= :newSortNo", nativeQuery = true)
    void addOneLTSavedSortNoAndGOESortNo(long projectId, int savedSortNo, int newSortNo);
}
