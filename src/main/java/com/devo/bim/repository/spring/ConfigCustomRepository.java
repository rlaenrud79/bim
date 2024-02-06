package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ConfigCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigCustomRepository extends JpaRepository<ConfigCustom, Long> {
}
