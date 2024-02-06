package com.devo.bim.repository.spring;


import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.CoWorkSnapShotFile;

@Repository
public interface CoWorkSnapShotFileRepository extends JpaRepository<CoWorkSnapShotFile, Long> {
    List<CoWorkSnapShotFile> findByCoWorkSnapShotId(long id);

    @EntityGraph(attributePaths = {"coWorkSnapShot"})
    CoWorkSnapShotFile findCoWorkSnapShotById(long id);
    
}
