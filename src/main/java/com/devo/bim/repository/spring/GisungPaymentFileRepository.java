package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungPaymentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GisungPaymentFileRepository extends JpaRepository<GisungPaymentFile, Long> {
    List<GisungPaymentFile> findByGisungPaymentId(long gisungPaymentId);
}
