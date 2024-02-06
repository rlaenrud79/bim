package com.devo.bim.service;

import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.WorkAmountDTO;
import com.devo.bim.model.dto.WorkPlanDTO;
import com.devo.bim.model.entity.WorkAmount;
import com.devo.bim.model.entity.WorkPlan;
import com.devo.bim.model.vo.SearchWorkPlanVO;
import com.devo.bim.model.vo.WorkAmountVO;
import com.devo.bim.model.vo.WorkPlanVO;
import com.devo.bim.repository.dsl.WorkPlanDTODslRepository;
import com.devo.bim.repository.spring.WorkPlanRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkPlanService extends AbstractService {
    private final WorkPlanRepository workPlanRepository;

    private final WorkPlanDTODslRepository workPlanDTODslRepository;

    public PageDTO<WorkPlanDTO> findWorkPlanDTOs(SearchWorkPlanVO searchWorkPlanVO, Pageable pageable) {
        return workPlanDTODslRepository.findWorkPlanDTOs(searchWorkPlanVO, pageable);
    }

    @Transactional
    public JsonObject postWorkPlan(WorkPlanVO workPlanVO) {
        try {
            WorkPlan workPlan = workPlanRepository.findByWorkIdAndYearAndMonth(workPlanVO.getWorkId(), workPlanVO.getYear(), workPlanVO.getMonth()).orElseGet(WorkPlan::new);
            if (workPlan != null && workPlan.getId() > 0) {
                workPlan.setWorkPlanAtUpdateWorkPlan(workPlanVO, userInfo);
                workPlanRepository.save(workPlan);
            } else {
                workPlan.setWorkPlanAtAddWorkPlan(workPlanVO, userInfo);
                workPlanRepository.save(workPlan);
            }

            return proc.getResult(true, workPlan.getId(), "system.work_plan_service.post_work_plan");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putWorkPlan(WorkPlanVO workPlanVO) {
        WorkPlan savedWorkPlan = workPlanRepository.findById(workPlanVO.getId()).orElseGet(WorkPlan::new);
        if (savedWorkPlan.getId() == 0) {
            return proc.getResult(false, "system.work_plan_service.not_exist_work_plan");
        }

        try {
            savedWorkPlan.setWorkPlanAtUpdateWorkPlan(workPlanVO, userInfo);
            return proc.getResult(true, "system.work_plan_service.put_work_plan");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public WorkPlan findByIdAndProjectId(long id, long projectId) {
        return workPlanRepository.findByIdAndProjectId(id, projectId).orElseGet(WorkPlan::new);
    }

    public JsonObject deleteWorkPlan(long id) {
        WorkPlan savedWorkPlan = workPlanRepository.findById(id).orElseGet(WorkPlan::new);
        if (savedWorkPlan.getId() == 0) {
            return proc.getResult(false, "system.work_plan_service.not_exist_work_plan");
        }

        try {
            workPlanRepository.delete(savedWorkPlan);

            return proc.getResult(true, "system.work_plan_service.delete_work_plan");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<WorkPlanDTO> findAllWorkPlanListForWork(long projectId, long workId, String reportDate) {
        return workPlanDTODslRepository.findAllWorkPlanListForWork(projectId, workId, reportDate);
    }

    public List<WorkPlanDTO> findAllWorkPlanListForWorkSum(long projectId, long workId) {
        return workPlanDTODslRepository.findAllWorkPlanListForWorkSum(projectId, workId);
    }

    public BigDecimal getWorkPlanByMonthRateSum(String year, String month) {
        return workPlanRepository.getWorkPlanByMonthRateSum(year, month).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getWorkPlanByDayRateSum(String year, String month) {
        return workPlanRepository.getWorkPlanByDayRateSum(year, month).orElse(BigDecimal.ZERO);
    }
}
