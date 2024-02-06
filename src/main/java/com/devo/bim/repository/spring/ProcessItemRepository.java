package com.devo.bim.repository.spring;

import com.devo.bim.model.dto.ProcessItemDTO;
import com.devo.bim.model.entity.JobSheet;
import com.devo.bim.model.entity.ProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessItemRepository extends JpaRepository<ProcessItem, Long> {
    @Modifying
    @Query(value = "update process_item  \n"
            + "set parent_id = (    select  coalesce (id, 0) \n"
            + "                     from    process_item \n"
            + "                     where   process_id = :processInfoId \n"
            + "                     and     row_num = :parentRowNum \n"
            + "                     order by id desc limit 1 ) \n"
            + "where process_id = :processInfoId \n"
            + "and row_num = :rowNum ", nativeQuery = true)
    void updateParentId(long processInfoId, int rowNum, int parentRowNum);

    @Modifying
    @Query(value = "update process_item as a \n"
            + "set parent_id = (    select  coalesce (id, 0) \n"
            + "                     from    process_item \n"
            + "                     where   process_id = :processInfoId \n"
            + "                     and     row_num = a.parent_row_num \n"
            + "                     order by id desc limit 1 ) \n"
            + "where a.process_id = :processInfoId ", nativeQuery = true)
    void updateParentIds(Long processInfoId);

    @Modifying
    @Query(value = "update process_item \n"
            + "set      gantt_sort_no = gantt_sort_no + 1 \n"
            + "where    process_id = :currentProcessInfoId \n"
            + "and      gantt_sort_no >= :newGanttSortNo", nativeQuery = true)
    void updateGanttSortNoPlusOne(long currentProcessInfoId, long newGanttSortNo);

    @Modifying
    @Query(value = "update process_item \n"
            + "set      gantt_sort_no = gantt_sort_no + :different \n"
            + "where    process_id = :currentProcessInfoId \n"
            + "and      gantt_sort_no >= :newGanttSortNo", nativeQuery = true)
    void updateGanttSortNo(long currentProcessInfoId, long newGanttSortNo, int different);


    @Modifying
    @Query(value = "insert into process_item(process_id, task_name, task_depth, phasing_code, duration \n"
            + ", planned_start_date, planned_end_date, start_date, end_date, include_holiday, description \n"
            + ", validate, progress_target, progress_rate, gantt_task_type, gantt_open, gantt_holder, gantt_sort_no \n"
            + ", row_num, work_id, task_full_path, parent_row_num, parent_id \n"
            + ", first_count_formula, first_count, first_count_unit, complex_unit_price, cost, paid_cost \n"
            + ", cost_writer_id, cost_write_date, cost_last_modifier_id, cost_last_modify_date, task_status, is_bookmark, paid_progress_rate, progress_amount) \n"
            + "select :newProcessId, task_name, task_depth, phasing_code, duration \n"
            + "         , planned_start_date, planned_end_date, start_date, end_date, include_holiday, description \n"
            + "         , 'NONE', progress_target, progress_rate, gantt_task_type, gantt_open, gantt_holder, gantt_sort_no \n"
            + "         , row_num, work_id, task_full_path, parent_row_num, 0 \n"
            + "         , first_count_formula, first_count, first_count_unit, complex_unit_price, cost, paid_cost, cost_writer_id, cost_write_date \n"
            + "         , cost_last_modifier_id, cost_last_modify_date, task_status, is_bookmark, paid_progress_rate, progress_amount \n"
            + "from process_item \n"
            + "where process_id = :currentProcessId and task_status = 'REG' \n"
            + "order by gantt_sort_no asc \n", nativeQuery = true)
    void insertSelectForNewVersion(long currentProcessId, long newProcessId);

    @Modifying
    @Query(value = "update process_item as a \n" +
            "set parent_id = (   select  coalesce (id, 0) \n" +
            "                    from    process_item \n" +
            "                    where   process_id = :newProcessId \n" +
            "                    and     gantt_sort_no = (select gantt_sort_no \n" +
            "                                                from process_item \n" +
            "                                                where id = (    select  parent_id \n" +
            "                                                                from    process_item \n" +
            "                                                                where   process_id = :currentProcessId \n" +
            "                                                                and     gantt_sort_no = a.gantt_sort_no \n" +
            "                                                           )\n" +
            "                                            )\n" +
            "                )\n" +
            "where a.process_id = :newProcessId \n", nativeQuery = true)
    void updateParentIdsForNewVersion(long currentProcessId, long newProcessId);


    @Modifying
    @Query(value = "update process_item \n"
            + "set      is_bookmark = false \n"
            + "where    process_id = :currentProcessInfo ", nativeQuery = true)
    void  updateFalseIsBookMarkCurrentVersion(long currentProcessInfo);

    @Query(value = "select id \n"
            + "from process_item \n"
            + "where process_id = :processId and task_name like :taskName and phasing_code like :phasingCode", nativeQuery = true)
    List<Long> findByProcessIdAndTaskNameAndPhasingCode(Long processId, String taskName, String phasingCode);

    @Query(value = "select task_name \n"
            + "from process_item \n"
            + "where process_id = :processId and parent_row_num = 0", nativeQuery = true)
    List<String> findByProcessIdAndTaskNameAndParentRowNum(Long processId);

    @Query(value = "select pt.task_full_path as task_full_path," +
            "(select COALESCE(sum(today_progress_amount), 0) from job_sheet_process_item_cost as jpc left join process_item as p on jpc.process_item_id=p.id\n" +
            "                where p.task_full_path like pt.task_full_path || '%' and report_date >= TO_TIMESTAMP(:startReportDate, 'YYYY-MM-DD') and report_date <= TO_TIMESTAMP(:endReportDate, 'YYYY-MM-DD')) as progress_amount, " +
            "(select COALESCE(sum(today_progress_rate), 0.00) from job_sheet_process_item_cost as jpc left join process_item as p on jpc.process_item_id=p.id\n" +
            "                where p.task_full_path like pt.task_full_path || '%' and report_date >= TO_TIMESTAMP(:startReportDate, 'YYYY-MM-DD') and report_date <= TO_TIMESTAMP(:endReportDate, 'YYYY-MM-DD')) as progress_rate " +
            "from process_item as pt left join work as w on pt.work_id=w.id" +
            " where process_id=:processId and gantt_task_type='PROJECT' and task_status='REG' " +
            " and w.id=:workId and task_depth=:taskDepth order by pt.id asc", nativeQuery = true)
    List<Object[]> findProcessItemByWorkIdByTaskDepth(long processId, long workId, int taskDepth, String startReportDate, String endReportDate);

    @Query(value = "select id from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth", nativeQuery = true)
    Optional<BigDecimal> getProcessItemTopTaskId(long processId, long taskDepth);

    @Query(value = "select count(*) from process_item \n" +
            " where process_id=:processId", nativeQuery = true)
    Optional<BigDecimal> getProcessItemCount(long processId);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and (cate1 is null or cate1 = '') limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate(long processId);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth and cate1=:cate limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate1(long processId, long taskDepth, String cate);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth and cate2=:cate limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate2(long processId, long taskDepth, String cate);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth and cate3=:cate limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate3(long processId, long taskDepth, String cate);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth and cate4=:cate limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate4(long processId, long taskDepth, String cate);

    @Query(value = "select * from process_item \n" +
            " where process_id=:processId and task_depth=:taskDepth and cate5=:cate limit 1", nativeQuery = true)
    Optional<ProcessItem> findFirstProcessItemByProcessIdAndTaskDepthAndCate5(long processId, long taskDepth, String cate);

    @Query(value = "select count(*) from vm_process_item\n" +
            " where gantt_task_type='TASK' and process_id=:processId and task_status='REG'", nativeQuery = true)
    Optional<BigDecimal> getProcessItemProgressRateCount(long processId);

    @Query(value = "select COALESCE(ROUND(avg(progress_rate)), 0) from vm_process_item\n" +
            " where gantt_task_type='TASK' and process_id=:processId and task_status='REG'", nativeQuery = true)
    Optional<Long> getProcessItemProgressRateAvg(long processId);

    @Query(value = "select * \n"
            + "from process_item \n"
            + "where gantt_task_type='TASK' and task_status='REG' and process_id = :processId and start_date <= :date and end_date >= :date order by cost desc limit :limit", nativeQuery = true)
    List<ProcessItem> getProcessItemDateListLimit(Long processId, String date, int limit);

    @Query(value = "select * \n"
            + "from process_item \n"
            + "where gantt_task_type='TASK' and task_status='REG' and process_id = :processId and start_date <= :date and end_date >= :date order by task_name asc", nativeQuery = true)
    List<ProcessItem> getProcessItemDateList(Long processId, String date);


    @Query(value = "select COALESCE(sum(paid_cost), 0.00) from vm_process_item\n" +
            " where gantt_task_type='TASK' and process_id=:processId and task_status='REG'", nativeQuery = true)
    Optional<Long> getProcessItemPaidCostSum(long processId);
}
