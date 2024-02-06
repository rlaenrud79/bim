package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ConfigName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectConfigNameRepository extends JpaRepository<ConfigName, Long> {
}
