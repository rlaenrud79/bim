package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GisungExcelFileRepository extends JpaRepository<GisungExcelFile, Long> {
    List<GisungExcelFile> findById(long id);
}
