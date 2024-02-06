package com.devo.bim.repository.spring;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.MySnapShotFile;

@Repository
public interface MySnapShotFileRepository extends JpaRepository<MySnapShotFile, Long> {

    List<MySnapShotFile> findByMySnapShotId(long mySnapShotId);
}
