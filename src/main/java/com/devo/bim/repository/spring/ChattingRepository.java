package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    Optional<Chatting> findByCoWorkId(long coWorkId);
}
