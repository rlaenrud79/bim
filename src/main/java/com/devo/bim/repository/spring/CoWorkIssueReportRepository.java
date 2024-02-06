package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkIssueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoWorkIssueReportRepository extends JpaRepository<CoWorkIssueReport, Long> {
}
