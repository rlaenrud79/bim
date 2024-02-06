package com.devo.bim.repository.jpa;

import com.devo.bim.model.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class sampleRepository {
    @Autowired  private EntityManager em;

    Account getAccount(long id){
        return em.createQuery("select a from Account a where a.id = :id", Account.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
