package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheetProcessItem;
import com.devo.bim.model.entity.ProcessInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessInfoRepository extends JpaRepository<ProcessInfo, Long> {

    Optional<ProcessInfo> findByProjectId(long projectId);

    @EntityGraph(attributePaths = {"processItems"})
    Optional<ProcessInfo> findByIdAndProjectId(long id, long projectId);

    List<ProcessInfo> findByProjectIdAndIsCurrentVersion(long projectId, boolean isCurrentVersion);

}
