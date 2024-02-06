package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkModeling;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoWorkModelingRepository extends JpaRepository<CoWorkModeling, Long> {

    List<CoWorkModeling> findByModelingId(long modelingId);
    List<CoWorkModeling> findByCoWorkId(long coWokrId);
    List<CoWorkModeling> findByModelingIdAndCoWorkId(long modelingId, long coWokrId);
}
