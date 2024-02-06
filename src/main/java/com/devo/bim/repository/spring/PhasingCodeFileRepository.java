package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.PhasingCodeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhasingCodeFileRepository extends JpaRepository<PhasingCodeFile, Long> {
    List<PhasingCodeFile> findAllByProjectIdAndPhasingCodeOrderByOriginFileName(long projectId, String phasingCode);
}
