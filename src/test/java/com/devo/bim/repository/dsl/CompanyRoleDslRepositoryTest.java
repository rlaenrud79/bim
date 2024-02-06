package com.devo.bim.repository.dsl;


import com.devo.bim.model.entity.CompanyRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CompanyRoleDslRepositoryTest {

    @Autowired private CompanyRoleDslRepository companyRoleDslRepository;

    @Test
    public void findNextCompanyRoleBySortNo_test(){
        long projectId = 1;
        int sortNo = 3;
        CompanyRole savedCompanyRole = companyRoleDslRepository.findNextCompanyRoleBySortNo(projectId, sortNo);

        Assertions.assertThat(savedCompanyRole.getSortNo()).isEqualTo(4);
    }

    @Test
    public void findPreviousCompanyRoleBySortNo_test(){
        long projectId = 1;
        int sortNo = 3;
        CompanyRole savedCompanyRole = companyRoleDslRepository.findPreviousCompanyRoleBySortNo(projectId, sortNo);

        Assertions.assertThat(savedCompanyRole.getSortNo()).isEqualTo(2);
    }
}