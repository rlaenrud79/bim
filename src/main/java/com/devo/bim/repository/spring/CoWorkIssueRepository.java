package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkIssue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoWorkIssueRepository extends JpaRepository<CoWorkIssue, Long> {
    @EntityGraph(attributePaths = {"coWork","coWork.coWorkIssues"})
    Optional<CoWorkIssue> findCoWorkById(long id);

    List<CoWorkIssue> findByCoWorkId(long coWorkId);
}
