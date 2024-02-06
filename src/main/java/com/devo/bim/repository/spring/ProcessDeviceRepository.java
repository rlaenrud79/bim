package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItem;
import com.devo.bim.model.entity.ProcessDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessDeviceRepository extends JpaRepository<ProcessDevice, Long> {
    Optional<ProcessDevice> findById(long id);

    //Optional<ProcessDevice> findByProjectIdAndProcessIdAndDeviceName(long projectId, long processId, String deviceName);
    @Query(value = "select * from process_device where project_id=:projectId and process_id=:processId and device_name=:deviceName", nativeQuery = true)
    Optional<ProcessDevice> findByProjectIdAndProcessIdAndDeviceName(long projectId, long processId, String deviceName);

    @Query(value = "select COALESCE(sum(device_amount), 0.00) from job_sheet_process_item_device as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
            " where (j.status = 'GOING' or j.status = 'COMPLETE') and j.enabled=true and p.project_id=:projectId and p.process_id=:processId and process_device_id=:processDeviceId", nativeQuery = true)
    Optional<BigDecimal> getJobSheetProcessItemDeviceAmount(long projectId, long processId, long processDeviceId);

    @Query(value = "select * from process_device where project_id=:projectId and process_id=:processId order by id desc", nativeQuery = true)
    List<ProcessDevice> findByProjectIdAndProcessIdAndDeviceList(long projectId, long processId);
}
