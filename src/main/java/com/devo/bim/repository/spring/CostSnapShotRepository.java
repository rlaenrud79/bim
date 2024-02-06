package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CostSnapShot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Native;
import java.util.Optional;

@Repository
public interface CostSnapShotRepository extends JpaRepository<CostSnapShot, Long> {
    Optional<CostSnapShot> findByIdAndCostId(long id, long costSnapshotId);

    @Query(value="select * from cost_snap_shot a inner join cost b on a.cost_id = b.id where b.project_id=:projectId order by a.last_modify_date desc limit 1", nativeQuery = true)
    Optional<CostSnapShot> getLatestCostSnapshot(long projectId);
}
