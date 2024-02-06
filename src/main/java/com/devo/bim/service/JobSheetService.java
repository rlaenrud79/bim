package com.devo.bim.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Calendar;
import java.util.stream.Collectors;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.repository.dsl.JobSheetProcessItemDTODslRepository;
import com.devo.bim.repository.dsl.ViewJobSheetProcessItemDTODslRepository;
import com.devo.bim.repository.spring.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.model.vo.JobSheetGrantorVO;
import com.devo.bim.model.vo.JobSheetVO;
import com.devo.bim.model.vo.SearchJobSheetVO;
import com.devo.bim.repository.dsl.JobSheetDTODslRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;

@Service
@RequiredArgsConstructor
public class JobSheetService extends AbstractService {

    private final JobSheetRepository jobSheetRepository;
    private final JobSheetGrantorRepository jobSheetGrantorRepository;
    private final JobSheetProcessItemRepository jobSheetProcessItemRepository;
    private final ViewJobSheetProcessItemRepository viewJobSheetProcessItemRepository;
    private final JobSheetProcessItemDTODslRepository jobSheetProcessItemDTODslRepository;
    private final ViewJobSheetProcessItemDTODslRepository viewJobSheetProcessItemDTODslRepository;
    private final JobSheetProcessItemWorkerRepository jobSheetProcessItemWorkerRepository;
    private final JobSheetProcessItemDeviceRepository jobSheetProcessItemDeviceRepository;
    private final ProcessWorkerRepository processWorkerRepository;
    private final ProcessDeviceRepository processDeviceRepository;
    private final ProcessInfoRepository processInfoRepository;
    private final JobSheetReferenceRepository jobSheetReferenceRepository;
    private final MySnapShotRepository mySnapShotRepository;
    private final JobSheetSnapShotRepository jobSheetSnapShotRepository;
    private final JobSheetDTODslRepository jobSheetDTODslRepository;
    private final JobSheetFileRepository jobSheetFileRepository;
    private final ProcessItemRepository processItemRepository;
    private final SelectProgressConfigRepository selectProgressConfigRepository;
    private final AlertService alertService;

