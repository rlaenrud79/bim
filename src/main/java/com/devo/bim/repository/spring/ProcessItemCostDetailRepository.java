package com.devo.bim.repository.spring;

import com.devo.bim.model.dto.ProcessItemCostDetailDTO;
import com.devo.bim.model.entity.GisungListExcelFile;
import com.devo.bim.model.entity.ProcessItemCostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessItemCostDetailRepository extends JpaRepository<ProcessItemCostDetail, Long> {

    @Modifying
    @Query(value = "insert into process_item_cost_detail(process_item_id, code, name, count, unit, unit_price, cost \n" +
            ", is_first, writer_id, write_date, last_modifier_id, last_modify_date) \n" +
            "select (   select  id \n" +
            "           from    process_item \n" +
            "           where   process_id = :newProcessId \n" +
            "             and     gantt_sort_no = (   select  gantt_sort_no \n" +
            "                                         from    process_item \n" +
            "                                         where   process_id = :currentProcessId \n" +
            "                                           and     id = a.process_item_id)) as process_item_id \n" +
            "        , code, name, count, unit, unit_price, cost, is_first, :userId, now(), :userId, now() \n" +
            "from    process_item_cost_detail as a \n" +
            "where   process_item_id in (select id \n" +
            "                            from process_item \n" +
            "                            where process_id = :currentProcessId) ", nativeQuery = true)
    void insertSelectForNewVersion(long currentProcessId, long newProcessId, long userId);


    @Modifying
    @Query(value = "insert into process_item_cost_detail(process_item_id, code, name, count, unit, unit_price, cost, is_first, writer_id, write_date, last_modifier_id, last_modify_date) \n"
            + "values(:processItemId, :code, :name, :count, :unit, :unitPrice, :cost, :isFirst, :writerId, :writeDate, :lastModifierId, :lastModifyDate)", nativeQuery = true)
    void insertProcessItemCostDetailExcel(long processItemId, String code, String name, BigDecimal count, String unit, BigDecimal unitPrice, BigDecimal cost, boolean isFirst, long writerId, LocalDateTime writeDate, long lastModifierId, LocalDateTime lastModifyDate);

    @Modifying
    @Query(value = "delete from process_item_cost_detail \n"
                + "where process_item_id = :processItemId", nativeQuery = true)
    void deleteByProcessItemId(long processItemId);

    @Query(value = "select * from process_item_cost_detail where process_item_id = :processItemCostDetail", nativeQuery = true)
    List<ProcessItemCostDetail> findByProcessItemId(long processItemCostDetail);

    @Query(value = "select process_item.task_name , process_item.phasing_code , process_item_cost_detail.* from process_item \n"
            + " right join process_item_cost_detail on process_item.id = process_item_cost_detail.process_item_id "
            + "where process_item.process_id = :processId order by process_item.row_num asc, process_item.parent_row_num asc", nativeQuery = true)
    List<ProcessItemCostDetail> findCostDetailByProcessId(long processId);

    @Query(value = "select count from process_item_cost_detail\n" +
            " where is_first=true and process_item_id=:processItemId", nativeQuery = true)
    Optional<BigDecimal> findProcessItemCostDetailByProcessItemIdByIsFirst(long processItemId);

    @Query(value = "select * from process_item_cost_detail where id = :processItemCostDetailId", nativeQuery = true)
    ProcessItemCostDetailDTO findByProcessItemCostDetailId(long processItemCostDetailId);
}
