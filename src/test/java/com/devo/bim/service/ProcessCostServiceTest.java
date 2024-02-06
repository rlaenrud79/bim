package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.ProcessItemCostDTO;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class ProcessCostServiceTest {

    @Autowired
    ProcessCostService processCostService;

    @Resource
    private UserInfo userInfo;

    @Test
    void findProcessItemCostCurrentVersionTest(){
        SearchProcessItemCostVO searchProcessItemCostVO = new SearchProcessItemCostVO();
        searchProcessItemCostVO.setPhasingCode("TR200440");
        userInfo.setProjectId(30L);
        JsonObject jsonObject = processCostService.findProcessItemCostCurrentVersion(searchProcessItemCostVO);
        System.out.println(jsonObject.toString());
    }
}
