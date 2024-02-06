package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.ChattingJoiner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChattingJoinerRepository extends JpaRepository<ChattingJoiner, Long> {
    Optional<ChattingJoiner> findByChattingIdAndJoinerId(long chatId, long joinerId);
}
