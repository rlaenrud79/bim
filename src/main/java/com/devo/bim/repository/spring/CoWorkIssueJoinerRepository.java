package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkIssueJoiner;
import com.devo.bim.model.entity.CoWorkJoiner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoWorkIssueJoinerRepository extends JpaRepository<CoWorkIssueJoiner, Long> {
    void deleteByCoWorkIssueId(long coWorkIssueId);
}
