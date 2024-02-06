package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    @EntityGraph(attributePaths = {"accountViewPoints"})
    Account findAccountViewPointById(long id);

    @EntityGraph(attributePaths = {"mySnapShots"})
    Account findMySnapShotById(long id);
    
    Optional<Account> findByCompanyIdAndResponsible(long companyId, boolean responsible);

    List<Account> findByCompanyId(long companyId);
}
