package com.devo.bim.repository.spring;

import com.devo.bim.model.entity.AccountReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountReferenceRepository extends JpaRepository<AccountReference, Long> {

}
