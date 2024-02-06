package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.LastSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LastSessionRepository extends JpaRepository<LastSession, Long> {

    Optional<LastSession> findByAccountIdAndProjectId(long accountId, long projectId);
}
