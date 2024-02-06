package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ConfigGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigGroupRepository extends JpaRepository<ConfigGroup, Long> {
}
