package com.devo.bim.repository.spring;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.CoWorkSnapShot;

@Repository
public interface CoWorkSnapShotRepository extends JpaRepository<CoWorkSnapShot, Long> {

    @EntityGraph(attributePaths = {"coWork", "coWorkSnapShotFiles"})
    Optional<CoWorkSnapShot> findCoWorkSnapShotFileById(long id);
    
    List<CoWorkSnapShot> findByCoWorkId(long coWorkId);
}
