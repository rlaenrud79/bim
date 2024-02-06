package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItemDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobSheetProcessItemDeviceRepository extends JpaRepository<JobSheetProcessItemDevice, Long> {
    List<JobSheetProcessItemDevice> findJobSheetProcessItemDeviceByJobSheetId(long jobSheetId);

    List<JobSheetProcessItemDevice> findJobSheetProcessItemDeviceByJobSheetProcessItemId(long jobSheetProcessItemId);

    @Query(value = "select name as device_name,\n" +
            " COALESCE((select device_amount from job_sheet_process_item_device where device_name = w.name and job_sheet_id=:jobSheetId), 0.00) as device_amount,\n" +
            " COALESCE((select before_device_amount from job_sheet_process_item_device where device_name = w.name and job_sheet_id=:jobSheetId), 0.00) as before_device_amount\n" +
            " from select_progress_config as w where w.ctype='device'  order by sorder asc", nativeQuery = true)
    List<JobSheetProcessItemDevice> getProcessItemDeviceDetail(long jobSheetId);
}
