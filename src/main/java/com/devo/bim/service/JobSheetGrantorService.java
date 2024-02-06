package com.devo.bim.service;

import com.devo.bim.model.entity.JobSheetGrantor;
import com.devo.bim.model.vo.JobSheetGrantorVO;
import com.devo.bim.repository.spring.JobSheetGrantorRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobSheetGrantorService extends AbstractService {

    private final JobSheetGrantorRepository jobSheetGrantorRepository;

    public JobSheetGrantor findById(long id) {
        return jobSheetGrantorRepository.findById(id).orElseGet(JobSheetGrantor::new);
    }

    @Transactional
    public JsonObject deleteJobSheetReply(JobSheetGrantorVO jobSheetGrantorVO) {
        try {
            JobSheetGrantor savedJobSheetGrantor = jobSheetGrantorRepository.findByIdAndJobSheetId(jobSheetGrantorVO.getId(), jobSheetGrantorVO.getJobSheetId()).orElseGet(JobSheetGrantor::new);
            if (savedJobSheetGrantor.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_grantor");

            savedJobSheetGrantor.setJobSheetGrantorAtDeleteJobSheetReply();

            return proc.getResult(true,"system.job_sheet_service.delete_job_sheet_reply");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
