package com.devo.bim.service;

import com.devo.bim.model.dto.JobSheetTemplateDTO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;
import com.devo.bim.model.entity.JobSheetTemplate;
import com.devo.bim.model.vo.JobSheetTemplateVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback
public class JobSheetTemplateServiceTest extends AbstractServiceTest {

    @Autowired
    private JobSheetTemplateService jobSheetTemplateService;

    @Test
    public void getJobSheetTemplateList_ASC() {
        SearchJobSheetTemplateVO searchJobSheetTemplateVO = new SearchJobSheetTemplateVO();
        searchJobSheetTemplateVO.setProjectId(1);
        searchJobSheetTemplateVO.setSortProp("ASC");

        List<JobSheetTemplateDTO> result = jobSheetTemplateService.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);

        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("title1");
    }


    @Test
    public void postJobSheetTemplate_test() {

        JobSheetTemplateVO jobSheetTemplateVO = new JobSheetTemplateVO();
        jobSheetTemplateVO.setTitle("제목1");
        jobSheetTemplateVO.setContents("내용1");
        jobSheetTemplateVO.setEnable(true);

        jobSheetTemplateService.postJobSheetTemplate(jobSheetTemplateVO);

    }

    @Test
    public void putJobSheetTemplate_test() {
        long jobSheetTemplateId = 2L;

        String title = "title1";
        String contents = "contents1";
        boolean enabled = false;

        JobSheetTemplateVO jobSheetTemplateVO = new JobSheetTemplateVO();
        jobSheetTemplateVO.setId(jobSheetTemplateId);
        jobSheetTemplateVO.setTitle(title);
        jobSheetTemplateVO.setContents(contents);
        jobSheetTemplateVO.setEnable(enabled);

        jobSheetTemplateService.putJobSheetTemplate(jobSheetTemplateVO);

        JobSheetTemplate savedJobSheetTemplate = jobSheetTemplateService.findById(jobSheetTemplateId);

        Assertions.assertThat(savedJobSheetTemplate.getTitle()).isEqualTo(title);
        Assertions.assertThat(savedJobSheetTemplate.getContents()).isEqualTo(contents);
        Assertions.assertThat(savedJobSheetTemplate.isEnabled()).isFalse();
    }
}
