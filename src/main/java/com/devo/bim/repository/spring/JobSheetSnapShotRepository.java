package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetSnapShot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSheetSnapShotRepository extends JpaRepository<JobSheetSnapShot, Long> {
    List<JobSheetSnapShot> findByJobSheetId(long jobSheetId);
}
