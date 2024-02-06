package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungProcessItemCostDetail;
import com.devo.bim.model.entity.JobSheetProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GisungProcessItemCostDetailRepository extends JpaRepository<GisungProcessItemCostDetail, Long> {
    Optional<GisungProcessItemCostDetail> findById(long id);

    Optional<GisungProcessItemCostDetail> findByGisungProcessItemIdAndCode(long gisungProcessItemId, String code);

    List<GisungProcessItemCostDetail> findGisungProcessItemCostDetailByGisungProcessItemId(long gisungProcessItemId);

    List<GisungProcessItemCostDetail> findGisungProcessItemCostDetailByGisungId(long gisungId);

    List<GisungProcessItemCostDetail> findByGisungIdAndCode(long gisungId, String code);
    List<GisungProcessItemCostDetail> findByGisungIdAndGisungProcessItemId(long gisungId, long gisungProcessItemId);

    @Modifying
    @Query(value = "delete from gisung_process_item_cost_detail where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);

    @Query(value = "select COALESCE(sum(count - paid_progress_count), 0.00) from gisung_process_item_cost_detail\n" +
            " where gisung_id=:gisungId and code=:code", nativeQuery = true)
    Optional<BigDecimal> getGisungProcessItemCostDetailByCountSum(long gisungId, String code);

    @Query(value = "select COALESCE(sum(cost - paid_cost), 0.00) from gisung_process_item_cost_detail\n" +
            " where gisung_id=:gisungId and code=:code", nativeQuery = true)
    Optional<BigDecimal> getGisungProcessItemCostDetailByCostSum(long gisungId, String code);

    @Query(value = "select * from gisung_process_item_cost_detail\n" +
            " where gisung_id=:gisungId and process_item_id=:processItemId and code=:code", nativeQuery = true)
    Optional<GisungProcessItemCostDetail> findByGisungIdAndProcessItemIdAndCode(long gisungId, long processItemId, String code);
}
