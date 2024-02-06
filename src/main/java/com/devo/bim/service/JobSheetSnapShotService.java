package com.devo.bim.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devo.bim.model.entity.JobSheetSnapShot;
import com.devo.bim.repository.spring.JobSheetSnapShotRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobSheetSnapShotService extends AbstractService {
    private final JobSheetSnapShotRepository jobSheetSnapShotRepository;

    public JobSheetSnapShot findById(long id) {
        return jobSheetSnapShotRepository.findById(id).orElseGet(JobSheetSnapShot::new);
    }

    @Transactional
    public JsonObject deleteJobSheetSnapShot(long id) {
        JobSheetSnapShot savedJobSheetSnapShot = jobSheetSnapShotRepository.findById(id).orElseGet(JobSheetSnapShot::new);

        if (savedJobSheetSnapShot.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_snap_shot");

        try {
            jobSheetSnapShotRepository.delete(savedJobSheetSnapShot);

            return proc.getResult(true, "system.job_sheet_service.delete_job_sheet_snap_shot");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
