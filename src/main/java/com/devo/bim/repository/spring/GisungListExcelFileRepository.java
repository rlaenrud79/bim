package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.Gisung;
import com.devo.bim.model.entity.GisungListExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GisungListExcelFileRepository extends JpaRepository<GisungListExcelFile, Long> {
    Optional<GisungListExcelFile> findById(long id);

    List<GisungListExcelFile> findByGisungId(long gisungId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_list_excel_file where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);

    @Query(value = "select * from gisung_list_excel_file where gisung_id = :gisungId order by id desc limit 1", nativeQuery = true)
    Optional<GisungListExcelFile> findGisungListExcelFileByGisungId(long gisungId);
}
