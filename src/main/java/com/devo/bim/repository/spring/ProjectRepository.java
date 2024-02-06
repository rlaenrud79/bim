package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @EntityGraph(attributePaths = {"companies.companyRole"})
    Optional<Project> findMyProjectById(long id);
}
