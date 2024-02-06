package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByProjectId(long projectId);

    Optional<Company> findByIdAndProjectId(long companyId, long projectId);
}
