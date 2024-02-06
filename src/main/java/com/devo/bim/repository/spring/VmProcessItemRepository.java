package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.VmProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmProcessItemRepository extends JpaRepository<VmProcessItem, Long> {
    List<VmProcessItem> findById(long id);
}
