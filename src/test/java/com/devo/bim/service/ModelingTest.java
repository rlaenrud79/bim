package com.devo.bim.service;

import com.devo.bim.model.dto.ModelingAssemblyDTO;
import com.devo.bim.model.dto.ModelingAttributeDTO;
import com.devo.bim.model.entity.ModelingAssembly;
import com.devo.bim.repository.spring.ModelingAssemblyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
@ActiveProfiles("test")
@SpringBootTest
public class ModelingTest {
    @Autowired
    private ModelingAssemblyRepository modelingAssemblyRepository;
    @Autowired
    private ModelingService modelingService;

    @Test
    void findByExchangeIdRepositoyTest(){
        String exchangeId = "교량받침 - 4300kN,H=190mm(양방향).dffaf816e672bb7ff2202f068f39614da02b9410";
        ModelingAssembly result = modelingAssemblyRepository.findByExchangeId(exchangeId);
        System.out.println("repository:" + result.getId());
    }
    @Test
    void findByExchangeIdServiceTest(){
        String exchangeId = "교량받침 - 4300kN,H=190mm(양방향).dffaf816e672bb7ff2202f068f39614da02b9410";
        ModelingAssemblyDTO result = modelingService.getMoelingAssemblyFindByExchangeId(exchangeId);
        System.out.println("service:" + result.getId());
    }
    @Test
    void findByExchangeIdBlankTest(){
        String exchangeId = "NOT_EXIST_EXCHANGE_ID";
        ModelingAssemblyDTO dto = modelingService.getMoelingAssemblyFindByExchangeId(exchangeId);
        System.out.println("service:" + dto.getId());
        Assertions.assertThat(dto.getId()).isEqualTo(0);
    }
    @Test
    void getModelingAttributesFindByModelingAssemblyAndUserAttr(){
        long modelingAssemblyId = 33111;
        List<ModelingAttributeDTO> attributeList = modelingService.getModelingAttributes(modelingAssemblyId, true);
        for(ModelingAttributeDTO dto : attributeList){
            System.out.println("dto.id:" + dto.getId() + ":" + dto.getAttributeName() + ":" + dto.getAttributeValue());
        }
        System.out.println("cnt:" + attributeList.size());
        //Assertions.assertThat(attributeList.size()).isEqualTo(3);
    }
    @Test
    void updateTest(){
        //{"modelingAssembly":{"id":0}, "attributeName":"", "attributeType":"String", "attributeValue":"", "userAttr":true};
        ModelingAttributeDTO dto = new ModelingAttributeDTO();
        dto.setId(403924);
        dto.setUserAttr(true);
        dto.setAttributeName("HELLO_NAME");
        dto.setAttributeType("String");
        dto.setAttributeValue("HELLO");
        dto.setModelingAssembly(new ModelingAssembly(33111));
        modelingService.putUserProperty(dto);
        System.out.println("SUCCESS");
    }
}
