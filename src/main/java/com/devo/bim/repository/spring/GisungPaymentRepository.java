package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungPayment;
import com.devo.bim.model.entity.GisungTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungPaymentRepository extends JpaRepository<GisungPayment, Long> {
    Optional<GisungPayment> findByIdAndProjectId(long id, long projectId);
}
