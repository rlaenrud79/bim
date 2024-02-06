package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSheetReferenceRepository extends JpaRepository<JobSheetReference, Long> {
    List<JobSheetReference> findByJobSheetId(long jobSheetId);
}
