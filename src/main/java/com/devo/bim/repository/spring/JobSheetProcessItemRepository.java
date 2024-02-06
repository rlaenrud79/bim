package com.devo.bim.repository.spring;


import java.math.BigDecimal;
import java.util.List;

import com.devo.bim.model.entity.JobSheet;
import com.devo.bim.model.entity.JobSheetProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSheetProcessItemRepository extends JpaRepository<JobSheetProcessItem, Long> {
	List<JobSheetProcessItem> findJobSheetProcessItemByJobSheetId(long jobSheetId);

	Optional<JobSheetProcessItem> findJobSheetProcessItemById(long processItemId);

	Optional<JobSheetProcessItem> findById(long id);

	@Query(value = "select * from job_sheet_process_item as p left join job_sheet as j on p.job_sheet_id=j.id" +
			" where process_item_id=:processItemId order by p.id desc", nativeQuery = true)
	List<JobSheet> selectJobSheetProcessItemId(long processItemId);

	@Query(value = "select COALESCE(sum(today_progress_rate), 0.00) from vm_job_sheet_process_item_cost\n" +
			" where phasing_code=:phasingCode and process_item_id=:processItemId", nativeQuery = true)
	Optional<BigDecimal> getJobSheetProcessItemTodayProgressRate(String phasingCode, long processItemId);

	@Query(value = "select COALESCE(sum(today_progress_amount), 0.00) from vm_job_sheet_process_item_cost\n" +
			" where phasing_code=:phasingCode and process_item_id=:processItemId", nativeQuery = true)
	Optional<Long> getJobSheetProcessItemTodayProgressAmount(String phasingCode, long processItemId);

	//@Query("SELECT SUM(today_progress_rate) FROM job_sheet_process_item WHERE process_item_id = :processItemId")
	//Double getJobSheetProcessItemTodayProgressRate(long processItemId);

	//@Query(value = "select * from job_sheet_process_item as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
	//		"where j.id != :jobSheetId and process_item_id=:processItemId and report_date < ':reportDate'::DATE order by report_date desc limit 1", nativeQuery = true)
	//List<JobSheetProcessItem> findByJobSheetProcessItemIdAndJobSheetIdAndItemIdAndReportDate(long jobSheetId, long processItemId, String reportDate);

	@Modifying
	@Query(value = "delete from job_sheet_process_item_cost where job_sheet_id = :jobSheetId", nativeQuery = true)
	void deleteAllByIdInQuery(long jobSheetId);

	@Query(value = "select p.* from job_sheet_process_item as p left join job_sheet as j on p.job_sheet_id=j.id\n" +
			" where j.project_id=:projectId and j.enabled = true", nativeQuery = true)
	List<JobSheetProcessItem> findAllJobSheetByEnabledByStatus(long projectId);

	@Query(value = "select count(*) from job_sheet_process_item\n" +
			" where phasing_code=:phasingCode and process_item_id=:processItemId", nativeQuery = true)
	Optional<Long> getJobSheetProcessItemCount(String phasingCode, long processItemId);

	@Modifying
	@Query(value = "delete from job_sheet_process_item where gisung_id = :gisungId", nativeQuery = true)
	void deleteAllByGisungIdInQuery(long gisungId);

	@Modifying
	@Query(value = "delete from job_sheet_process_item where gisung_id = :gisungId and process_item_id=:processItemId", nativeQuery = true)
	void deleteAllByGisungIdAndProcessItemIdInQuery(long gisungId, long processItemId);

	@Query(value = "select COALESCE(ROUND(sum(today_progress_amount)), 0) from job_sheet_process_item as jp left join job_sheet as j on jp.job_sheet_id=j.id\n" +
			" where (j.status='COMPLETE' or j.status='GOING') and j.report_date=TO_TIMESTAMP(:reportDate, 'YYYY-MM-DD') and j.enabled=true", nativeQuery = true)
	Optional<Long> getJobSheetProcessItemTodayProgressAmountSum(String reportDate);
}
