package com.devo.bim.service;

import com.devo.bim.model.entity.JobSheetFile;
import com.devo.bim.repository.spring.JobSheetFileRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobSheetFileService extends AbstractService {
    private final JobSheetFileRepository jobSheetFileRepository;
    private final FileDeleteService fileDeleteService;

    @Transactional
    public JsonObject deleteJobSheetFile(long id) {
        JobSheetFile savedJobSheetFile = jobSheetFileRepository.findById(id).orElseGet(JobSheetFile::new);

        if(savedJobSheetFile.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_file");

        try{
            fileDeleteService.deletePhysicalFile(savedJobSheetFile.getFilePath());
            jobSheetFileRepository.delete(savedJobSheetFile);

            return proc.getResult(true, "system.job_sheet_service.delete_job_sheet_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

}
