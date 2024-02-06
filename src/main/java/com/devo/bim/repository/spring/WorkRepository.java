package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    Optional<Work> findByIdAndProjectId(long workId, long projectId);
    Optional<Work> findByProjectIdAndSortNo(long projectId, int sortNo);

    @Modifying
    @Query(value = "update work set sort_no = sort_no + 1 where project_id = :projectId and sort_no >= :sortNo", nativeQuery = true)
    void addOneGOESortNo(int sortNo, long projectId);

    @Modifying
    @Query(value = "update work set sort_no = sort_no - 1 where project_id = :projectId and sort_no > :savedSortNo and sort_no <= :newSortNo ", nativeQuery = true)
    void minusOneGTSavedSOrtNoAndLOESortNo(long projectId, int savedSortNo, int newSortNo);

    @Modifying
    @Query(value = "update work set sort_no = sort_no + 1 where project_id = :projectId and sort_no < :savedSortNo and sort_no >= :newSortNo", nativeQuery = true)
    void addOneLTSavedSortNoAndGOESortNo(long projectId, int savedSortNo, int newSortNo);

    @Modifying
    @Query(value = "insert into schedule_work (work_id, schedule_id) select :workId, id from schedule where type='HOLIDAY' and is_all_work = true order by id asc", nativeQuery = true)
    void addNewWorkSchedule(long workId);

    @Query(value = "select COALESCE(max(sort_no), 0) from work", nativeQuery = true)
    Optional<Integer> getMaxWorkSortNo();
}
