package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.entity.Account;
import com.devo.bim.repository.spring.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

public class AbstractServiceTest {
    @Autowired private AccountRepository accountRepository;
    @Resource
    private UserInfo userInfo;

    @BeforeEach
    public void setUp(){
        Account newAccount = accountRepository.findByEmail("79storm@nate.com").orElseGet(Account::new);
        userInfo.setUserInfo(newAccount,"", "");
        userInfo.setProjectId(1);
    }
}
