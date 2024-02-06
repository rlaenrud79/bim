package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSheetTemplateRepository extends JpaRepository<JobSheetTemplate, Long> {

    Optional<JobSheetTemplate> findByIdAndProjectId(long jobSheetTemplateId, long projectId);
}