    @Transactional
    public JsonObject postJobSheet(JobSheetVO jobSheetVO) {
        try {
            JobSheet savedJobSheet = addJobSheet(jobSheetVO);

            // 작업일지 상시일 경우
            if (jobSheetVO.getStatus() != null && (jobSheetVO.getStatus().equals("GOING") || jobSheetVO.getStatus().equals("COMPLETE"))) {
                // 공정 누적 진행률이 100%를 오버하는지 체크
                for (int i = 0; i < jobSheetVO.getProcessItemIds().size(); i++) {
                    JobSheetProcessItem jobSheetProcessItem = new JobSheetProcessItem();
                    ProcessItem processItem = processItemRepository.getById(jobSheetVO.getProcessItemIds().get(i));

                    // progressRate 진행률 합계
                    BigDecimal progressRate = processItem.getProgressRate();
                    BigDecimal progressRateSum = progressRate.add(jobSheetVO.getTodayProgressRates().get(i));
                    if (progressRateSum.compareTo(new BigDecimal("100")) > 0)
                        return proc.getResult(false, "system.gisung_service.task_progress_rate_over_100");
                }
            }

            addJobSheetGrantor(jobSheetVO, savedJobSheet);
            addJobSheetProcessItem(jobSheetVO, savedJobSheet);
            addJobSheetReferences(jobSheetVO, savedJobSheet);
            //addJobSheetSnapShot(jobSheetVO, savedJobSheet);

            List<JobSheetProcessItem> jobSheetProcessItems = findJobSheetProcessItemByJobSheetId(savedJobSheet.getId());
            saveProcessItem(jobSheetProcessItems);
            saveProcessWorker(savedJobSheet);
            saveProcessDevice(savedJobSheet);
            saveAlert(jobSheetVO, savedJobSheet, "INSERT");

            if (jobSheetVO.getStatus() != null && (jobSheetVO.getStatus().equals("GOING") || jobSheetVO.getStatus().equals("COMPLETE"))) {
                return proc.getResult(true, savedJobSheet.getId(), "system.job_sheet_service.sangsin_job_sheet");
            } else {
                return proc.getResult(true, savedJobSheet.getId(), "system.job_sheet_service.post_job_sheet");
            }
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void saveAlert(JobSheetVO jobSheetVO, JobSheet savedJobSheet, String actionType) {
        // grantor
        alertService.insertAlertForJobSheet(userInfo.getProjectId(), jobSheetVO.getGrantorId(), savedJobSheet, actionType);

        // reference
        if (jobSheetVO.getReferencesIds().size() > 0) {
            List<Long> targetReferenceIds = jobSheetVO.getReferencesIds().stream().filter(t -> !t.equals(jobSheetVO.getGrantorId())).collect(Collectors.toList());

            for (Long referenceId : targetReferenceIds) {
                alertService.insertAlertForJobSheet(userInfo.getProjectId(), referenceId, savedJobSheet, actionType);
            }
        }
    }

    private JobSheet addJobSheet(JobSheetVO jobSheetVO) {
        JobSheet jobSheet = new JobSheet();
        jobSheet.setJobSheetAtAddJobSheet(userInfo, jobSheetVO);
        return jobSheetRepository.save(jobSheet);
    }

    private void addJobSheetProcessItem(JobSheetVO jobSheetVO, JobSheet savedJobSheet) throws JsonProcessingException {
        for (int i = 0; i < jobSheetVO.getProcessItemIds().size(); i++) {
            JobSheetProcessItem jobSheetProcessItem = new JobSheetProcessItem();
            ProcessItem processItem = processItemRepository.getById(jobSheetVO.getProcessItemIds().get(i));

            // 스냅샷 저장
            MySnapShot mySnapShot = new MySnapShot();
            long newMySnapShotId = 0;
            String newMySnapShotName = "";
            if (jobSheetVO.getMySnapShotIds() != null && jobSheetVO.getMySnapShotIds().size() > i) {
                if (jobSheetVO.getMySnapShotIds().get(i) != null && jobSheetVO.getMySnapShotIds().get(i) > 0) {
                    mySnapShot = mySnapShotRepository.findByIdAndOwnerId(jobSheetVO.getMySnapShotIds().get(i), userInfo.getId());
                    if (mySnapShot != null) {
                        newMySnapShotId = mySnapShot.getId();
                        newMySnapShotName = mySnapShot.getSource();
                    }
                }
            }

            jobSheetProcessItem.setJobSheetProcessItemAtAddJobSheet(
                    savedJobSheet, processItem,
                    jobSheetVO.getPhasingCodes().get(i),
                    jobSheetVO.getTaskFullPaths().get(i),
                    jobSheetVO.getBeforeProgressRates().get(i),
                    jobSheetVO.getAfterProgressRates().get(i),
                    jobSheetVO.getTodayProgressRates().get(i),
                    jobSheetVO.getBeforeProgressAmounts().get(i),
                    jobSheetVO.getAfterProgressAmounts().get(i),
                    jobSheetVO.getTodayProgressAmounts().get(i),
                    jobSheetVO.getGrantorId(),
                    jobSheetVO.getProcessItemWorkers().get(i),
                    jobSheetVO.getProcessItemDevices().get(i),
                    jobSheetVO.getExchangeIds().get(i),
                    newMySnapShotId, newMySnapShotName
            );
            jobSheetProcessItem = jobSheetProcessItemRepository.save(jobSheetProcessItem);

            // 작업일지 상시일 경우
            /**
            if (jobSheetVO.getStatus() != null && (jobSheetVO.getStatus().equals("GOING") || jobSheetVO.getStatus().equals("COMPLETE"))) {
                // 작업일지 상시일 경우, 작업일지 상시 진행률 저장
                JobSheetProcessItemCost jobSheetProcessItemCost = new JobSheetProcessItemCost();
                jobSheetProcessItemCost.setJobSheetProcessItemCostAtAddJobSheet(userInfo, processItem, savedJobSheet,
                        jobSheetVO.getBeforeProgressRates().get(i),
                        jobSheetVO.getAfterProgressRates().get(i),
                        jobSheetVO.getTodayProgressRates().get(i),
                        jobSheetVO.getBeforeProgressAmounts().get(i),
                        jobSheetVO.getAfterProgressAmounts().get(i),
                        jobSheetVO.getTodayProgressAmounts().get(i));
                jobSheetProcessItemCostRepository.save(jobSheetProcessItemCost);
            }
             **/



            // worker 추가
            if (jobSheetVO.getProcessItemWorkers() != null) {
                List<Object> jobSheetProcessItemWorkers = jobSheetVO.getProcessItemWorkers().get(i);
                if (jobSheetProcessItemWorkers.size() > 0) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonInString = objectMapper.writeValueAsString(jobSheetProcessItemWorkers);
                    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    List<Map<String, Object>> list = objectMapper.readValue(jsonInString, List.class);

                    for (Map<String, Object> map : list) {
                        String name = (String) map.get("name");
                        Double value = Double.parseDouble((String) map.get("value"));
                        BigDecimal workerAmount = new BigDecimal(value.toString());
                        Long processWorkerId = Long.parseLong((String) map.get("id"));

                        BigDecimal todayWorkerAmount = processWorkerRepository.getJobSheetProcessItemWorkerAmount(savedJobSheet.getProjectId(), processItem.getProcessInfo().getId(), processWorkerId).orElseGet(() -> BigDecimal.valueOf(0.00));
                        BigDecimal beforeWorkerAmount = todayWorkerAmount == null ? BigDecimal.ZERO : todayWorkerAmount;

                        JobSheetProcessItemWorker jobSheetProcessItemWorker = new JobSheetProcessItemWorker();
                        jobSheetProcessItemWorker.setJobSheetProcessItemWorkerAtAddJobSheetProcessItem(
                                savedJobSheet, jobSheetProcessItem, processItem.getId(), savedJobSheet.getProjectId(), processItem.getProcessInfo().getId(), name, workerAmount, beforeWorkerAmount, processWorkerId
                        );
                        jobSheetProcessItemWorkerRepository.save(jobSheetProcessItemWorker);
                    }
                }
            }

            if (jobSheetVO.getProcessItemDevices() != null) {
                // device 추가
                List<Object> jobSheetProcessItemDevices = jobSheetVO.getProcessItemDevices().get(i);
                if (jobSheetProcessItemDevices.size() > 0) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonInString = objectMapper.writeValueAsString(jobSheetProcessItemDevices);
                    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    List<Map<String, Object>> list = objectMapper.readValue(jsonInString, List.class);

                    for (Map<String, Object> map : list) {
                        String name = (String) map.get("name");
                        Double value = Double.parseDouble((String) map.get("value"));
                        Long processDeviceId = Long.parseLong((String) map.get("id"));
                        BigDecimal deviceAmount = new BigDecimal(value.toString());

                        BigDecimal todayDeviceAmount = processDeviceRepository.getJobSheetProcessItemDeviceAmount(savedJobSheet.getProjectId(), processItem.getProcessInfo().getId(), processDeviceId).orElseGet(() -> BigDecimal.valueOf(0.00));
                        BigDecimal beforeDeviceAmount = todayDeviceAmount == null ? BigDecimal.ZERO : todayDeviceAmount;

                        JobSheetProcessItemDevice jobSheetProcessItemDevice = new JobSheetProcessItemDevice();
                        jobSheetProcessItemDevice.setJobSheetProcessItemDeviceAtAddJobSheetProcessItem(
                                savedJobSheet, jobSheetProcessItem, processItem.getId(), savedJobSheet.getProjectId(), processItem.getProcessInfo().getId(), name, deviceAmount, beforeDeviceAmount, processDeviceId
                        );
                        jobSheetProcessItemDeviceRepository.save(jobSheetProcessItemDevice);
                    }
                }
            }
        }
    }

