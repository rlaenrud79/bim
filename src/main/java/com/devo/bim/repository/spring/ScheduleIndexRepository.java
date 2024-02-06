package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ScheduleIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleIndexRepository extends JpaRepository<ScheduleIndex, Long> {

}
