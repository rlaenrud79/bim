package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.IssueReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueReportFileRepository extends JpaRepository<IssueReportFile, Long> {
    List<IssueReportFile> findByIssueReportId(long id);
}
