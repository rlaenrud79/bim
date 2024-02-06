package com.devo.bim.service;

import com.devo.bim.model.dto.JobSheetTemplateDTO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;
import com.devo.bim.model.entity.JobSheetTemplate;
import com.devo.bim.model.vo.JobSheetTemplateVO;
import com.devo.bim.repository.dsl.JobSheetTemplateDTODslRepository;
import com.devo.bim.repository.spring.JobSheetTemplateRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSheetTemplateService extends AbstractService {

    private final JobSheetTemplateRepository jobSheetTemplateRepository;
    private final JobSheetTemplateDTODslRepository jobSheetTemplateDTODslRepository;

    @Transactional
    public JsonObject postJobSheetTemplate(JobSheetTemplateVO jobSheetTemplateVO) {
        try {
            jobSheetTemplateRepository.save(new JobSheetTemplate(userInfo, jobSheetTemplateVO));

            return proc.getResult(true, "system.admin_service.post_job_sheet_template");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<JobSheetTemplateDTO> findJobSheetTemplateDTOs(SearchJobSheetTemplateVO searchJobSheetTemplateVO) {
        return jobSheetTemplateDTODslRepository.findJobSheetTemplateDTOs(searchJobSheetTemplateVO);
    }

    public JobSheetTemplate findById(long jobSheetTemplateId) {
        return jobSheetTemplateRepository.findByIdAndProjectId(jobSheetTemplateId, userInfo.getProjectId()).orElseGet(JobSheetTemplate::new);
    }

    @Transactional
    public JsonObject putJobSheetTemplate(JobSheetTemplateVO jobSheetTemplateVO) {
        JobSheetTemplate savedJobSheetTemplate = jobSheetTemplateRepository.findByIdAndProjectId(jobSheetTemplateVO.getId(), userInfo.getProjectId()).orElseGet(JobSheetTemplate::new);
        if (savedJobSheetTemplate.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_job_sheet_template");

        try {
            savedJobSheetTemplate.setJobSheetTemplateAtUpdateJobSheetTemplate(jobSheetTemplateVO.getTitle(), jobSheetTemplateVO.getContents(), jobSheetTemplateVO.isEnable());
            return proc.getResult(true, "system.admin_service.put_job_sheet_template");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
