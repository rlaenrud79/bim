package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.JobSheet;
import com.devo.bim.model.entity.JobSheetProcessItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobSheetRepository extends JpaRepository<JobSheet, Long> {
    @EntityGraph(attributePaths = {"jobSheetReferences"})
    Optional<JobSheet> findJobSheetReferencesById(long id);

    Optional<JobSheet> findByIdAndProjectId(long id, long projectId);

    @Query(value = "select * from job_sheet\n" +
            " where (status = 'GOING' or status = 'COMPLETE') and writer_id=:writerId and enabled=true order by id desc limit 1", nativeQuery = true)
    Optional<JobSheet> findJobSheetAndWriterId(long writerId);

    @Query(value = "select * from job_sheet\n" +
            " where (status = 'COMPLETE') and project_id=:projectId and enabled=true order by id desc limit 1", nativeQuery = true)
    Optional<JobSheet> findJobSheetAndProjectId(long projectId);

    @Query(value = "select p.* from job_sheet_process_item as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
            " where j.enabled = true", nativeQuery = true)
    List<JobSheetProcessItem> findAllProcessIdByJobSheetByEnabled();

    @Query(value = "select to_char(max(report_date), 'yyyy-mm-dd') from job_sheet\n" +
            " where (status = 'GOING' or status = 'COMPLETE') and enabled=true", nativeQuery = true)
    Optional<String> findJobSheetAndReportDate();

    @Query(value = "select * from job_sheet\n" +
            " where (status = 'GOING' or status = 'COMPLETE') and project_id=:projectId and enabled=true and report_date >= TO_TIMESTAMP(:startReportDate, 'YYYY-MM-DD') and report_date <= TO_TIMESTAMP(:endReportDate, 'YYYY-MM-DD') order by report_date desc limit 1", nativeQuery = true)
    Optional<JobSheet> findJobSheetAndGisungReportDate(long projectId, String startReportDate, String endReportDate);
}
