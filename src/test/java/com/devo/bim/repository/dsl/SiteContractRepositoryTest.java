package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.SiteContract;
import com.devo.bim.repository.spring.SiteContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class SiteContractRepositoryTest {
    @Autowired
    SiteContractRepository siteContractRepository;

    @Test
    void siteContract(){
        List<SiteContract> list = siteContractRepository.findAllBy(7L);
        for(SiteContract dto : list ){
            System.out.println(dto.getId() + ":" + dto.getSiteStatus() + ":" + dto.getSiteSum());
        }
    }
}
