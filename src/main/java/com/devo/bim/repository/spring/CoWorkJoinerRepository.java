package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CoWorkJoiner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoWorkJoinerRepository extends JpaRepository<CoWorkJoiner, Long> {
    List<CoWorkJoiner> findByCoWorkId(long coWorkId);
}
