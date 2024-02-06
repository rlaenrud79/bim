package com.devo.bim.repository.spring;


import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.CoWork;
import com.devo.bim.model.entity.CoWorkSnapShot;

@Repository
public interface CoWorkRepository extends JpaRepository<CoWork, Long> {
    @EntityGraph(attributePaths = {"chatting"})
    Optional<CoWork> findByIdAndProjectId(long coWorkId, long projectId);

    @EntityGraph(attributePaths = {"coWorkSnapShots"})
    Optional<CoWorkSnapShot> findSnapShotById(long coWorkId);
}
