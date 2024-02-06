package com.devo.bim;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.QAccount;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.devo.bim.model.entity.QAccount.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryDslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads(){
        JPAQueryFactory query = new JPAQueryFactory(em);

        QAccount qAccount = account;

        Account result = query.select(qAccount)
                .from(qAccount)
                .where(qAccount.email.eq("79storm@nate.com"))
                .fetchOne();

        assertThat(result.getEmail()).isEqualTo("79storm@nate.com");

    }


}
