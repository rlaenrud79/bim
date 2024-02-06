package com.devo.bim.service;

import com.devo.bim.model.dto.JobSheetProcessItemDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.WorkAmountDTO;
import com.devo.bim.model.entity.JobSheetProcessItem;
import com.devo.bim.model.entity.WorkAmount;
import com.devo.bim.model.vo.SearchWorkAmountVO;
import com.devo.bim.model.vo.WorkAmountVO;
import com.devo.bim.repository.dsl.WorkAmountDTODslRepository;
import com.devo.bim.repository.spring.WorkAmountRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkAmountService extends AbstractService {
    private final WorkAmountRepository workAmountRepository;

    private final WorkAmountDTODslRepository workAmountDTODslRepository;

    public PageDTO<WorkAmountDTO> findWorkAmountDTOs(SearchWorkAmountVO searchWorkAmountVO, Pageable pageable) {
        return workAmountDTODslRepository.findWorkAmountDTOs(searchWorkAmountVO, pageable);
    }

    @Transactional
    public JsonObject postWorkAmount(WorkAmountVO workAmountVO) {
        try {
            WorkAmount workAmount = workAmountRepository.findByWorkIdAndYear(workAmountVO.getWorkId(), workAmountVO.getYear()).orElseGet(WorkAmount::new);
            if (workAmount != null && workAmount.getId() > 0) {
                workAmount.setWorkAmountAtUpdateWorkAmount(workAmountVO, userInfo);
                workAmountRepository.save(workAmount);
            } else {
                workAmount.setWorkAmountAtAddWorkAmount(workAmountVO, userInfo);
                workAmountRepository.save(workAmount);
            }

            return proc.getResult(true, workAmount.getId(), "system.work_amount_service.post_work_amount");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putWorkAmount(WorkAmountVO workAmountVO) {
        WorkAmount savedWorkAmount = workAmountRepository.findById(workAmountVO.getId()).orElseGet(WorkAmount::new);
        if (savedWorkAmount.getId() == 0) {
            return proc.getResult(false, "system.work_amount_service.not_exist_work_amount");
        }

        try {
            savedWorkAmount.setWorkAmountAtUpdateWorkAmount(workAmountVO, userInfo);
            return proc.getResult(true, "system.work_amount_service.put_work_amount");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public WorkAmount findByIdAndProjectId(long id, long projectId) {
        return workAmountRepository.findByIdAndProjectId(id, projectId).orElseGet(WorkAmount::new);
    }

    public JsonObject deleteWorkAmount(long id) {
        WorkAmount savedWorkAmount = workAmountRepository.findById(id).orElseGet(WorkAmount::new);
        if (savedWorkAmount.getId() == 0) {
            return proc.getResult(false, "system.work_amount_service.not_exist_work_amount");
        }

        try {
            workAmountRepository.delete(savedWorkAmount);

            return proc.getResult(true, "system.work_amount_service.delete_work_amount");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<WorkAmountDTO> findAllWorkAmountListForWork(long projectId, String languageCode, String year, long jobSheetId) {
        return workAmountDTODslRepository.findAllWorkAmountListForWork(projectId, languageCode, year, jobSheetId);
    }

    public WorkAmountDTO findSumWorkAmountListForYear(long projectId, String year, long jobSheetId, int workId) {
        return workAmountDTODslRepository.findSumWorkAmountListForYear(projectId, year, jobSheetId, workId);
    }

    public List<WorkAmountDTO> findAllWorkAmountList(long projectId, String languageCode) {
        return workAmountDTODslRepository.findAllWorkAmountList(projectId, languageCode);
    }

    public WorkAmountDTO findSumWorkAmountList(long projectId) {
        return workAmountDTODslRepository.findSumWorkAmountList(projectId);
    }

    public List<JobSheetProcessItemDTO> findSumJobSheetProcessItemListForYear(long workId, long jobSheetId, String reportDate) {
        return workAmountDTODslRepository.findSumJobSheetProcessItemListForYear(workId, jobSheetId, reportDate);
    }

    public BigDecimal getWorkAmountByAmountSum(long projectId, String year) {
        return workAmountRepository.getWorkAmountByAmountSum(projectId, year).orElse(BigDecimal.ZERO);
    }
}
