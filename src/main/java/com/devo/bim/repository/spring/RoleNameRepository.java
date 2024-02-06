package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleNameRepository extends JpaRepository<RoleName, Long> {
    List<RoleName> findByLanguageCode(String languageCode);
}
