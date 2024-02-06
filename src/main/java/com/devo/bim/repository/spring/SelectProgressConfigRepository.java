package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItemWorker;
import com.devo.bim.model.entity.SelectProgressConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectProgressConfigRepository extends JpaRepository<SelectProgressConfig, Long> {

    /**
    @Query(value = "select * from (select w.id, name, COALESCE((select worker_amount from process_worker where worker_name = w.name), 0.00) as amount, w.sorder, w.process_id, ctype\n" +
            " from select_progress_config as w left join process_item as p on w.process_id = p.process_id\n" +
            " where p.id =:processItemId and w.ctype='worker'  order by sorder asc) as a\n" +
            " union all\n" +
            " select * from (select w.id, name, COALESCE((select device_amount from process_device where device_name = w.name), 0.00) as amount, w.sorder, w.process_id, ctype\n" +
            " from select_progress_config as w left join process_item as p on w.process_id = p.process_id\n" +
            " where p.id =:processItemId and w.ctype='device' order by sorder asc) as b", nativeQuery = true)
    **/
    @Query(value = "select * from (select w.id, name, COALESCE(" +
            "(select sum(worker_amount)\n" +
            "         from job_sheet_process_item_worker as jw\n" +
            "                  left join job_sheet js on js.id = jw.job_sheet_id\n" +
            "where process_item_id=p.id and (js.status='GOING' or js.status='COMPLETE') and js.enabled = true\n" +
            "and worker_name = w.name), 0.00) as amount" +
            ", w.sorder, w.process_id, ctype\n" +
            " from select_progress_config as w left join process_item as p on w.process_id = p.process_id\n" +
            " where p.id =:processItemId and w.ctype='worker'  order by sorder asc) as a\n" +
            " union all\n" +
            " select * from (select w.id, name, COALESCE(" +
            "(select sum(device_amount)\n" +
            "         from job_sheet_process_item_device as jw\n" +
            "                  left join job_sheet js on js.id = jw.job_sheet_id\n" +
            "where process_item_id=p.id and (js.status='GOING' or js.status='COMPLETE') and js.enabled = true\n" +
            "and device_name = w.name), 0.00) as amount" +
            ", w.sorder, w.process_id, ctype\n" +
            " from select_progress_config as w left join process_item as p on w.process_id = p.process_id\n" +
            " where p.id =:processItemId and w.ctype='device' order by sorder asc) as b", nativeQuery = true)
    List<SelectProgressConfig> getProcessItemWorkerDeviceDetail(long processItemId);

}