    private void saveProcessItem(List<JobSheetProcessItem> jobSheetProcessItems) {
        for (JobSheetProcessItem  jobSheetProcessItem : jobSheetProcessItems) {
            ProcessItem processItem = processItemRepository.getById(jobSheetProcessItem.getProcessItem().getId());
            BigDecimal todayProgressRate = jobSheetProcessItemRepository.getJobSheetProcessItemTodayProgressRate(processItem.getPhasingCode(), processItem.getId()).orElseGet(() -> BigDecimal.valueOf(0.00));
            long todayProgressAmount = jobSheetProcessItemRepository.getJobSheetProcessItemTodayProgressAmount(processItem.getPhasingCode(), processItem.getId()).orElseGet(() -> 0L);
            BigDecimal beforeProgressRate = todayProgressRate == null ? BigDecimal.ZERO : todayProgressRate;
            long beforeProgressAmount = todayProgressAmount;
            // fix) 공사일지의 진척률이 공정과 내역에서 잘못 표시되는 현상
            processItem.setProgressRate(beforeProgressRate);
            processItem.setProgressAmount(beforeProgressAmount);
            processItemRepository.save(processItem);
        }
    }

    private void saveProcessWorker(JobSheet savedJobSheet) {
        List<JobSheetProcessItemWorker> jobSheetProcessItemWorkers = jobSheetProcessItemWorkerRepository.findJobSheetProcessItemWorkerByJobSheetId(savedJobSheet.getId());
        for (JobSheetProcessItemWorker  jobSheetProcessItemWorker : jobSheetProcessItemWorkers) {
            ProcessItem processItem = processItemRepository.getById(jobSheetProcessItemWorker.getProcessItemId());
            ProcessInfo processInfo = processInfoRepository.getById(processItem.getProcessInfo().getId());
            BigDecimal todayWorkerAmount = processWorkerRepository.getJobSheetProcessItemWorkerAmount(jobSheetProcessItemWorker.getProjectId(), processItem.getProcessInfo().getId(), jobSheetProcessItemWorker.getProcessWorkerId()).orElseGet(() -> BigDecimal.valueOf(0.00));
            BigDecimal beforeWorkerAmount = todayWorkerAmount == null ? BigDecimal.ZERO : todayWorkerAmount;
            ProcessWorker processWorker = processWorkerRepository.findByProjectIdAndProcessIdAndWorkerName(savedJobSheet.getProjectId(), processItem.getProcessInfo().getId(), jobSheetProcessItemWorker.getWorkerName()).orElseGet(ProcessWorker::new);
            if (processWorker != null && processWorker.getId() > 0) {
                processWorker.setWorkerAmount(beforeWorkerAmount);
                processWorkerRepository.save(processWorker);
            } else {
                processWorker.setProcessWorkerAtAddJobSheetProcessItemWorker(
                        jobSheetProcessItemWorker.getProjectId(),
                        processInfo,
                        jobSheetProcessItemWorker.getWorkerName(),
                        beforeWorkerAmount
                );
                processWorkerRepository.save(processWorker);
            }
        }
    }

    private void saveProcessDevice(JobSheet savedJobSheet) {
        List<JobSheetProcessItemDevice> jobSheetProcessItemDevices = jobSheetProcessItemDeviceRepository.findJobSheetProcessItemDeviceByJobSheetId(savedJobSheet.getId());
        for (JobSheetProcessItemDevice  jobSheetProcessItemDevice : jobSheetProcessItemDevices) {
            ProcessItem processItem = processItemRepository.getById(jobSheetProcessItemDevice.getProcessItemId());
            ProcessInfo processInfo = processInfoRepository.getById(processItem.getProcessInfo().getId());
            BigDecimal todayDeviceAmount = processDeviceRepository.getJobSheetProcessItemDeviceAmount(jobSheetProcessItemDevice.getProjectId(), processItem.getProcessInfo().getId(), jobSheetProcessItemDevice.getProcessDeviceId()).orElseGet(() -> BigDecimal.valueOf(0.00));
            BigDecimal beforeDeviceAmount = todayDeviceAmount == null ? BigDecimal.ZERO : todayDeviceAmount;
            ProcessDevice processDevice = processDeviceRepository.findByProjectIdAndProcessIdAndDeviceName(jobSheetProcessItemDevice.getProjectId(), processItem.getProcessInfo().getId(), jobSheetProcessItemDevice.getDeviceName()).orElseGet(ProcessDevice::new);
            if (processDevice != null && processDevice.getId() > 0) {
                processDevice.setDeviceAmount(beforeDeviceAmount);
                processDeviceRepository.save(processDevice);
            } else {
                processDevice.setProcessDeivceAtAddJobSheetProcessItemDevice(
                        jobSheetProcessItemDevice.getProjectId(),
                        processInfo,
                        jobSheetProcessItemDevice.getDeviceName(),
                        beforeDeviceAmount
                );
                processDeviceRepository.save(processDevice);
            }
        }
    }


