package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.AccountGrantor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountGrantorRepository extends JpaRepository<AccountGrantor, Long> {

}
