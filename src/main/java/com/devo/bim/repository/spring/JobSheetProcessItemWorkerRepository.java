package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItemDevice;
import com.devo.bim.model.entity.JobSheetProcessItemWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSheetProcessItemWorkerRepository extends JpaRepository<JobSheetProcessItemWorker, Long> {
    List<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByJobSheetId(long jobSheetId);

    List<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByJobSheetProcessItemId(long jobSheetProcessItemId);

    @Query(value = "select name as worker_name,\n" +
            " COALESCE((select worker_amount from job_sheet_process_item_worker where worker_name = w.name and job_sheet_id=:jobSheetId), 0.00) as worker_amount,\n" +
            " COALESCE((select before_worker_amount from job_sheet_process_item_worker where worker_name = w.name and job_sheet_id=:jobSheetId), 0.00) as before_worker_amount\n" +
            " from select_progress_config as w where w.ctype='worker'  order by sorder asc", nativeQuery = true)
    List<JobSheetProcessItemWorker> getProcessItemWorkerDetail(long jobSheetId);

    //Optional<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByWorkerName(String workerName);

    //@Query(value = "select * from job_sheet_process_item_worker where worker_name = :workerName order by id desc", nativeQuery = true)
    //Optional<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByWorkerName(String workerName);
}
