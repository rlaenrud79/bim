package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.WorkName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkNameRepository extends JpaRepository<WorkName, Long> {
}
