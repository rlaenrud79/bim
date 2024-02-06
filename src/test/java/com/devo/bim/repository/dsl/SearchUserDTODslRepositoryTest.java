package com.devo.bim.repository.dsl;


import com.devo.bim.model.dto.SearchUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SearchUserDTODslRepositoryTest {

    @Autowired private SearchUserDTODslRepository searchUserDTODslRepository;

    @Test
    public void findSearchUserDTOs_Test(){

        long projectId = 1L;

        List<SearchUserDTO> result = searchUserDTODslRepository.findSearchUserDTOs(projectId);

//        Assertions.assertThat(result.get(0).getSearchUserDTO().getUserId()).isEqualTo(4);
//        Assertions.assertThat(result.get(0).getCompanyRoleNameLocale(Locale.ENGLISH)).isEqualTo("Ordering Company");
//        Assertions.assertThat(result.get(0).getCompanyRoleNameLocale(Locale.KOREAN)).isEqualTo("발주사");
//        Assertions.assertThat(result.get(0).getCompanyRoleNameLocale(Locale.CHINESE)).isEqualTo("發注社");

    }
}