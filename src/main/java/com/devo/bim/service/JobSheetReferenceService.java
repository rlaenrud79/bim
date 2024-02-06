package com.devo.bim.service;

import com.devo.bim.model.entity.JobSheetReference;
import com.devo.bim.repository.spring.JobSheetReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSheetReferenceService extends AbstractService {

    private final JobSheetReferenceRepository jobSheetReferenceRepository;

    public List<JobSheetReference> findById(long id) {
        return jobSheetReferenceRepository.findByJobSheetId(id);
    }

}
