package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GisungProcessItemRepository extends JpaRepository<GisungProcessItem, Long> {
    Optional<GisungProcessItem> findById(long id);
    Optional<GisungProcessItem> findByGisungIdAndPhasingCode(long gisungId, String phasingCode);

    Optional<GisungProcessItem> findByPhasingCode(String phasingCode);

    List<GisungProcessItem> findGisungProcessItemByGisungId(long gisungId);

    @Query(value = "select COALESCE(sum(gp.cost), 0.0000) from gisung_process_item as gp left join gisung as g on gp.gisung_id=g.id\n" +
            " where status='COMPLETE' and phasing_code=:phasingCode and process_item_id=:processItemId", nativeQuery = true)
    Optional<BigDecimal> getGisungProcessItemSumCost(String phasingCode, long processItemId);

    @Query(value = "select COALESCE(sum(gp.progress_rate), 0.0000) from gisung_process_item as gp left join gisung as g on gp.gisung_id=g.id\n" +
            " where status='COMPLETE' and phasing_code=:phasingCode and process_item_id=:processItemId", nativeQuery = true)
    Optional<BigDecimal> getGisungProcessItemSumProgressRate(String phasingCode, long processItemId);

    @Query(value = "select gp.* from gisung_process_item as gp left join gisung as g on gp.gisung_id=g.id\n" +
            " where status='COMPLETE' and phasing_code=:phasingCode and process_item_id=:processItemId order by gp.paid_cost desc limit 1", nativeQuery = true)
    Optional<GisungProcessItem> getGisungProcessItemPrevPaidCostInfo(String phasingCode, long processItemId);

    @Modifying
    @Query(value = "delete from gisung_process_item where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);

    @Query(value = "select COALESCE(ROUND(sum(gp.cost)), 0) from gisung_process_item as gp left join gisung as g on gp.gisung_id=g.id\n" +
            " where status='COMPLETE' and year=:year and month=:month", nativeQuery = true)
    Optional<Long> getGisungProcessItemSumYearMonthCost(String year, String month);
}
