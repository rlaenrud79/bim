package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.JobSheetProcessItem;
import com.devo.bim.model.entity.ViewJobSheetProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewJobSheetProcessItemRepository extends JpaRepository<ViewJobSheetProcessItem, Long> {
    List<ViewJobSheetProcessItem> findViewJobSheetProcessItemByJobSheetId(long jobSheetId);

    @Query(value = "select * from view_job_sheet_process_item\n" +
            " where id=:processItemId", nativeQuery = true)
    Optional<ViewJobSheetProcessItem> findViewJobSheetProcessItemByProcessItemId(long processItemId);

    @Query(value = "select p.* from view_job_sheet_process_item as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
            " where j.project_id=:projectId and j.enabled = true and (j.status='COMPLETE' or j.status='GOING') and j.report_date=TO_TIMESTAMP(:reportDate, 'YYYY-MM-DD') limit :limit", nativeQuery = true)
    List<ViewJobSheetProcessItem> getJobSheetProcessItemReportDateListLimit(long projectId, String reportDate, int limit);
}
