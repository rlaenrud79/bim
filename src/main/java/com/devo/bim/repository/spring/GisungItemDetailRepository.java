package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.GisungItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GisungItemDetailRepository extends JpaRepository<GisungItemDetail, Long> {
    List<GisungItemDetail> findGisungItemDetailByGisungItemId(long gisungItemId);

    List<GisungItemDetail> findGisungItemDetailByGisungIdAndGisungItemId(long gisungId, long gisungItemId);

    List<GisungItemDetail> findGisungItemDetailByGisungId(long gisungId);

    //@Query(value = "select count(*) from gisung_item_detail where gisung_item_id = :gisungItemId and id <= (select id from gisung_item_detail where net_check='Y')", nativeQuery = true)
    @Query(value = "select count(*) from gisung_item_detail where gisung_item_id = :gisungItemId and net_check='Y'", nativeQuery = true)
    Optional<Integer> getGisungItemDetailRowSpanCnt(long gisungItemId);

    //@Query(value = "select * from gisung_item_detail where gisung_item_id = :gisungItemId and id <= (select id from gisung_item_detail where net_check='Y') order by id asc", nativeQuery = true)
    @Query(value = "select * from gisung_item_detail where gisung_item_id = :gisungItemId and net_check='Y' order by id asc", nativeQuery = true)
    List<GisungItemDetail> getGisungItemDetailList01(long gisungItemId);

    //@Query(value = "select * from gisung_item_detail where gisung_item_id = :gisungItemId and id > (select id from gisung_item_detail where net_check='Y') order by id asc", nativeQuery = true)
    @Query(value = "select * from gisung_item_detail where gisung_item_id = :gisungItemId and (net_check is null or net_check='') order by id asc", nativeQuery = true)
    List<GisungItemDetail> getGisungItemDetailList02(long gisungItemId);

    @Query(value = "select * from gisung_item_detail where gisung_id = :gisungId and gtype = :gtype order by id asc", nativeQuery = true)
    List<GisungItemDetail> getGisungItemDetailGisungIdGtypeList(long gisungId, String gtype);

    @Query(value = "select item_detail05 from gisung_item_detail as gd left join gisung_item as g on gd.gisung_item_id=g.id where g.gisung_id = :gisungId and gd.gtype = :gtype and document_no = :documentNo order by gd.id asc", nativeQuery = true)
    List<String> getGisungItemDetailGisungIdGtypeListData(long gisungId, String gtype, Integer documentNo);

    @Query(value = "select id from gisung where id < :id and status='COMPLETE' order by id desc limit 1", nativeQuery = true)
    Optional<Long> getGisungPrevId(long id);

    @Query(value = "select count(*) from gisung where year = :year and id < :id and status='COMPLETE'", nativeQuery = true)
    Optional<Integer> getGisungYearCount(String year, long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from gisung_item_detail where gisung_id = :gisungId", nativeQuery = true)
    void deleteAllByIdInQuery(long gisungId);
}
