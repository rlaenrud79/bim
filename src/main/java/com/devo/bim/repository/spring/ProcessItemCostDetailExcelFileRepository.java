package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessItemCostDetailExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessItemCostDetailExcelFileRepository extends JpaRepository<ProcessItemCostDetailExcelFile, Long> {
    Optional<ProcessItemCostDetailExcelFile> findById(long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from process_item_cost_detail_excel_file where id = :id", nativeQuery = true)
    void deleteAllByIdInQuery(long id);

    @Query(value = "select * from process_item_cost_detail_excel_file order by id desc limit 1", nativeQuery = true)
    Optional<ProcessItemCostDetailExcelFile> findProcessItemCostDetailExcelFileByGisungId();
}
