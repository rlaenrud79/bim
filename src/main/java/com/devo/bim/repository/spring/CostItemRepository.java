package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.CostItem;
import com.devo.bim.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostItemRepository extends JpaRepository<CostItem, Long> {
}
