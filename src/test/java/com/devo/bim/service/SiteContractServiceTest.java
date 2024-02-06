package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.entity.SiteContract;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class SiteContractServiceTest {
    @Autowired
    private SiteContractService siteContractService;
    @Autowired
    private UserInfo userInfo;

    @Test
    void findAllBy(){
        List<SiteContract> lst = siteContractService.findAllByProjectId(7L);
        for(SiteContract s : lst){
            System.out.println(s.getId() + ":" + s.getProjectId() + ":" + s.getSiteStatus() + ":" + s.getSiteSum());
        }
    }

    @Test
    void findAllByUserInfo(){
        userInfo.setProjectId(7);
        JsonObject obj = siteContractService.findAllByProjectId();
        System.out.println(obj.toString());
    }
}
