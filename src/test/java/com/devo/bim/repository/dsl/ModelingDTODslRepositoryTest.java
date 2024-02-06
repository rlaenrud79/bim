package com.devo.bim.repository.dsl;


import com.devo.bim.model.dto.ModelingDTO;
import com.devo.bim.model.vo.SearchModelingVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ModelingDTODslRepositoryTest {

    @Autowired private ModelingDTODslRepository modelingDTODslRepository;

    @Test
    public void modeling_dto_list(){

        SearchModelingVO searchModelingVO = new SearchModelingVO();
        searchModelingVO.setProjectId(1);
        searchModelingVO.setSearchLatest(false);

        searchModelingVO.setSortProp("FILE_NAME");

        BigDecimal fileSize = new BigDecimal("15204");

        List<ModelingDTO> result = modelingDTODslRepository.findModelingDTOs(searchModelingVO);

        Assertions.assertThat(result.size()).isEqualTo(5);
        Assertions.assertThat(result.get(0).getFileName()).isEqualTo("aaaaaaaaaaaaaaaaa.rvt");
        Assertions.assertThat(result.get(0).getFileSize()).isEqualTo(fileSize);
        Assertions.assertThat(result.get(0).getWork().getName()).isEqualTo("배관 공종");
        Assertions.assertThat(result.get(0).getWriterDTO().getUserId()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getWriterDTO().getEmail()).isEqualTo("79storm@nate.com");
        Assertions.assertThat(result.get(0).getWriterDTO().getUserName()).isEqualTo("신상현");
        Assertions.assertThat(result.get(0).getWriterDTO().getCompanyId()).isEqualTo(1);
    }
}