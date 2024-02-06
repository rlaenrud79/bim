package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    Optional<Bulletin> findByIdAndProjectId(long id, long projectId);
}
