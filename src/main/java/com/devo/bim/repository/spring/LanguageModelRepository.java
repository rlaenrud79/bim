package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageModelRepository extends JpaRepository<LanguageModel, Long> {
}
