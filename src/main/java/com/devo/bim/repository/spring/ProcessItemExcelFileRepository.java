package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessItemExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessItemExcelFileRepository extends JpaRepository<ProcessItemExcelFile, Long> {
    Optional<ProcessItemExcelFile> findById(long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from process_item_excel_file where id = :id", nativeQuery = true)
    void deleteAllByIdInQuery(long id);

    @Query(value = "select * from process_item_excel_file order by id desc limit 1", nativeQuery = true)
    Optional<ProcessItemExcelFile> findProcessItemExcelFileById();
}
