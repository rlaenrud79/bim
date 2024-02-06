package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.IssueManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueManagerRepository extends JpaRepository<IssueManager, Long> {
}
