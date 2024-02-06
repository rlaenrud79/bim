package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Document;
import com.devo.bim.model.entity.WorkAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByIdAndProjectId(long id, long projectId);
}