package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {
    Optional<DocumentCategory> findByIdAndProjectId(long id, long projectId);

    Optional<DocumentCategory> findById(long id);

    @Modifying
    @Query(value = "update document_category set sort_no = sort_no + 1 where project_id = :projectId and sort_no >= :sortNo", nativeQuery = true)
    void addOneGOESortNo(int sortNo, long projectId);
}
