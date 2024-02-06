package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessWorkerRepository extends JpaRepository<ProcessWorker, Long> {
    Optional<ProcessWorker> findById(long id);

    //Optional<ProcessWorker> findByProjectIdAndProcessIdAndWorkerName(long projectId, long processId, String workerName);
    @Query(value = "select * from process_worker where project_id=:projectId and process_id=:processId and worker_name=:workerName", nativeQuery = true)
    Optional<ProcessWorker> findByProjectIdAndProcessIdAndWorkerName(long projectId, long processId, String workerName);

    @Query(value = "select COALESCE(sum(worker_amount), 0.00) from job_sheet_process_item_worker as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
            " where (j.status = 'GOING' or j.status = 'COMPLETE') and j.enabled=true and p.project_id=:projectId and p.process_id=:processId and process_worker_id=:processWorkerId", nativeQuery = true)
    Optional<BigDecimal> getJobSheetProcessItemWorkerAmount(long projectId, long processId, long processWorkerId);

    @Query(value = "select * from process_worker where project_id=:projectId and process_id=:processId order by id desc", nativeQuery = true)
    List<ProcessWorker> findByProjectIdAndProcessIdAndWorkerList(long projectId, long processId);
}
