package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSheetFileRepository extends JpaRepository<JobSheetFile, Long> {
    List<JobSheetFile> findByJobSheetId(long jobSheetId);
}