    private void addJobSheetReferences(JobSheetVO jobSheetVO, JobSheet savedJobSheet) {
        int sortNo = 1;
        if (jobSheetVO.getReferencesIds() != null) {
            for (Long referenceId : jobSheetVO.getReferencesIds()) {
                JobSheetReference jobSheetReference = new JobSheetReference();
                jobSheetReference.setJobSheetReferenceAtAddJobSheet(savedJobSheet, referenceId, sortNo);
                jobSheetReferenceRepository.save(jobSheetReference);
                sortNo++;
            }
        }
    }

    private void addJobSheetGrantor(JobSheetVO jobSheetVO, JobSheet savedJobSheet) {
        JobSheetGrantor jobSheetGrantor = new JobSheetGrantor();
        jobSheetGrantor.setJobSheetGrantorAtAddJobSheet(savedJobSheet, jobSheetVO.getGrantorId());
        jobSheetGrantorRepository.save(jobSheetGrantor);
    }

    public PageDTO<JobSheetDTO> findJobSheetDTOs(SearchJobSheetVO searchJobSheetVO, Pageable pageable) {
        return jobSheetDTODslRepository.findJobSheetDTOs(searchJobSheetVO, pageable);
    }

    public JobSheet findJobSheetReferencesById(long jobSheetId) {
        return jobSheetRepository.findJobSheetReferencesById(jobSheetId).orElseGet(JobSheet::new);
    }

    public JobSheet findJobSheetByIdAndProjectId(long id, long projectId) {
        return jobSheetRepository.findByIdAndProjectId(id, projectId).orElseGet(JobSheet::new);
    }

    public JobSheet findJobSheetAndWriterId(long writer_id) {
        return jobSheetRepository.findJobSheetAndWriterId(writer_id).orElseGet(JobSheet::new);
    }

    public JobSheet findJobSheetAndProjectId() {
        return jobSheetRepository.findJobSheetAndProjectId(userInfo.getProjectId()).orElseGet(JobSheet::new);
    }

