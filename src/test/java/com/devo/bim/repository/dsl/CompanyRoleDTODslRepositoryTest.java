package com.devo.bim.repository.dsl;


import com.devo.bim.model.dto.CompanyRoleDTO;
import com.devo.bim.model.vo.SearchCompanyRoleVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CompanyRoleDTODslRepositoryTest {

    @Autowired private CompanyRoleDTODslRepository companyRoleDtoDslRepository;

    @Test
    public void company_role_list(){

        SearchCompanyRoleVO companyRoleListSearchCondition = new SearchCompanyRoleVO();
        companyRoleListSearchCondition.setProjectId(1);
        companyRoleListSearchCondition.setLang("KO");
        companyRoleListSearchCondition.setSortProp("NAME");

        List<CompanyRoleDTO> result = companyRoleDtoDslRepository.findCompanyRoleDTOs(companyRoleListSearchCondition);

        Assertions.assertThat(result.get(0).getRoleName()).isEqualTo("감리업체");
        Assertions.assertThat(result.get(0).getWriterDTO().getUserId()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getWriterDTO().getEmail()).isEqualTo("79storm@nate.com");
        Assertions.assertThat(result.get(0).getWriterDTO().getUserName()).isEqualTo("신상현");
        Assertions.assertThat(result.get(0).getWriterDTO().getCompanyId()).isEqualTo(1);
    }
}