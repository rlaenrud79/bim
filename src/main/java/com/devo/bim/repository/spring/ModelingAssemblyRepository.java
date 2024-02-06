package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ModelingAssembly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelingAssemblyRepository extends JpaRepository<ModelingAssembly, Long> {
    ModelingAssembly findByExchangeId(String exchangeId);
}
