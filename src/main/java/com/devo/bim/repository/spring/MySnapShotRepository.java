package com.devo.bim.repository.spring;


import java.util.List;
import java.util.Optional;

import com.devo.bim.model.entity.JobSheet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.MySnapShot;

@Repository
public interface MySnapShotRepository extends JpaRepository<MySnapShot, Long> {
	MySnapShot findByIdAndOwnerId(long id, long ownerId);

    @Query(
            value = "select id, source from my_snap_shot where id=:id and owner_id=:ownerId",
            nativeQuery = true
    )
    MySnapShot getMySnapShotByIdAndOwnerId(long id, long ownerId);

    List<MySnapShot> findByOwnerId(long ownerId);

    List<MySnapShot> findByIdInAndOwnerId(List<Long> id, long ownerId);
    
    @EntityGraph(attributePaths = {"mySnapShotFiles"})
    Optional<MySnapShot>  findMySnapShotFileByIdAndOwnerId(long id, long ownerId);

    void deleteByOwnerIdAndId(long id, long mySnapShotId);
}
