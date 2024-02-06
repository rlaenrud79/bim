package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkIssueReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CoWorkIssueReportFileRepository extends JpaRepository<CoWorkIssueReportFile, Long> {

    List<CoWorkIssueReportFile> findByCoWorkIssueReportId(long coWorkIssueReportId);
}
