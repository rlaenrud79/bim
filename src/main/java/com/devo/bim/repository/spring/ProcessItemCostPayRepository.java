package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessItemCostPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessItemCostPayRepository extends JpaRepository<ProcessItemCostPay, Long> {
    @Modifying
    @Query(value = "insert into process_item_cost_pay(process_item_id, cost_date, progress_rate, cost, sum_progress_rate, sum_cost \n" +
            ", description, writer_id, write_date, last_modifier_id, last_modify_date, task_progress_rate, task_cost) \n" +
            "select (   select  id \n" +
            "           from    process_item \n" +
            "           where   process_id = :newProcessId \n" +
            "             and     gantt_sort_no = (   select  gantt_sort_no \n" +
            "                                         from    process_item \n" +
            "                                         where   process_id = :currentProcessId \n" +
            "                                           and     id = a.process_item_id)) as process_item_id \n" +
            "     , cost_date, progress_rate, cost, sum_progress_rate, sum_cost, description, :userId, now(), :userId, now(), task_progress_rate, task_cost \n" +
            "from    process_item_cost_pay as a \n" +
            "where   process_item_id in (select id \n" +
            "                            from process_item \n" +
            "                            where process_id = :currentProcessId) ", nativeQuery = true)
    void insertSelectForNewVersion(long currentProcessId, long newProcessId, long userId);

    @Query(value = "select * from process_item_cost_pay\n" +
            " where process_item_id=:processItemId order by id desc limit 1", nativeQuery = true)
    Optional<ProcessItemCostPay> getProcessItemCostPayByMaxId(long processItemId);

}