    @Transactional
    public JsonObject denyJobSheet(JobSheetGrantorVO jobSheetGrantorVO) {
        try {
            JobSheetGrantor savedJobSheetGrantor = jobSheetGrantorRepository.findByIdAndJobSheetId(jobSheetGrantorVO.getId(), jobSheetGrantorVO.getJobSheetId()).orElseGet(JobSheetGrantor::new);
            if (savedJobSheetGrantor.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_grantor");
            savedJobSheetGrantor.setJobSheetGrantorAtDenyJobSheet(jobSheetGrantorVO);

            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(jobSheetGrantorVO.getJobSheetId(), userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");
            savedJobSheet.setJobSheetAtDenyJobSheet();      // 반려 상태로 변경(status REJECT)

            List<JobSheetProcessItem> jobSheetProcessItems = findJobSheetProcessItemByJobSheetId(jobSheetGrantorVO.getJobSheetId());
            saveProcessItem(jobSheetProcessItems);

            return proc.getResult(true, "system.job_sheet_service.deny_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject approveJobSheet(JobSheetGrantorVO jobSheetGrantorVO) {
        try {
            JobSheetGrantor savedJobSheetGrantor = jobSheetGrantorRepository.findByIdAndJobSheetId(jobSheetGrantorVO.getId(), jobSheetGrantorVO.getJobSheetId()).orElseGet(JobSheetGrantor::new);
            if (savedJobSheetGrantor.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_grantor");
            savedJobSheetGrantor.setJobSheetGrantorAtApproveJobSheet(jobSheetGrantorVO);

            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(jobSheetGrantorVO.getJobSheetId(), userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");
            savedJobSheet.setJobSheetAtApproveJobSheet();       // 승인 상태로 변경(status COMPLETE)

            List<JobSheetProcessItem> jobSheetProcessItems = findJobSheetProcessItemByJobSheetId(jobSheetGrantorVO.getJobSheetId());
            saveProcessItem(jobSheetProcessItems);

            return proc.getResult(true, "system.job_sheet_service.approve_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteJobSheet(long id) {
        try {
            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(id, userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");

            savedJobSheet.setJobSheetAtDeleteJobSheet();
            jobSheetProcessItemRepository.deleteAllByIdInQuery(savedJobSheet.getId());  // job_sheet_process_item_cost 삭제

            List<JobSheetProcessItem> jobSheetProcessItems = jobSheetProcessItemRepository.findJobSheetProcessItemByJobSheetId(savedJobSheet.getId());
            saveProcessItem(jobSheetProcessItems);
            saveProcessWorker(savedJobSheet);
            saveProcessDevice(savedJobSheet);
            setDisabledAlert(savedJobSheet);

            return proc.getResult(true, "system.job_sheet_service.delete_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteSelJobSheet(List<JobSheetVO> jobSheetVO) {
        try {
            for (int i = 0; i < jobSheetVO.size(); i++) {
                JobSheet deleteJobSheet = jobSheetRepository.findById(jobSheetVO.get(i).getId()).orElseGet(JobSheet::new);
                if (deleteJobSheet.getId() == 0) {
                    return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");
                }

                deleteJobSheet.setJobSheetAtDeleteJobSheet();
                jobSheetProcessItemRepository.deleteAllByIdInQuery(deleteJobSheet.getId());  // job_sheet_process_item_cost 삭제

                List<JobSheetProcessItem> jobSheetProcessItems = jobSheetProcessItemRepository.findJobSheetProcessItemByJobSheetId(deleteJobSheet.getId());
                saveProcessItem(jobSheetProcessItems);
                saveProcessWorker(deleteJobSheet);
                saveProcessDevice(deleteJobSheet);
                setDisabledAlert(deleteJobSheet);
            }

            return proc.getResult(true, "system.job_sheet_service.delete_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putJobSheetWritingStatus(long id) {
        try {
            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(id, userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");

            savedJobSheet.setJobSheetAtWritingJobSheet();   // 임시저장 상태로 변경
            jobSheetProcessItemRepository.deleteAllByIdInQuery(savedJobSheet.getId());  // job_sheet_process_item_cost 삭제

            //List<JobSheetProcessItem> jobSheetProcessItems = jobSheetProcessItemRepository.findJobSheetProcessItemByJobSheetId(savedJobSheet.getId());
            List<JobSheetProcessItem> updateJobSheetProcessItems = jobSheetProcessItemRepository.findAllJobSheetByEnabledByStatus(userInfo.getProjectId());
            saveProcessItem(updateJobSheetProcessItems);    // 작업일지 저장된 공정 실적 다시 적용

            return proc.getResult(true, "system.job_sheet_service.writing_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void setDisabledAlert(JobSheet savedJobSheet) {
        alertService.setDisabledAlert(userInfo.getProjectId(), savedJobSheet.getId(), AlertType.JOB_SHEET);
    }

    @Transactional
    public JsonObject putJobSheet(JobSheetVO jobSheetVO) {
        try {
            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(jobSheetVO.getId(), userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");

            // 작업일지 상시일 경우
            if (jobSheetVO.getStatus() != null && (jobSheetVO.getStatus().equals("GOING") || jobSheetVO.getStatus().equals("COMPLETE"))) {
                // 공정 누적 진행률이 100%를 오버하는지 체크
                for (int i = 0; i < jobSheetVO.getProcessItemIds().size(); i++) {
                    JobSheetProcessItem jobSheetProcessItem = new JobSheetProcessItem();
                    ProcessItem processItem = processItemRepository.getById(jobSheetVO.getProcessItemIds().get(i));

                    // progressRate 진행률 합계
                    BigDecimal progressRate = processItem.getProgressRate();
                    BigDecimal progressRateSum = progressRate.add(jobSheetVO.getTodayProgressRates().get(i));
                    if (progressRateSum.compareTo(new BigDecimal("100")) > 0)
                        return proc.getResult(false, "system.gisung_service.task_progress_rate_over_100");
                }
            }

            savedJobSheet.setJobSheetAtUpdateJobSheet(jobSheetVO);

            //grantor
            JobSheetGrantor savedJobSheetGrantor = jobSheetGrantorRepository.findByJobSheetId(jobSheetVO.getId()).orElseGet(JobSheetGrantor::new);
            if (savedJobSheetGrantor.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet_grantor");
            jobSheetGrantorRepository.delete(savedJobSheetGrantor);
            addJobSheetGrantor(jobSheetVO, savedJobSheet);

            //JobSheetProcessItem
            List<JobSheetProcessItem> jobSheetProcessItems = jobSheetProcessItemRepository.findJobSheetProcessItemByJobSheetId(jobSheetVO.getId());
            if (jobSheetProcessItems.size() > 0) {
                jobSheetProcessItemRepository.deleteAllInBatch(jobSheetProcessItems);
            }
            /**
            //JobSheetProcessItemCost
            List<JobSheetProcessItemCost> jobSheetProcessItemCosts = jobSheetProcessItemCostRepository.findJobSheetProcessItemCostByJobSheetId(jobSheetVO.getId());
            if (jobSheetProcessItemCosts.size() > 0) {
                jobSheetProcessItemCostRepository.deleteAllInBatch(jobSheetProcessItemCosts);
            }
             **/
            //JobSheetProcessItemWorker
            List<JobSheetProcessItemWorker> jobSheetProcessItemWorkers = jobSheetProcessItemWorkerRepository.findJobSheetProcessItemWorkerByJobSheetId(jobSheetVO.getId());
            if (jobSheetProcessItemWorkers.size() > 0) {
                jobSheetProcessItemWorkerRepository.deleteAllInBatch(jobSheetProcessItemWorkers);
            }
            //JobSheetProcessItemDevice
            List<JobSheetProcessItemDevice> jobSheetProcessItemDevices = jobSheetProcessItemDeviceRepository.findJobSheetProcessItemDeviceByJobSheetId(jobSheetVO.getId());
            if (jobSheetProcessItemDevices.size() > 0) {
                jobSheetProcessItemDeviceRepository.deleteAllInBatch(jobSheetProcessItemDevices);
            }
            addJobSheetProcessItem(jobSheetVO, savedJobSheet);

            //reference
            List<JobSheetReference> jobSheetReferences = jobSheetReferenceRepository.findByJobSheetId(jobSheetVO.getId());
            if (jobSheetReferences.size() > 0) {
                jobSheetReferenceRepository.deleteAllInBatch(jobSheetReferences);
            }
            addJobSheetReferences(jobSheetVO, savedJobSheet);

            /**
             //snapshot
             if (jobSheetVO.getMySnapShotIds().size() > 0) {
             List<JobSheetSnapShot> jobSheetSnapShots = jobSheetSnapShotRepository.findByJobSheetId(jobSheetVO.getId());
             jobSheetSnapShotRepository.deleteAllInBatch(jobSheetSnapShots);
             }
             addJobSheetSnapShot(jobSheetVO, savedJobSheet);
             **/

            // 전체 공정 실적 다시 적용
            /**
            List<JobSheetProcessItem> updateJobSheetProcessItems = jobSheetProcessItemRepository.findAllJobSheetByEnabledByStatus(userInfo.getProjectId());
            saveProcessItem(updateJobSheetProcessItems);
            saveProcessWorker(savedJobSheet);
            saveProcessDevice(savedJobSheet);
             **/

            saveProcessItem(jobSheetProcessItems);
            saveProcessWorker(savedJobSheet);
            saveProcessDevice(savedJobSheet);

            setDisabledPreviousAlert(savedJobSheet);
            saveAlert(jobSheetVO, savedJobSheet, "UPDATE");

            if (jobSheetVO.getStatus() != null && (jobSheetVO.getStatus().equals("GOING") || jobSheetVO.getStatus().equals("COMPLETE"))) {
                return proc.getResult(true, "system.job_sheet_service.sangsin_job_sheet");
            } else {
                return proc.getResult(true, "system.job_sheet_service.put_job_sheet");
            }
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void setDisabledPreviousAlert(JobSheet savedJobSheet) {
        alertService.setDisabledPreviousAlert(userInfo.getProjectId(), savedJobSheet.getId(), AlertType.JOB_SHEET);
    }

    @Transactional
    public JsonObject copyJobSheet(JobSheetVO jobSheetVO) {
        try {
            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(jobSheetVO.getId(), userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");

            JobSheet newJobSheet = addJobSheet(jobSheetVO);
            addJobSheetGrantor(jobSheetVO, newJobSheet);
            addJobSheetProcessItem(jobSheetVO, newJobSheet);
            addJobSheetReferences(jobSheetVO, newJobSheet);

            /**
             if (jobSheetVO.getMySnapShotIds().size() > 0) {
             addJobSheetSnapShot(jobSheetVO, newJobSheet);
             } else {
             addJobSheetSnapShotAtCopy(savedJobSheet, newJobSheet);
             }
             **/

            if (jobSheetVO.getFileIds().size() > 0) {
                addJobSheetFileAtCopy(jobSheetVO, newJobSheet);
            }

            saveAlert(jobSheetVO, newJobSheet, "INSERT");

            return proc.getResult(true, newJobSheet.getId(), "system.job_sheet_service.copy_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }

    }

    private void addJobSheetFileAtCopy(JobSheetVO jobSheetVO, JobSheet newJobSheet) throws IOException {
        String saveBasePath = Utils.getSaveBasePath(winPathUpload, linuxPathUpload, macPathUpload);
        String newFilePath = userInfo.getProjectId() + "/" + FileUploadUIType.JOB_SHEET_FILE.name();
        int sortNo = 1;
        for (Long fileId : jobSheetVO.getFileIds()) {
            JobSheetFile savedJobSheetFile = jobSheetFileRepository.findById(fileId).orElseGet(JobSheetFile::new);
            File oldFile = new File(Utils.getPhysicalFilePath(winPathUpload, linuxPathUpload, macPathUpload, savedJobSheetFile.getFilePath()));
            String newFileName = getSaveFileName(savedJobSheetFile.getOriginFileName());
            String newFilePathSavedDB = "/file_upload/" + newFilePath + "/" + newFileName;
            FileCopyUtils.copy(oldFile, new File(saveBasePath + "/" + newFilePath + "/" + newFileName));

            JobSheetFile newJobSheetFile = new JobSheetFile();
            newJobSheetFile.setJobSheetFileAtJobSheetCopy(newJobSheet, savedJobSheetFile, newFilePathSavedDB, sortNo);
            jobSheetFileRepository.save(newJobSheetFile);
            sortNo++;
        }
    }

    private void addJobSheetSnapShotAtCopy(JobSheet savedJobSheet, JobSheet newJobSheet) {
        if (savedJobSheet.getJobSheetSnapShots().size() > 0) {
            for (JobSheetSnapShot savedJobSheetSnapShot : savedJobSheet.getJobSheetSnapShots()) {
                JobSheetSnapShot newJobSheetSnapShot = new JobSheetSnapShot();
                newJobSheetSnapShot.setJobSheetSnapShotAtCopyJobSheet(newJobSheet, savedJobSheetSnapShot);
                jobSheetSnapShotRepository.save(newJobSheetSnapShot);
            }
        }
    }

    private String getSaveFileName(String originalFileName) {
        return Utils.getFileNameWithOutExt(originalFileName) + "_" + Utils.getSaveFileNameDate() + "." + Utils.getFileExtName(originalFileName);
    }

    @Transactional
    public JsonObject reAddJobSheet(JobSheetVO jobSheetVO) {
        try {
            JobSheet savedJobSheet = jobSheetRepository.findByIdAndProjectId(jobSheetVO.getId(), userInfo.getProjectId()).orElseGet(JobSheet::new);
            if (savedJobSheet.getId() == 0) return proc.getResult(false, "system.job_sheet_service.not_exist_job_sheet");

            JobSheet newJobSheet = addJobSheetWithGroupId(jobSheetVO, savedJobSheet);
            addJobSheetGrantor(jobSheetVO, newJobSheet);
            addJobSheetProcessItem(jobSheetVO, newJobSheet);
            addJobSheetReferences(jobSheetVO, newJobSheet);

            /**
             if (jobSheetVO.getMySnapShotIds().size() > 0) {
             addJobSheetSnapShot(jobSheetVO, newJobSheet);
             } else {
             addJobSheetSnapShotAtCopy(savedJobSheet, newJobSheet);
             }
             **/

            if (jobSheetVO.getFileIds().size() > 0) {
                addJobSheetFileAtCopy(jobSheetVO, newJobSheet);
            }
            saveAlert(jobSheetVO, newJobSheet, "INSERT");

            return proc.getResult(true, newJobSheet.getId(), "system.job_sheet_service.re_add_job_sheet");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private JobSheet addJobSheetWithGroupId(JobSheetVO jobSheetVO, JobSheet savedJobSheet) {
        JobSheet newJobSheet = new JobSheet();
        newJobSheet.setJobSheetAtReAddJobSheet(userInfo, jobSheetVO, savedJobSheet.getId());
        return jobSheetRepository.save(newJobSheet);
    }

    public JobSheetProcessItem findJobSheetProcessItemById(long id) {
        return jobSheetProcessItemRepository.findJobSheetProcessItemById(id).orElseGet(JobSheetProcessItem::new);
    }

    public ViewJobSheetProcessItem findViewJobSheetProcessItemByProcessItemId(long id) {
        return viewJobSheetProcessItemRepository.findViewJobSheetProcessItemByProcessItemId(id).orElseGet(ViewJobSheetProcessItem::new);
    }

    public List<ViewJobSheetProcessItemDTO> findAllByProcessId(long processItemId, long jobSheetId, long userId) {
        return viewJobSheetProcessItemDTODslRepository.findAllByProcessId(processItemId, jobSheetId, userId);
    }

    public List<JobSheetProcessItem> findJobSheetProcessItemByJobSheetId(long jobSheetId) {
        return jobSheetProcessItemRepository.findJobSheetProcessItemByJobSheetId(jobSheetId);
    }

    public List<ViewJobSheetProcessItem> findViewJobSheetProcessItemByJobSheetId(long jobSheetId) {
        return viewJobSheetProcessItemRepository.findViewJobSheetProcessItemByJobSheetId(jobSheetId);
    }

    public List<JobSheet> selectJobSheetProcessItemId(long jobSheetProcessItemId) {
        return jobSheetProcessItemRepository.selectJobSheetProcessItemId(jobSheetProcessItemId);
    }

    public List<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByJobSheetId(long jobSheetId) {
        return jobSheetProcessItemWorkerRepository.findJobSheetProcessItemWorkerByJobSheetId(jobSheetId);
    }

    public List<JobSheetProcessItemWorker> getProcessItemWorkerDetail(long jobSheetId) {
        return jobSheetProcessItemWorkerRepository.getProcessItemWorkerDetail(jobSheetId);
    }

    public List<JobSheetDTO> findJobSheetListIdsByPhasingCodes(long processId, String[] phasingCodes) {
        return jobSheetDTODslRepository.findJobSheetListIdsByPhasingCodes(userInfo.getProjectId(), processId, userInfo.getId(), phasingCodes);
    }

    /**
     * 작업지시서 작업자 리스트
     * @param jobSheetId
     * @return
     */
    public List<JobSheetProcessItemWorkerDTO> findAllProjectListForWorker(long jobSheetId, LocalDateTime reportDate) {
        return jobSheetProcessItemDTODslRepository.findAllProjectListForWorker(jobSheetId, reportDate);
    }

    public List<JobSheetProcessItemDevice> getProcessItemDeviceDetail(long jobSheetId) {
        return jobSheetProcessItemDeviceRepository.getProcessItemDeviceDetail(jobSheetId);
    }

    public List<JobSheetProcessItemWorker> findJobSheetProcessItemWorkerByJobSheetProcessItemId(long jobSheetProcessItemId) {
        return jobSheetProcessItemWorkerRepository.findJobSheetProcessItemWorkerByJobSheetProcessItemId(jobSheetProcessItemId);
    }

    public List<JobSheetProcessItemDevice> findJobSheetProcessItemDeviceByJobSheetProcessItemId(long jobSheetProcessItemId) {
        return jobSheetProcessItemDeviceRepository.findJobSheetProcessItemDeviceByJobSheetProcessItemId(jobSheetProcessItemId);
    }

    /**
     * 작업지시서 장비 리스트
     * @param jobSheetId
     * @return
     */
    public List<JobSheetProcessItemDeviceDTO> findAllProjectListForDevice(long jobSheetId, LocalDateTime reportDate) {
        return jobSheetProcessItemDTODslRepository.findAllProjectListForDevice(jobSheetId, reportDate);
    }

    public JsonObject jobSheetGisungReport(String startDate, String endDate) {
        return proc.getResult(true, jobSheetDTODslRepository.findJobSheetListDTOs(userInfo.getProjectId(), startDate, endDate));
    }

    public ProcessItem getProcessItem(long processId) {
        return processItemRepository.getById(processId);
    }

    public List<ProcessDevice> findByProjectIdAndProcessIdAndDeviceList(long processId) {
        return processDeviceRepository.findByProjectIdAndProcessIdAndDeviceList(userInfo.getProjectId(), processId);
    }

    public List<ProcessWorker> findByProjectIdAndProcessIdAndWorkerList(long processId) {
        return processWorkerRepository.findByProjectIdAndProcessIdAndWorkerList(userInfo.getProjectId(), processId);
    }

    public String findJobSheetAndReportDate() {
        return jobSheetRepository.findJobSheetAndReportDate().orElse("");
    }

    public long getJobSheetProcessItemTodayProgressAmountSum(String reportDate) {
        return jobSheetProcessItemRepository.getJobSheetProcessItemTodayProgressAmountSum(reportDate).orElse(0L);
    }

    public List<JobSheetProcessItemDeviceDTO> getJobSheetProcessItemDeviceDate(String reportDate) {
        return jobSheetProcessItemDTODslRepository.getJobSheetProcessItemDeviceDate(reportDate, 5);
    }

    public List<JobSheetProcessItemWorkerDTO> getJobSheetProcessItemWorkerDate(String reportDate) {
        return jobSheetProcessItemDTODslRepository.getJobSheetProcessItemWorkerDate(reportDate, 5);
    }

    public long getJobSheetProcessItemDeviceAmountSum(String reportDate) {
        List<JobSheetProcessItemDeviceDTO> jobSheetProcessItemDeviceDTOS = jobSheetProcessItemDTODslRepository.getJobSheetProcessItemDeviceDateList(reportDate);
        return jobSheetProcessItemDeviceDTOS.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
    }

    public long getJobSheetProcessItemWorkerAmountSum(String reportDate) {
        List<JobSheetProcessItemWorkerDTO> jobSheetProcessItemWorkerDTOS = jobSheetProcessItemDTODslRepository.getJobSheetProcessItemWorkerDateList(reportDate);
        return jobSheetProcessItemWorkerDTOS.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
    }

    public List<ViewJobSheetProcessItem> getJobSheetProcessItemReportDateListLimit(String date) {
        return viewJobSheetProcessItemRepository.getJobSheetProcessItemReportDateListLimit(userInfo.getProjectId(), date, 5);
    }

    public String generateCalendarHtml(int year, int month) {
        StringBuilder html = new StringBuilder();
        String date = "";

        // 월과 연도 출력
        SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMMM yyyy");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        // 달력 테이블 시작
        html.append("<table><thead><tr>");
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        for (String day : daysOfWeek) {
            html.append("<th>").append(day).append("</th>");
        }
        html.append("</tr></thead><tbody>");

        calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 2, 1);

        // 1일 이전의 빈 날짜 처리
        int prevYear = calendar.get(Calendar.YEAR);
        int prevMonth = calendar.get(Calendar.MONTH) + 1;
        int firstDayOfPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int lastDayOfPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOffset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7;

        html.append("<tr>");
        for (int i = firstDayOfPrevMonth - dayOffset + 1; i <= firstDayOfPrevMonth; i++) {
            date = prevYear + "-" + String.format("%02d", prevMonth) + "-" + String.format("%02d", i);
            List<ViewJobSheetProcessItem> viewJobSheetProcessItems = viewJobSheetProcessItemRepository.getJobSheetProcessItemReportDateListLimit(userInfo.getProjectId(), date, 3);
            html.append("<td><div class=\"head\">");
            html.append("<strong class=\"txt-color-gray\">").append(i).append("</strong>");
            html.append("</div>");
            html.append("<ul>");
            for (ViewJobSheetProcessItem viewJobSheetProcessItem : viewJobSheetProcessItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(viewJobSheetProcessItem.getTaskName()).append("</button></li>");
            }
            html.append("</ul>");
            html.append("</td>");
        }

        // 날짜 삽입
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDay; day++) {
            date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
            List<ViewJobSheetProcessItem> viewJobSheetProcessItems = viewJobSheetProcessItemRepository.getJobSheetProcessItemReportDateListLimit(userInfo.getProjectId(), year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day), 3);
            html.append("<td><div class=\"head\">");
            if ((firstDayOfWeek + day - 1) % 7 == 1) {
                html.append("<strong class=\"txt-color-red\">").append(day).append("</strong>");
            } else {
                html.append("<strong>").append(day).append("</strong>");
            }
            html.append("</div>");
            html.append("<ul>");
            for (ViewJobSheetProcessItem viewJobSheetProcessItem : viewJobSheetProcessItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(viewJobSheetProcessItem.getTaskName()).append("</button></li>");
            }
            html.append("</ul>");
            html.append("</td>");

            // 줄 바꾸기
            if ((firstDayOfWeek + day - 1) % 7 == 0) {
                html.append("</tr><tr>");
            }
        }

        // 1일 이후의 빈 날짜 처리
        int nextMonthDay = 1;
        calendar.add(Calendar.MONTH, 1); // 다음 달로 이동
        int nextYear = calendar.get(Calendar.YEAR);
        int nextMonth = calendar.get(Calendar.MONTH) + 1;
        int firstDayOfNextMonth = calendar.get(Calendar.DAY_OF_WEEK);

        while (nextMonthDay < 7 - (firstDayOfNextMonth - Calendar.MONDAY)) {
            System.out.println(Calendar.DAY_OF_YEAR + "-" + String.format("%02d", Calendar.DAY_OF_MONTH) + "-" + String.format("%02d", nextMonthDay) + "----------------------------");
            date = nextYear + "-" + String.format("%02d", nextMonth) + "-" + String.format("%02d", nextMonthDay);
            List<ViewJobSheetProcessItem> viewJobSheetProcessItems = viewJobSheetProcessItemRepository.getJobSheetProcessItemReportDateListLimit(userInfo.getProjectId(), nextYear + "-" + String.format("%02d", nextMonth) + "-" + String.format("%02d", nextMonthDay), 3);
            html.append("<td><div class=\"head\">");
            html.append("<strong class=\"txt-color-gray\">").append(nextMonthDay).append("</strong>");
            html.append("</div>");
            html.append("<ul>");
            for (ViewJobSheetProcessItem viewJobSheetProcessItem : viewJobSheetProcessItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(viewJobSheetProcessItem.getTaskName()).append("</button></li>");
            }
            //html.append("<li><button type=\"button\" class=\"pop-open-btn\" data-modal=\"#calendarPopup\">항목2</button></li>");
            html.append("</ul>");
            html.append("</td>");
            nextMonthDay++;
        }

        // 달력 테이블 종료
        html.append("</tr></table>");

        return html.toString();
    }
}
