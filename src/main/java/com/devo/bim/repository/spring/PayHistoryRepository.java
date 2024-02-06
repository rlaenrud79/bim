package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
}
