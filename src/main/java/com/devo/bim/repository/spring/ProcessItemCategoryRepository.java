package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessInfo;
import com.devo.bim.model.entity.ProcessItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessItemCategoryRepository extends JpaRepository<ProcessItemCategory, Long> {
    Optional<ProcessItemCategory> findByCode(String code);

    @Query(value = "select * from process_item_category" +
            " where up_code=:upCode order by display", nativeQuery = true)
    List<ProcessItemCategory> selectCateList(String upCode);

    @Query(value = "select right(concat(:strZero, COALESCE((MAX(CAST(SUBSTRING(code, :intCLen, :intLen) AS INTEGER)) + 1), 1)), :intLen) FROM process_item_category", nativeQuery = true)
    String selectMaxCateCode(String strZero, int intCLen, int intLen);

    @Query(value = "select COALESCE(max(display)+1,1) FROM process_item_category" +
            " where up_code=:upCode", nativeQuery = true)
    long getDisplay(String upCode);

    @Modifying
    @Query(value = "update process_item_category \n"
            + "set      name = :name \n"
            + "where    code = :code", nativeQuery = true)
    void updateCategoryName(String code, String name);

    @Modifying
    @Query(value = "update process_item_category \n"
            + "set      display = :display \n"
            + "where    code = :code", nativeQuery = true)
    void updateCategoryDisplay(String code, int display);
}
