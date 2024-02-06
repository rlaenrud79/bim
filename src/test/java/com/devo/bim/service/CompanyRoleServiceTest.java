package com.devo.bim.service;


import com.devo.bim.model.dto.CompanyRoleDTO;
import com.devo.bim.model.entity.CompanyRole;
import com.devo.bim.model.entity.CompanyRoleName;
import com.devo.bim.model.vo.SearchCompanyRoleVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
//@Rollback(value = false)
class CompanyRoleServiceTest extends AbstractServiceTest {

    @Autowired private CompanyRoleService companyRoleService;

    @Test
    public void getCompanyRoleDtoList_size(){

        SearchCompanyRoleVO companyRoleListSearchCondition = new SearchCompanyRoleVO();
        companyRoleListSearchCondition.setProjectId(1);
        companyRoleListSearchCondition.setLang("KO");
        companyRoleListSearchCondition.setSortProp("NAME");

        List<CompanyRoleDTO> result = companyRoleService.findCompanyRoleDTOs(companyRoleListSearchCondition);

        Assertions.assertThat(result.size()).isEqualTo(5);
    }

    @Test
    public void getCompanyRoleDtoList_sorting(){

        SearchCompanyRoleVO companyRoleListSearchCondition = new SearchCompanyRoleVO();
        companyRoleListSearchCondition.setProjectId(1);
        companyRoleListSearchCondition.setLang("KO");
        companyRoleListSearchCondition.setSortProp("NAME");

        List<CompanyRoleDTO> result = companyRoleService.findCompanyRoleDTOs(companyRoleListSearchCondition);

        Assertions.assertThat(result.get(0).getRoleName()).isEqualTo("감리업체");
    }

    @Test
    public void getCompanyRoleDtoList_first_role_writer(){

        SearchCompanyRoleVO companyRoleListSearchCondition = new SearchCompanyRoleVO();
        companyRoleListSearchCondition.setProjectId(1);
        companyRoleListSearchCondition.setLang("KO");
        companyRoleListSearchCondition.setSortProp("NAME");

        List<CompanyRoleDTO> result = companyRoleService.findCompanyRoleDTOs(companyRoleListSearchCondition);

        Assertions.assertThat(result.get(0).getWriterDTO().getUserId()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getWriterDTO().getEmail()).isEqualTo("79storm@nate.com");
        Assertions.assertThat(result.get(0).getWriterDTO().getUserName()).isEqualTo("신상현");
        Assertions.assertThat(result.get(0).getWriterDTO().getCompanyId()).isEqualTo(1);
    }

