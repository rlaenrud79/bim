package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ChattingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingLogRepository extends JpaRepository<ChattingLog, Long> {
}
