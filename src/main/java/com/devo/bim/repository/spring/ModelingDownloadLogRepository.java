package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ModelingDownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelingDownloadLogRepository extends JpaRepository<ModelingDownloadLog, Long> {

}