    @Test
    public void postCompanyRoleAndCompanyRoleName_test(){

        int sortNo = 5;
        String engRoleName = "TEST";

        CompanyRoleName companyRoleName_1 = new CompanyRoleName("EN", "Role_Test");
        CompanyRoleName companyRoleName_2 = new CompanyRoleName("KO", "Role_테스트");
        CompanyRoleName companyRoleName_3 = new CompanyRoleName("ZH", "Role_测试");

        List<CompanyRoleName> companyRoleNames = new ArrayList<>();
        companyRoleNames.add(companyRoleName_1);
        companyRoleNames.add(companyRoleName_2);
        companyRoleNames.add(companyRoleName_3);

        com.devo.bim.model.vo.CompanyRoleVO companyRoleVO = new com.devo.bim.model.vo.CompanyRoleVO();
        companyRoleVO.setSortNo(sortNo);
        companyRoleVO.setCompanyRoleNames(companyRoleNames);

        companyRoleService.postCompanyRoleAndCompanyRoleName(companyRoleVO);

        CompanyRole savedCompanyRole = companyRoleService.findBySortNo(sortNo);

        Assertions.assertThat(savedCompanyRole.getName()).isEqualTo("TEST");
        for (CompanyRoleName companyRoleName : savedCompanyRole.getCompanyRoleNames()) {
            if("EN".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_Test");
            if("KO".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_테스트");
            if("ZH".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_测试");
        }
    }

    @Test
    public void putCompanyRoleAndCompanyRoleName_minusOneUpdateCompanyRole_test(){

        int companyRoleId = 2;
        int sortNo = 4;

        String engRoleName = "TEST_UPDATE";

        CompanyRoleName companyRoleName_1 = new CompanyRoleName("EN", "Role_Test_Update");
        CompanyRoleName companyRoleName_2 = new CompanyRoleName("KO", "Role_테스트_Update");
        CompanyRoleName companyRoleName_3 = new CompanyRoleName("ZH", "Role_测试_Update");

        List<CompanyRoleName> companyRoleNames = new ArrayList<>();
        companyRoleNames.add(companyRoleName_1);
        companyRoleNames.add(companyRoleName_2);
        companyRoleNames.add(companyRoleName_3);

        com.devo.bim.model.vo.CompanyRoleVO companyRoleVO = new com.devo.bim.model.vo.CompanyRoleVO();
        companyRoleVO.setSortNo(sortNo);
        companyRoleVO.setCompanyRoleNames(companyRoleNames);

        companyRoleService.putCompanyRoleAndCompanyRoleName(companyRoleVO);

        CompanyRole savedCompanyRole1 = companyRoleService.findById(companyRoleId);

        Assertions.assertThat(savedCompanyRole1.getName()).isEqualTo("TEST_UPDATE");
        Assertions.assertThat(savedCompanyRole1.getSortNo()).isEqualTo(4);
        for (CompanyRoleName companyRoleName : savedCompanyRole1.getCompanyRoleNames()) {
            if("EN".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_Test_Update");
            if("KO".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_테스트_Update");
            if("ZH".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_测试_Update");
        }

        CompanyRole savedCompanyRole2 = companyRoleService.findBySortNo(2);
        Assertions.assertThat(savedCompanyRole2.getName()).isEqualTo("감리업체");
    }

    @Test
    public void postCompanyRoleAndCompanyRoleName_addOneUpdateCompanyRole_test(){

        int companyRoleId = 4;
        int sortNo = 2;

        String engRoleName = "TEST_Update";

        CompanyRoleName companyRoleName_1 = new CompanyRoleName("EN", "Role_Test_Update");
        CompanyRoleName companyRoleName_2 = new CompanyRoleName("KO", "Role_테스트_Update");
        CompanyRoleName companyRoleName_3 = new CompanyRoleName("ZH", "Role_测试_Update");

        List<CompanyRoleName> companyRoleNames = new ArrayList<>();
        companyRoleNames.add(companyRoleName_1);
        companyRoleNames.add(companyRoleName_2);
        companyRoleNames.add(companyRoleName_3);

        com.devo.bim.model.vo.CompanyRoleVO companyRoleVO = new com.devo.bim.model.vo.CompanyRoleVO();
        companyRoleVO.setSortNo(sortNo);
        companyRoleVO.setCompanyRoleNames(companyRoleNames);
        companyRoleService.postCompanyRoleAndCompanyRoleName(companyRoleVO);

        CompanyRole savedCompanyRole1 = companyRoleService.findBySortNo(sortNo);

        Assertions.assertThat(savedCompanyRole1.getName()).isEqualTo("TEST_Update");
        for (CompanyRoleName companyRoleName : savedCompanyRole1.getCompanyRoleNames()) {
            if("EN".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_Test_Update");
            if("KO".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_테스트_Update");
            if("ZH".equals(companyRoleName.getLanguageCode())) Assertions.assertThat(companyRoleName.getName()).isEqualTo("Role_测试_Update");
        }

        CompanyRole savedCompanyRole2 = companyRoleService.findBySortNo(4);
        Assertions.assertThat(savedCompanyRole2.getName()).isEqualTo("감리업체");
    }

    @Test
    public void setEnabledCompanyRole_OFF_ON(){

        int companyRoleId = 1;

        companyRoleService.putEnabledCompanyRole(companyRoleId, "OFF");

        CompanyRole savedCompanyRole = companyRoleService.findById(companyRoleId);
        Assertions.assertThat(savedCompanyRole.isEnabled()).isEqualTo(false);

        companyRoleService.putEnabledCompanyRole(companyRoleId, "ON");
        CompanyRole savedCompanyRole2 = companyRoleService.findById(companyRoleId);
        Assertions.assertThat(savedCompanyRole2.isEnabled()).isEqualTo(true);
    }
}