package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ProcessItemLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessItemLinkRepository extends JpaRepository<ProcessItemLink, Long> {

    Optional<ProcessItemLink> findByPredecessorId(long predecessorId);

    @Modifying
    @Query(value = "update process_item_link as a \n"
            + "set predecessor_id = ( select   coalesce (id, 0) \n"
            + "                         from     process_item \n"
            + "                         where    process_id = :processInfoId \n"
            + "                         and      phasing_code = a.predecessor \n"
            + "                         order by id desc limit 1 ) \n"
            + "where a.process_item_id in ( select  coalesce (id, 0) \n"
            + "                             from    process_item \n"
            + "                             where   process_id = :processInfoId \n"
            + "                             and     row_num = :rowNum) ", nativeQuery = true)

    void updatePredecessorItemIdByPhasingCode(long processInfoId, int rowNum);

    @Modifying
    @Query(value = "update process_item_link as a \n"
            + "set predecessor_id = ( select   coalesce (id, 0) \n"
            + "                         from     process_item \n"
            + "                         where    process_id = :processInfoId \n"
            + "                         and      phasing_code = a.predecessor \n"
            + "                         order by id desc limit 1 ) \n"
            + "where a.process_item_id in ( select  coalesce (id, 0) \n"
            + "                             from    process_item \n"
            + "                             where   process_id = :processInfoId) ", nativeQuery = true)

    void updatePredecessorItemIdByPhasingCodes(Long processInfoId);


    @Modifying
    @Query(value = "insert into process_item_link(process_item_id, predecessor, predecessor_type, predecessor_value \n" +
            "                                , predecessor_duration, sort_no, predecessor_id) \n" +
            "select  (   select  id \n" +
            "            from    process_item \n" +
            "            where   process_id = :newProcessId \n" +
            "            and     gantt_sort_no = (   select  gantt_sort_no \n" +
            "                                        from    process_item \n" +
            "                                        where   process_id = :currentProcessId \n" +
            "                                        and     id = a.process_item_id)) as process_item_id \n" +
            "        , predecessor, predecessor_type, predecessor_value, predecessor_duration, sort_no \n" +
            "        , ( select  id \n" +
            "            from    process_item \n" +
            "            where   process_id = :newProcessId \n" +
            "            and     gantt_sort_no = (   select  gantt_sort_no \n" +
            "                                        from    process_item \n" +
            "                                        where   process_id = :currentProcessId \n" +
            "                                        and     id = a.predecessor_id)) as predecessor_id \n" +
            "from    process_item_link as a \n" +
            "where   a.process_item_id in (  Select id \n" +
            "                                from   process_item \n" +
            "                                where  process_id = :currentProcessId) \n" +
            "order by id desc ", nativeQuery = true)

    void insertSelectForNewVersion(long currentProcessId, long newProcessId);


}
