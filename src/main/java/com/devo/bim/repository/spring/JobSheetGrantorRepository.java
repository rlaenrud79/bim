package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetGrantor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSheetGrantorRepository extends JpaRepository<JobSheetGrantor, Long> {
    Optional<JobSheetGrantor> findByIdAndJobSheetId(long id, long jobSheetId);

    Optional<JobSheetGrantor> findByJobSheetId(long jobSheetId);
}
