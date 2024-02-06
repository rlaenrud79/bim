package com.devo.bim.service;

import com.devo.bim.component.Utils;
import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.ProcessCategoryDefaultCode;
import com.devo.bim.model.enumulator.TaskType;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.repository.dsl.ModelingAttributeDslRepository;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.devo.bim.repository.dsl.VmProcessItemDTODslRepository;
import com.devo.bim.repository.spring.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessCostService extends AbstractService {
    private final ProcessInfoRepository processInfoRepository;
    private final ProcessItemRepository processItemRepository;
    private final ProcessItemCostDetailRepository processItemCostDetailRepository;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final ProcessItemCostPayRepository processItemCostPayRepository;
    private final ModelingAttributeDslRepository modelingAttributeDslRepository;
    private final ConfigService configService;
    private final JobSheetProcessItemRepository jobSheetProcessItemRepository;
    private final VmProcessItemDTODslRepository vmProcessItemDTODslRepository;
    private final ProcessItemCostDetailExcelFileRepository processItemCostDetailExcelFileRepository;
    private final WorkService workService;

    public JsonObject findProcessItemCostCurrentVersion(SearchProcessItemCostVO searchProcessItemCostVO) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setUpId(searchProcessItemCostVO.getWorkId());
        List<WorkDTO> workDTOs = new ArrayList<>();
        if (searchProcessItemCostVO.getWorkId() > 0) {
            workDTOs = workService.findWorkUpDTOs(searchWorkVO);
        }
        if (searchProcessItemCostVO != null && searchProcessItemCostVO.getSubWorkId() > 0) {
            searchProcessItemCostVO.setWorkId(searchProcessItemCostVO.getSubWorkId());
        }

        List<VmProcessItemDTO> vmProcessItemDTOS = vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);
        long paidCostSum = vmProcessItemDTOS.stream().map(t -> t.getPaidCost()).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();

        return proc.getResult(true, vmProcessItemDTOS, paidCostSum, workDTOs);
    }

    public JsonObject findSubWorkList(SearchProcessItemCostVO searchProcessItemCostVO) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setUpId(searchProcessItemCostVO.getWorkId());
        List<WorkDTO> workDTOs = new ArrayList<>();
        if (searchProcessItemCostVO.getWorkId() > 0) {
            workDTOs = workService.findWorkUpDTOs(searchWorkVO);
        }
        return proc.getResult(true, workDTOs);
    }

    public List<VmProcessItemDTO> findProcessItemCostCurrentVersionList(SearchProcessItemCostVO searchProcessItemCostVO){
        List<VmProcessItemDTO> vmProcessItemDTOS = vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);
        return vmProcessItemDTOS;
    }

    public JsonObject findProcessItemCostSearchCodeJson(SearchProcessItemCostVO searchProcessItemCostVO, List<Long> processItemIds) {
        return proc.getResult(true, processItemCostDslRepository.findProcessItemCostSearchCode(userInfo.getProjectId(), searchProcessItemCostVO, processItemIds));
    }

    public JsonObject findProcessItemSearch(SearchProcessItemCostVO searchProcessItemCostVO) {
        List<VmProcessItemDTO> findProcessItemCost = new ArrayList<>();
        List<VmProcessItemDTO> findParentProcessItem = vmProcessItemDTODslRepository.findProcessItemSearch(userInfo.getProjectId(), searchProcessItemCostVO);
        for(int i=0 ; i<findParentProcessItem.size() ; i++){
            if (findParentProcessItem.get(i).getGisungProcessItemId() <= 0) {
                findProcessItemCost.add(findParentProcessItem.get(i));
            }
        }
        return proc.getResult(true, findProcessItemCost);
    }

    public List<VmProcessItemDTO> findProcessItemCostChildrenRecursive(long processParentItemId){
        List<VmProcessItemDTO> findProcessItemCost = new ArrayList<>();
        List<VmProcessItemDTO> findParentProcessItem = vmProcessItemDTODslRepository.findProcessItemCostChildrenCurrentVersion(userInfo.getProjectId(), processParentItemId);

        for(int i=0 ; i<findParentProcessItem.size() ; i++){
            findProcessItemCost.add(findParentProcessItem.get(i));
            if(findParentProcessItem.get(i).getGanttTaskType().equals(TaskType.TASK)){continue;}
            else{
                List<VmProcessItemDTO> tempParentProcessItem = findProcessItemCostChildrenRecursive(findParentProcessItem.get(i).getProcessItemId());
                for(int j=0 ; j < tempParentProcessItem.size() ; j++){
                    findProcessItemCost.add(tempParentProcessItem.get(j));
                }
            }
        }
        return findProcessItemCost;
    }

    public boolean isOverlap (List<Long> searchResultProcessItemId, VmProcessItemDTO processItemId){
        List<VmProcessItemDTO> findParent = vmProcessItemDTODslRepository.findProcessItemCostByParentIdCurrentVersion(userInfo.getProjectId(), processItemId.getProcessParentItemId());
        if(findParent.isEmpty()){ // 최상단
            return false;
        }
        else if(searchResultProcessItemId.contains(processItemId.getProcessParentItemId())){
            return true;
        }
        else{
            return isOverlap(searchResultProcessItemId, findParent.get(0));
        }
    }
    public JsonObject findProcessItemCostWithChildrenCurrentVersion(SearchProcessItemCostVO searchProcessItemCostVO) {
        if(searchProcessItemCostVO.getWorkId()==0 && searchProcessItemCostVO.getPhasingCode().isEmpty() && searchProcessItemCostVO.getTaskName().isEmpty() && searchProcessItemCostVO.getStartDate().isEmpty() && searchProcessItemCostVO.getEndDate().isEmpty()){
            return proc.getResult(true, vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO));
        }
        else{
            List<VmProcessItemDTO> findProcessItemCost = new ArrayList<>();
            List<VmProcessItemDTO> findParentProcessItem = vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);
            List<Long> searchResultProcessItemId = processItemCostDslRepository.findProcessItemIdCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);

            if(findParentProcessItem.size() > 0){
                for(int i=0 ; i<findParentProcessItem.size() ; i++){
                    if(isOverlap(searchResultProcessItemId, findParentProcessItem.get(i))){
                        continue;
                    }
                    findProcessItemCost.add(findParentProcessItem.get(i));

                    if(findParentProcessItem.get(i).getGanttTaskType().equals(TaskType.TASK)){continue;}
                    else{
                        List<VmProcessItemDTO> tempParentProcessItem = findProcessItemCostChildrenRecursive(findParentProcessItem.get(i).getProcessItemId());
                        for(int j=0 ; j < tempParentProcessItem.size() ; j++){
                            findProcessItemCost.add(tempParentProcessItem.get(j));
                        }
                    }
                }
            }
            return proc.getResult(true, findProcessItemCost);
        }
    }
    public List<JsonObject> findProcessItemsCostCurrentVersionByPhasingCodes(JsonElement phasingCodes) {
        String phasingCodeString = phasingCodes.toString();
        phasingCodeString = phasingCodeString.replace("[", "");
        phasingCodeString = phasingCodeString.replace("]", "");
        phasingCodeString = phasingCodeString.replace("\"", "");
        String[] phasingCodesList = phasingCodeString.split(",");

        List<JsonObject> processItemsCost = new ArrayList<>();
        for(int i=0 ; i < phasingCodesList.length ; i++){
            if (phasingCodesList[i] != null && !phasingCodesList[i].isEmpty()) {
                processItemsCost.add(proc.getResult(true, vmProcessItemDTODslRepository.findProcessItemsCostCurrentVersionByPhasingCodes(userInfo.getProjectId(), phasingCodesList[i])));
            }
        }
        return processItemsCost;
    }
    public JsonObject findProcessItemCostForPass(SearchProcessItemCostVO searchProcessItemCostVO) {
        return proc.getResult(true, vmProcessItemDTODslRepository.findProcessItemCostForPass(userInfo.getProjectId(), searchProcessItemCostVO));
    }

    public List<ProcessItemCostDetailDTO> findProcessItemCostDetail(Long processItemId, String rowState) {
        return processItemCostDslRepository.findProcessItemCostDetail(userInfo.getProjectId(), processItemId, rowState);
    }

    public List<ProcessItemCostDetailDTO> findCostDetailByProcessId(long processId){
        List<ProcessItemCostDetail> processItemCostDetail = processItemCostDetailRepository.findCostDetailByProcessId(processId);
        List<ProcessItemCostDetailDTO> processItemCostDetailDTOS = new ArrayList<>();
        for(ProcessItemCostDetail item : processItemCostDetail){
            processItemCostDetailDTOS.add(new ProcessItemCostDetailDTO(item));
        }

        return processItemCostDetailDTOS;
    }

    @Transactional
    public void saveProcessItemCostDetail(ProcessItem processItem, List<ProcessItemCostDetailDTO> processItemCostDetailDTOs) {
        processItemCostDetailDTOs
                .forEach(processItemCostDetailDTO ->
                        processItemCostDetailRepository
                                .findById(processItemCostDetailDTO.getProcessItemCodeDetailId())
                                .map(saveProcessItemCostDetail ->
                                        saveProcessItemCostDetail.update(processItemCostDetailDTO, userInfo.getId()))
                                .orElseGet(() ->
                                        processItemCostDetailRepository.save(new ProcessItemCostDetail(processItem, processItemCostDetailDTO, userInfo.getId())))
                );
    }

    public ProcessItem findProcessItemById(Long processItemId) {
        return processItemRepository
                .findById(processItemId)
                .orElseGet(ProcessItem::new);
    }

    public BigDecimal calculateComplexUnitPrice(List<ProcessItemCostDetailDTO> processItemCostDetailDTOs) {
        try {
            return processItemCostDetailDTOs
                    .stream()
                    .filter(t -> t.isFirst())
                    .findFirst()
                    .map(firstItem -> {
                        BigDecimal count = firstItem.getCount();
                        BigDecimal sum =
                                processItemCostDetailDTOs
                                        .stream()
                                        .map(x -> x.getCost())
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        return sum.divide(count, 0, RoundingMode.DOWN);
                    }).orElseGet(()->Utils.getDefaultDecimalError());

        } catch (Exception ex) {
            return Utils.getDefaultDecimalError();
        }
    }

    @Transactional
    public boolean updateProcessItemComplexUnitPrice(List<ProcessItemCostDetailDTO> processItemCostDetailDTOs) {
        ProcessItem processItem;
        try {
            processItem =
                    processItemCostDetailDTOs
                            .stream()
                            .filter(t -> t.isFirst())
                            .findFirst()
                            .map(firstItem -> {
                                BigDecimal count = firstItem.getCount();
                                BigDecimal sum =
                                        processItemCostDetailDTOs
                                                .stream()
                                                .map(x -> x.getCost())
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                BigDecimal complexUnitPrice = sum.divide(count, 0, RoundingMode.DOWN);

                                // 대표수량 변경과 그에 따른 비용 재계산
                                return processItemRepository
                                        .findById(firstItem.getProcessItemId())
                                        .map(savedProcessItem -> savedProcessItem.updateComplexUnitPrice(complexUnitPrice, userInfo.getId()))
                                        .orElseGet(ProcessItem::new);
                            })
                            .orElseGet(ProcessItem::new);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            processItem = new ProcessItem();
        }

        return processItem.getId() > 0;
    }

    @Transactional
    public void deleteProcessItemCostDetail(List<ProcessItemCostDetailDTO> processItemCostDetailDTOs) {
        processItemCostDetailDTOs
                .forEach(processItemCostDetailDTO -> processItemCostDetailRepository.deleteById(processItemCostDetailDTO.getProcessItemCodeDetailId()));
    }

    @Transactional
    public JsonObject setProcessItemBookmark(Long processItemId, boolean isBookmark) {
        return processItemRepository
                .findById(processItemId)
                .map(savedProcessItem -> {
                    if (savedProcessItem.getProcessInfo().getProjectId() != userInfo.getProjectId())
                        return proc.getResult(false, "system.process_cost_service.project_is_not_in_task");

                    return proc.getResult(true, isBookmark? "system.process_cost_service.bookmark_on":"system.process_cost_service.bookmark_off", savedProcessItem.setProcessItemBookmark(isBookmark));
                })
                .orElseGet(() -> proc.getResult(false, "system.process_cost_service.task_is_not"));
    }

    public List<VmProcessItemDTO> findProcessItemCostDetailBookmark() {
        return vmProcessItemDTODslRepository.findProcessItemCostDetailBookmark(userInfo.getProjectId());
    }

    public List<ProcessItemCostPayDTO> findProcessItemCostPay(long processItemId) {
        return processItemCostDslRepository.findProcessItemCostPay(userInfo.getProjectId(), processItemId);
    }

    public List<ProcessItemCostPayDTO> findProcessItemCostPayLatestEditable(long processItemId) {
        List<ProcessItemCostPayDTO> processItemCostPayDTOs = findProcessItemCostPay(processItemId);

        // 가장 최근에 등록한 기성내역만 수정할 수 있게
        if (processItemCostPayDTOs.size() > 0) {
            ProcessItemCostPayDTO processItemCostPayDTO = processItemCostPayDTOs.stream().findFirst().get();
            processItemCostPayDTO.setEdit(true);
        }

        return processItemCostPayDTOs;
    }

    public ProcessItemCostPay getProcessItemCostPayAtLatest(long processItemId) {
        ProcessItemCostPay processItemCostPay = processItemCostDslRepository.getProcessItemCostPayAtLatest(userInfo.getProjectId(), processItemId);

        if (processItemCostPay == null) {
            ProcessItem processItem = processItemRepository.findById(processItemId).orElseGet(ProcessItem::new);

            if (processItem.getId() == 0) throw new NotFoundException(message.getMessage("system.process_cost_service.task_is_not"));
            if (processItem.getProcessInfo().getProjectId() != userInfo.getProjectId()) throw new ForbiddenException(message.getMessage("system.process_cost_service.project_is_not_in_task"));

            processItemCostPay = new ProcessItemCostPay(processItem);
        }
        return processItemCostPay;
    }

    @Transactional
    public JsonObject postPaidCost(Long processItemId, ProcessItemCostPayDTO processItemCostPayDTO) {
        return processItemRepository
                .findById(processItemId)
                .map(savedProcessItem -> {
                    if (savedProcessItem.getProcessInfo().getProjectId() != userInfo.getProjectId())
                        return proc.getResult(false, "system.process_cost_service.project_is_not_in_task");

                    List<ProcessItemCostPayDTO> savedProcessItemCostPayDTOs = findProcessItemCostPay(processItemId);
                    ProcessItemCostPay newProcessItemCostPay = new ProcessItemCostPay(savedProcessItem, processItemCostPayDTO, userInfo.getId());

                    // 신규 기성내역 저장 시 누적 진행률이 100%를 오버하는지 체크
                    // progressRate 진행률 합계
                    BigDecimal savedSumProgressRate = savedProcessItemCostPayDTOs.stream().map(t -> t.getProgressRate()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (savedSumProgressRate.add(newProcessItemCostPay.getProgressRate()).compareTo(new BigDecimal("100")) > 0)
                        return proc.getResult(false, "system.process_cost_service.task_progress_rate_over_100");

                    // 신규 기성내역 저장 시 누적 기성금이 총 공사비를 오버하는지 체크
                    // cost 기성금 합계
                    BigDecimal savedSumCost = savedProcessItemCostPayDTOs.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal taskCost = savedProcessItem.getCost();       // 총 공사비
                    BigDecimal thisCost = newProcessItemCostPay.getCost();  // 신규 기성금
                    if (taskCost.compareTo(savedSumCost.add(thisCost)) < 0)
                        return proc.getResult(false, "system.process_cost_service.task_cost_over");

                    // 기성 내역 등록
                    processItemCostPayRepository.save(newProcessItemCostPay);

                    // 내역관리 기성 금액 업데이트
                    savedProcessItem.updateCost(newProcessItemCostPay.getSumCost(), newProcessItemCostPay.getSumProgressRate(), savedProcessItem.getPaidCost(), savedProcessItem.getPaidProgressRate(), userInfo.getId());

                    // 신규 등록 된 기성 내역 목록에 추가
                    ProcessItemCostPayDTO newProcessItemCostPayDTO = new ProcessItemCostPayDTO(newProcessItemCostPay);
                    newProcessItemCostPayDTO.setEdit(true);
                    savedProcessItemCostPayDTOs.add(0, newProcessItemCostPayDTO);

                    JsonObject result = proc.getResult(true, "system.process_cost_service.add_success", savedProcessItemCostPayDTOs);

                    proc.addProperty(result, "processItemCostPayAtCurrent", newProcessItemCostPay.getPredictedCostPayAtCurrent());

                    return result;
                })
                .orElseGet(() -> proc.getResult(false, "system.process_cost_service.task_is_not"));
    }

    @Transactional
    public JsonObject putPaidCost(long processItemCostPayId, ProcessItemCostPayDTO processItemCostPayDTO) {
        return processItemCostPayRepository
                .findById(processItemCostPayId)
                .map(savedProcessItemCostPay -> {
                    if (savedProcessItemCostPay.getProcessItem().getProcessInfo().getProjectId() != userInfo.getProjectId())
                        return proc.getResult(false, "system.process_cost_service.project_is_not_in_task");

                    List<ProcessItemCostPayDTO> savedProcessItemCostPayDTOs = findProcessItemCostPayLatestEditable(processItemCostPayDTO.getProcessItemId());

                    // 저장된 목록에서 수정 대상 찾기
                    ProcessItemCostPayDTO savedProcessItemCostPayDTO =
                            savedProcessItemCostPayDTOs
                                    .stream()
                                    .filter(t -> t.isEdit())
                                    .findFirst()
                                    .orElseGet(ProcessItemCostPayDTO::new);

                    if (savedProcessItemCostPayDTO.getProcessItemCostPayId() == 0)
                        return proc.getResult(false, "system.process_cost_service.task_is_not_editable_paid_cost_detail");

                    if (savedProcessItemCostPayDTO.getProcessItemCostPayId() != processItemCostPayDTO.getProcessItemCostPayId())
                        return proc.getResult(false, "system.process_cost_service.paid_cost_detail_can_not_edit_latest_not");

                    // 검증이 필요한 항목만 변경
                    savedProcessItemCostPayDTO.setProgress(processItemCostPayDTO.getProgress());
                    savedProcessItemCostPayDTO.setCost(processItemCostPayDTO.getCost());

                    // 신규 기성내역 저장 시 누적 진행률이 100%를 오버하는지 체크
                    int sumProgress = savedProcessItemCostPayDTOs.stream().map(t -> t.getProgress()).reduce(0, Integer::sum);
                    if (sumProgress > 100) return proc.getResult(false, "system.process_cost_service.task_progress_rate_over_100");
                    if (sumProgress < 0) return proc.getResult(false, "system.process_cost_service.task_progress_rate_is_not_minus");

                    // 신규 기성내역 저장 시 누적 기성금이 총 공사비를 오버하는지 체크
                    BigInteger sumCost = savedProcessItemCostPayDTOs.stream().map(t -> t.getCost()).reduce(BigInteger.ZERO, BigInteger::add);
                    BigInteger taskCost = savedProcessItemCostPay.getProcessItem().getCost().toBigInteger();

                    if (taskCost.compareTo(sumCost) < 0)
                        return proc.getResult(false, "system.process_cost_service.task_cost_over");

                    // 기성내역 업데이트
                    savedProcessItemCostPay.update(processItemCostPayDTO, userInfo.getId());

                    // 내역관리 기성 금액 업데이트
                    savedProcessItemCostPay.getProcessItem().updateCost(savedProcessItemCostPay.getSumCost(), savedProcessItemCostPay.getSumProgressRate(),  savedProcessItemCostPay.getProcessItem().getPaidCost(),  savedProcessItemCostPay.getProcessItem().getPaidProgressRate(), userInfo.getId());

                    // 기성내역 목록 업데이트
                    ProcessItemCostPayDTO updatedProcessItemCostPayDTO = new ProcessItemCostPayDTO(savedProcessItemCostPay);
                    updatedProcessItemCostPayDTO.setEdit(true);
                    savedProcessItemCostPayDTOs.set(0, updatedProcessItemCostPayDTO);

                    JsonObject result = proc.getResult(true, "system.process_cost_service.edit_success", savedProcessItemCostPayDTOs);

                    proc.addProperty(result, "processItemCostPayAtCurrent", savedProcessItemCostPay.getPredictedCostPayAtCurrent());

                    return result;
                })
                .orElseGet(() -> proc.getResult(false, "system.process_cost_service.paid_cost_detail_is_not"));
    }

    public PaidCostAllDTO getProcessItemCostPayAtLatestCostNo(long processInfoId) {
        PaidCostAllDTO paidCostAllDTO = processItemCostDslRepository.getProcessItemCostPayLatestCostNo(userInfo.getProjectId(), processInfoId);

        if (paidCostAllDTO == null) {
            paidCostAllDTO = new PaidCostAllDTO();
            paidCostAllDTO.setCostDate(Utils.getDateTimeByNationAndFormatType(LocalDateTime.now(), DateFormatEnum.YEAR_MONTH_DAY));
            paidCostAllDTO.setCostNo(1);
            paidCostAllDTO.setDescription("");
        }

        return paidCostAllDTO;
    }

    @Transactional
    public JsonObject postProcessItemCostPayAll(Long processInfoId, PaidCostAllDTO paidCostAllDTO) {
        return processInfoRepository
                .findByIdAndProjectId(processInfoId, userInfo.getProjectId())
                .map(savedProcessInfo -> {
                    // 작업 목록 조회
                    List<ProcessItem> savedProcessItems =
                            savedProcessInfo
                                    .getProcessItems()
                                    .stream()
                                    // 지난 기성 누적 진행율보다 현 공정 진행율이 커야 한다.
                                    // 공사 비용이 0 보다 커야 한다.
                                    .filter(t -> t.getProgressRate().compareTo(t.getPaidProgressRate()) > 0
                                            && t.getCost().compareTo(Utils.getDefaultDecimal()) > 0)
                                    .collect(Collectors.toList());

                    if (savedProcessItems == null && savedProcessItems.size() == 0)
                        return proc.getResult(true, "system.process_cost_service.paid_cost_detail_is_not_target");

                    // 작업 별 기성내역 등록
                    savedProcessItems.forEach(savedProcessItem -> {
                        // 작업 최근 기성내역 조회
                        ProcessItemCostPay latestProcessItemCostPay = processItemCostDslRepository.getProcessItemCostPayAtLatest(userInfo.getProjectId(), savedProcessItem.getId());

                        // 작업 최근 기성내역이 없는 경우 기본 기성내역 생성
                        if (latestProcessItemCostPay == null) latestProcessItemCostPay = new ProcessItemCostPay(savedProcessItem);

                        // 작업 최근 기성내역에서 현재 작업률로 기성내역을 계산해서 만들기 위한 예상 기성내역 생성
                        ProcessItemCostPayDTO predictedProcessItemCostPay = latestProcessItemCostPay.getPredictedCostPayAtCurrent(paidCostAllDTO);

                        // 예상 기성내역으로 기성내역 생성 후 저장
                        ProcessItemCostPay newProcessItemCostPay = processItemCostPayRepository.save(new ProcessItemCostPay(savedProcessItem, predictedProcessItemCostPay, userInfo.getId()));

                        // 작업 기성 내역 변경
                        savedProcessItem.updateCost(newProcessItemCostPay.getSumCost(), newProcessItemCostPay.getSumProgressRate(), savedProcessItem.getPaidCost(), savedProcessItem.getPaidProgressRate(), userInfo.getId());
                    });

                    return proc.getResult(true, "system.process_cost_service.paid_cost_process_task", new String[]{savedProcessItems.size()+""});
                })
                .orElseGet(() -> proc.getResult(false, "system.process_cost_service.project_is_not_in_task"));

    }

    @Transactional
    public JsonObject putFormulaEvaluation(ProcessItemCostDTO processItemCostDTO, long projectId) {

        // 속성, 집계 변수에 따라 값을 구한다.
        List<FormulaVariableValueDTO> formulaVariableValueDTOs = findAttributeValueForPropertyOrAggregation(processItemCostDTO, projectId);

        // 변수 중 하나라도 문제가 있다면 결과 반환
        if(isFail(formulaVariableValueDTOs)) {
            processItemCostDTO.setCalculateCostResult(message.getMessage("system.process_cost_service.fail") + getFailMessage(formulaVariableValueDTOs));
            return proc.getResult(false, "system.process_cost_service.formula_is_not_aggregation_or_property_or_data", processItemCostDTO);
        }

        // 계산식 치환
        String replacedFormula = replaceFormula(processItemCostDTO.getFirstCountFormula(), formulaVariableValueDTOs);

        // 계산식 평가
        if(evaluateFormula(replacedFormula, processItemCostDTO)) {
            return // task 비용 계산 및 저장
                    processItemRepository
                            .findById(processItemCostDTO.getProcessItemId())
                            .map(savedProcessItem -> {
                                if(savedProcessItem.getProcessInfo().getProjectId() != userInfo.getProjectId()) {
                                    processItemCostDTO.setCalculateCostResult(message.getMessage("system.process_cost_service.fail_project_is_not"));
                                    return proc.getResult(false, "system.process_cost_service.fail_formula_set_and_detail_save", processItemCostDTO);
                                }

                                savedProcessItem.updateFirstCountFormula(processItemCostDTO, userInfo.getId());
                                return proc.getResult(true,"system.process_cost_service.success_formula_and_save", processItemCostDTO);
                            })
                            .orElseGet(()->{
                                processItemCostDTO.setCalculateCostResult(message.getMessage("system.process_cost_service.fail_no_task"));
                                return proc.getResult(false,"system.process_cost_service.fail_formula_set_and_detail_save_proc",processItemCostDTO);
                            });
        } else {
            processItemCostDTO.setCalculateCostResult(message.getMessage("system.process_cost_service.fail_formula_error"));
            return proc.getResult(false, "system.process_cost_service.fail_formula_error",processItemCostDTO);
        }
    }

    @NotNull
    private String getFailMessage(List<FormulaVariableValueDTO> formulaVariableValueDTOs) {
        return formulaVariableValueDTOs
                .stream()
                .filter(t -> !t.isSuccess())
                .map(y -> y.getResult())
                .collect(Collectors.joining("|"));
    }

    @Nullable
    private boolean evaluateFormula(String replacedFormula, ProcessItemCostDTO processItemCostDTO) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        try {
            Object result = engine.eval(replacedFormula);
            processItemCostDTO.setFirstCount(new BigDecimal(result.toString()));
        } catch (ScriptException scriptException) {
            return false;
        }

        return true;
    }

    private String replaceFormula(String firstCountFormula, List<FormulaVariableValueDTO> formulaVariableValueDTOs) {
        String replacedFormula = firstCountFormula;

        for(FormulaVariableValueDTO formulaVariableValueDTO : formulaVariableValueDTOs) {
            replacedFormula = replacedFormula.replace(formulaVariableValueDTO.getVariable(), formulaVariableValueDTO.getValue() + "");
        }

        return replacedFormula;
    }

    private boolean isFail(List<FormulaVariableValueDTO> formulaVariableValueDTOs) {
        return formulaVariableValueDTOs
                .stream()
                .filter(t -> !t.isSuccess())
                .count() > 0;
    }

    @NotNull
    private List<FormulaVariableValueDTO> findAttributeValueForPropertyOrAggregation(ProcessItemCostDTO processItemCostDTO, long projectId) {
        return Arrays
                .stream(processItemCostDTO.getVariables())
                .map(formulaVariable -> getFormulaVariableValue(formulaVariable, projectId ))
                .collect(Collectors.toList());
    }

    public FormulaVariableValueDTO getFormulaVariableValue(String variable, long projectId) {
        boolean isSuccess = false;
        String phasingCodeConfigValue = configService.findConfigForCache("MODEL", "WBS_CODE",projectId);

        // 집계
        if (variable.indexOf("#") > 0) {
            String phasingCode = variable.split("#")[0];
            String aggregation = variable.split("#")[1].toLowerCase(Locale.ROOT);

            List<ModelingAttribute> modelingAttributes = modelingAttributeDslRepository.findModelingAttributePhasingCodeCount(userInfo.getProjectId(), phasingCodeConfigValue, phasingCode);

            if ("count".equals(aggregation)) {
                int count = modelingAttributes.size();
                isSuccess = count > 0;

                if (isSuccess) return new FormulaVariableValueDTO(variable, BigDecimal.valueOf(count), "", isSuccess);
                return new FormulaVariableValueDTO(variable, Utils.getDefaultDecimal(), message.getMessage("system.process_cost_service.aggregation_no_data",new String[]{variable}), isSuccess);
            }

            return new FormulaVariableValueDTO(variable, Utils.getDefaultDecimal(),message.getMessage("system.process_cost_service.aggregation_not_support",new String[]{aggregation}), isSuccess);
        }

        // 속성
        if (variable.indexOf("@") > 0) {
            String phasingCode = variable.split("@")[0];
            String property = variable.split("@")[1].toLowerCase(Locale.ROOT);

            List<ModelingAttribute> modelingAttributes = modelingAttributeDslRepository.findModelingAttributeFormulaByProperty(userInfo.getProjectId(), phasingCodeConfigValue, phasingCode, getModelAttributeNameForProperty(property));

            if (modelingAttributes.size() == 0) return new FormulaVariableValueDTO(variable, Utils.getDefaultDecimal(), message.getMessage("system.process_cost_service.aggregation_no_data",new String[]{variable}), isSuccess);

            BigDecimal value = Utils.getDefaultDecimal();
            for(ModelingAttribute modelingAttribute :modelingAttributes) {
                value = value.add(new BigDecimal(modelingAttribute.getAttributeValue()));
            };

            isSuccess = value.compareTo(Utils.getDefaultDecimal()) > 0;

            if (isSuccess) return new FormulaVariableValueDTO(variable, value, "", isSuccess);
            return new FormulaVariableValueDTO(variable, Utils.getDefaultDecimal(), message.getMessage("system.process_cost_service.property_value_0",new String[]{property}), isSuccess);
        }

        return new FormulaVariableValueDTO(variable, Utils.getDefaultDecimal(), message.getMessage("system.process_cost_service.property_not_support_variable",new String[]{variable}), isSuccess);
    }

    private String getModelAttributeNameForProperty(String property) {
        return "Text/" + Utils.convertFirstCharacterToUpperCase(property);
    }

    @Transactional
    public JsonObject putProcessItemForCost(String colId, ProcessItemCostDTO processItemCostDTO) {
        return processItemRepository
                .findById(processItemCostDTO.getProcessItemId())
                .map(savedProcessItem->{
                    if(savedProcessItem.getProcessInfo().getProjectId() != userInfo.getProjectId())
                        return proc.getResult(false, "system.process_cost_service.project_is_not_in_task");

                    if(savedProcessItem.getPaidCost().compareTo(Utils.getDefaultDecimal()) > 0)
                        return proc.getResult(false, "system.process_cost_service.paid_cost_is_edit_can_not");

                    // 그리드에서 저장이 안된 값이 넘어와 갱신되면 안되므로 실제 변경 단위한 한 셀이기 때문에 개별 업데이트
                    if("firstCount".equals(colId)) {
                        processItemCostDTO.setFirstCountFormula("");
                        savedProcessItem.updateFirstCountFormula(processItemCostDTO, userInfo.getId());
                    }
                    else if("firstCountUnit".equals(colId)) savedProcessItem.updateFirstCountUnit(processItemCostDTO, userInfo.getId());
                    else if("complexUnitPrice".equals(colId)) savedProcessItem.updateComplexUnitPrice(processItemCostDTO, userInfo.getId());
                    else return proc.getResult(false,"system.process_cost_service.item_can_not_save",processItemCostDTO);

                    return proc.getResult(true,"system.process_cost_service.save_success", processItemCostDTO);
                }).orElseGet(()->proc.getResult(false,"system.process_cost_service.task_is_not",processItemCostDTO));
    }

    public JsonObject procProcessItemDetails(Long processItemId, List<ProcessItemCostDetailDTO> processItemCostDetailVOs, String proType) {
        ProcessItem processItem = findProcessItemById(processItemId);
        for (int i = 0; i < processItemCostDetailVOs.size(); i++) {
            ProcessItemCostDetailDTO processItemCostDetailVO = processItemCostDetailVOs.get(i);
            System.out.println("--------------------------------------------- processItemCostDetailVO : " + processItemCostDetailVO);
        }
        /**
        List<ProcessItemCostDetailDTO> processItemCostDetailVOs = new ArrayList<>();
        for (int i = 0; i < processItemCostDetailIds.size(); i++) {
            ProcessItemCostDetail processItemCostDetail = processItemCostDetailRepository.findById(processItemCostDetailIds.get(i)).orElseGet(ProcessItemCostDetail::new);
            if (processItemCostDetail.getProcessItem().getId() == processItemId) {
                processItemCostDetailVOs.add(new ProcessItemCostDetailDTO(processItemCostDetail, ""));
            } else {
                processItemCostDetailVOs.add(new ProcessItemCostDetailDTO(processItemCostDetail, "C"));
            }
            System.out.println(processItemCostDetailVOs.get(i).getProcessItemCodeDetailId() + "--------------------------------------");
        }
        **/
        if(processItem.getId() == 0)
            return proc.getResult(false,"system.process_cost_service.task_is_not");

        if(processItem.getProcessInfo().getProjectId() != userInfo.getProjectId())
            return proc.getResult(false,"system.process_cost_service.project_is_not_in_task");

        if(processItem.getPaidCost().compareTo(Utils.getDefaultDecimal())>0) {
            if("save".equals(proType)) {
                BigDecimal calculatedComplexUnitPrice = calculateComplexUnitPrice(processItemCostDetailVOs);
                System.out.println(calculatedComplexUnitPrice + "--------------------------------------calculatedComplexUnitPrice");
                if (processItem.getPaidCost().compareTo(calculatedComplexUnitPrice) != 0 )
                    return proc.getResult(false, "system.process_cost_service.paid_cost_is_the_value_must_same");
            }
            if("delete".equals(proType)) return proc.getResult(false,"system.process_cost_service.paid_cost_is_can_not_delete");
        }

        String resultMessage = procProcessItemDetails(processItemCostDetailVOs, proType, processItem);

        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = findProcessItemCostDetail(processItemId, "R");

        if(updateProcessItemComplexUnitPrice(processItemCostDetailDTOs))
            resultMessage += processItem.getTitle() + " " + message.getMessage("system.process_cost_service.base_and_cost_recalculate");

        return proc.getMessageResult(true, resultMessage, processItemCostDetailDTOs);
    }

    private boolean onlyOneIsFirst(List<JsonObject> processItemCostDetail){
        int count = 0;

        for(int i=0 ; i < processItemCostDetail.size() ; i++){
            String isCheck = processItemCostDetail.get(i).get("대표") == null ? "" : processItemCostDetail.get(i).get("대표").toString().replaceAll("^\"|\"$", "").trim();
            if(isCheck.equals("o") || isCheck.equals("O") || isCheck.equals("v") || isCheck.equals("V") || isCheck.equals("0") ){
                count++;
            }
        }
        if(count <= 1){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean insertProcessItemCostDetailExcel(List<JsonObject> processItemCostDetail, Long processId){
        String taskName = processItemCostDetail.get(0).get("공정명") == null ? "" : processItemCostDetail.get(0).get("공정명").toString().replaceAll("^\"|\"$", "").trim();
        String phasingCode = processItemCostDetail.get(0).get("공정코드") == null ? "" : processItemCostDetail.get(0).get("공정코드").toString().replaceAll("^\"|\"$", "").trim();
        List<Long> processItemId = processItemRepository.findByProcessIdAndTaskNameAndPhasingCode(processId, taskName, phasingCode);
        String code = "";
        String name = "";
        BigDecimal count = new BigDecimal("0.00");
        String unit = "";
        BigDecimal unitPrice = new BigDecimal("0.00");;
        BigDecimal cost = new BigDecimal("0.00");;
        boolean isFirst = false;

        long userId = userInfo.getId();
        LocalDateTime writeDate =  LocalDateTime.now();

        if(processItemId.size() == 0){
            return false;
        }

        //기존에 저장 되어 있던 값들 삭제
        List<ProcessItemCostDetail> haveCostDetail = processItemCostDetailRepository.findByProcessItemId(processItemId.get(0));
        if(haveCostDetail.size() > 0){
            processItemCostDetailRepository.deleteByProcessItemId(processItemId.get(0));
        }

        for(int i=0 ; i < processItemCostDetail.size() ; i++){
            code = processItemCostDetail.get(i).get("코드") == null ? "" : processItemCostDetail.get(i).get("코드").toString().replaceAll("^\"|\"$", "").trim();
            name = processItemCostDetail.get(i).get("명") == null ? "" : processItemCostDetail.get(i).get("명").toString().replaceAll("^\"|\"$", "").trim();
            count = new BigDecimal( processItemCostDetail.get(i).get("값") == null ? "0.00" : processItemCostDetail.get(i).get("값").toString().replaceAll(",", "").replaceAll("^\"|\"$", "").trim() );
            unit = processItemCostDetail.get(i).get("단위") == null ? "" : processItemCostDetail.get(i).get("단위").toString().replaceAll("^\"|\"$", "").trim();
            unitPrice = new BigDecimal( processItemCostDetail.get(i).get("단가") == null ? "0.00" : processItemCostDetail.get(i).get("단가").toString().replaceAll(",", "").replaceAll("^\"|\"$", "").trim() );

            String isCheck = processItemCostDetail.get(i).get("대표") == null ? "" : processItemCostDetail.get(i).get("대표").toString().replaceAll("^\"|\"$", "").trim();
            if(isCheck.equals("o") || isCheck.equals("O") || isCheck.equals("v") || isCheck.equals("V") || isCheck.equals("0") ){
                isFirst = true;
            }
            else{
                isFirst = false;
            }

            cost = count.multiply(unitPrice).setScale(0, RoundingMode.DOWN);
            processItemCostDetailRepository.insertProcessItemCostDetailExcel(processItemId.get(0), code, name, count, unit, unitPrice, cost, isFirst, userId, writeDate, userId, writeDate);
        }

        //process_item 테이블도 업데이트 해야 화면에 정상적으로 출력 됨
        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = findProcessItemCostDetail(processItemId.get(0), "R");
        if(updateProcessItemComplexUnitPrice(processItemCostDetailDTOs)){
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional
    public JsonObject procProcessItemDetailsExcel(List<JsonObject> processItemCostDetailJsonObject){
        long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();

        try{
            if (processItemCostDetailJsonObject.size() == 1){ // 입력하는 excel 데이터의 길이가 1개라면
                if(processItemCostDetailJsonObject.get(0).get("공정명") != null && processItemCostDetailJsonObject.get(0).get("공정코드") != null){
                    insertProcessItemCostDetailExcel(processItemCostDetailJsonObject, processId);
                    return proc.getMessageResult(true, "저장 완료");
                }
                else{
                    return proc.getMessageResult(false, "저장 실패");
                }
            }
            else{
                String currentTaskName = processItemCostDetailJsonObject.get(0).get("공정명").toString();
                String currentPhasingCode = processItemCostDetailJsonObject.get(0).get("공정코드").toString();
                int objectSize = processItemCostDetailJsonObject.size();
                List<JsonObject> processItemCostDetail = new ArrayList<>();
                for(int i=0 ; i < objectSize ; i++){
                    //아무것도 입력이 안된 라인인지 확인
                    if(processItemCostDetailJsonObject.get(0).get("공정명") == null || processItemCostDetailJsonObject.get(0).get("공정코드") == null){
                        if(processItemCostDetailJsonObject.size() == 1){
                            if(onlyOneIsFirst(processItemCostDetail)){
                                insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                                return proc.getMessageResult(true, "저장 완료");
                            }
                            else{
                                return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                            }
                        }
                        else{
                            processItemCostDetailJsonObject.remove(0);
                            continue;
                        }
                    }
                    // 같은 공정들인지 확인
                    else if( currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) && currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                    }
                    // 다른 공정이 발생한 경우(지금까지의 공정을 DB에 저장)
                    else if( !currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) || !currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        if(onlyOneIsFirst(processItemCostDetail)){
                            insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                            processItemCostDetail = new ArrayList<>();
                            processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                            currentTaskName = processItemCostDetailJsonObject.get(0).get("공정명") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정명").toString();
                            currentPhasingCode = processItemCostDetailJsonObject.get(0).get("공정코드") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정코드").toString();
                        }
                        else{
                            return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                        }
                    }

                    if (processItemCostDetailJsonObject.size() == 1){
                        if(onlyOneIsFirst(processItemCostDetail)){
                            insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                            return proc.getMessageResult(true, "저장 완료");
                        }
                        else{
                            return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                        }
                    }
                    else{
                        processItemCostDetailJsonObject.remove(0);
                    }
                }
                return proc.getMessageResult(false, "저장 실패");
            }
        } catch(Exception e){
            System.out.println(e);
            return proc.getMessageResult(false, "저장 실패 : " + e);
        }
    }

    private String procProcessItemDetails(List<ProcessItemCostDetailDTO> processItemCostDetailVOs, String proType, ProcessItem processItem) {
        if("save".equals(proType)) {
            saveProcessItemCostDetail(processItem, processItemCostDetailVOs);
            return message.getMessage("system.process_cost_service.save_success");
        }
        if("delete".equals(proType)) {
            deleteProcessItemCostDetail(processItemCostDetailVOs);
            return message.getMessage("system.process_cost_service.delete_success");
        }

        return message.getMessage("system.process_cost_service.no_item");
    }

    public List<VmProcessItemDTO> listToTreeItems(List<VmProcessItemDTO> processItemCostCurrentVersion){
        List<VmProcessItemDTO> model = new ArrayList<>();
        String startDate = "2999-12-31";
        String endDate = "2000-01-01";
        int minDepth = 10, maxDepth = 0;
        int sumTotDataCount = 0;
        int sumTotDuration = 0;
        BigDecimal sumTotProgressDurationRate = new BigDecimal(0);
        BigInteger sumTotTaskCost = new BigInteger("0");
        BigDecimal sumTotPaidCost = new BigDecimal(0);
        for(VmProcessItemDTO m : processItemCostCurrentVersion){
            if(m.getTaskDepth()>0){
                if(maxDepth < m.getTaskDepth()){
                    maxDepth = m.getTaskDepth();
                }
                if(minDepth > m.getTaskDepth()){
                    minDepth = m.getTaskDepth();
                }
            }
            if(m.getGanttTaskType().getValue().equals("task")){
                sumTotDataCount++;
                sumTotDuration += m.getDuration();
                sumTotProgressDurationRate =sumTotProgressDurationRate.add(m.getProgressRate().multiply(new BigDecimal(0.01)).multiply(BigDecimal.valueOf(m.getDuration())));
                sumTotTaskCost = sumTotTaskCost.add(m.getTaskCost());
                sumTotPaidCost = sumTotPaidCost.add(m.getPaidCost());

                /*
                if(!m.getStartDate().isEmpty()){
                    startDate = LocalDate.parse( m.getStartDate() ).isBefore( LocalDate.parse(startDate) ) ? m.getStartDate() : startDate;
                }
                if(!m.getEndDate().isEmpty()){
                    endDate = LocalDate.parse( m.getEndDate() ).isAfter(LocalDate.parse(endDate)) ? m.getEndDate() : endDate;
                }
                 */
                if(m.getStartDate().isEmpty()){ m.setStartDate(startDate); }
                else { startDate = LocalDate.parse(m.getStartDate()).isBefore( LocalDate.parse(startDate)) ? m.getStartDate() : startDate; }

                if(m.getEndDate().isEmpty()){ m.setEndDate(endDate); }
                else { endDate = LocalDate.parse(m.getEndDate()).isBefore( LocalDate.parse(endDate)) ? m.getEndDate() : endDate; }
            }
        }

        for(int depth=maxDepth; depth>=minDepth; depth--){
            for(VmProcessItemDTO m : processItemCostCurrentVersion) {
                if(depth == m.getTaskDepth()){
                    if(m.getGanttTaskType().getValue().equals("project")) {
                        m.setProgressRate(new BigDecimal(0));
                        m.setFirstCount(new BigDecimal(0));
                        m.setDuration(0);
                        m.setComplexUnitPrice(new BigDecimal(0));
                        m.setStartDate("");
                        m.setEndDate("");
                        BigInteger sumDataTaskCost = new BigInteger("0");
                        BigDecimal sumDataPaidCost = new BigDecimal(0);
                        int sumDataDuration = 0;
                        BigDecimal sumDataProgressDurationRate = new BigDecimal(0);
                        String typeProjectStartDate = "2999-12-31";
                        String typeProjectEndDate = "2000-01-01";
                        for(VmProcessItemDTO mm : processItemCostCurrentVersion){
                            if(m.getProcessItemId() == mm.getProcessParentItemId()) {
                                sumDataTaskCost = sumDataTaskCost.add(mm.getTaskCost());
                                sumDataPaidCost = sumDataPaidCost.add(mm.getPaidCost());
                                sumDataDuration += mm.getDuration();
                                sumDataProgressDurationRate = sumDataProgressDurationRate.add( mm.getProgressRate().multiply(BigDecimal.valueOf(mm.getDuration())) );

                                if(mm.getStartDate().isEmpty()){ mm.setStartDate(typeProjectStartDate); }
                                else { typeProjectStartDate = LocalDate.parse(mm.getStartDate()).isBefore( LocalDate.parse(typeProjectStartDate)) ? mm.getStartDate() : typeProjectStartDate; }

                                if(mm.getEndDate().isEmpty()){ mm.setStartDate(typeProjectEndDate); }
                                else{ typeProjectEndDate = LocalDate.parse(mm.getEndDate()).isAfter( LocalDate.parse(typeProjectEndDate) ) ? mm.getEndDate() : typeProjectEndDate; }
                            }
                        }
                        m.setTaskCost(sumDataTaskCost);
                        m.setPaidCost(sumDataPaidCost);
                        m.setDuration(sumDataDuration);

                        if(sumDataDuration == 0){ m.setProgressRate(new BigDecimal(0)); }
                        else{ m.setProgressRate(sumDataProgressDurationRate.divide(BigDecimal.valueOf(sumDataDuration), 4, RoundingMode.HALF_UP) ); }

                        m.setStartDate(typeProjectStartDate);
                        m.setEndDate(typeProjectEndDate);
                    }
                }
                if(depth == minDepth){
                    model.add(m);
                }
            }
        }
        return model;
    }

    public List<String> getProcessItemSumCost(long processId) {
        List<String> taskName = processItemRepository.findByProcessIdAndTaskNameAndParentRowNum(processId);
        return taskName;
    }

    public long getJobSheetProcessItemCount(String phasingCode, long processItemId) {
        return jobSheetProcessItemRepository.getJobSheetProcessItemCount(phasingCode, processItemId).orElseGet(() -> 0L);
    }

    public JsonObject postProcessItemCostDetailExcelFile() {
        try {
            ProcessItemCostDetailExcelFile processItemCostDetailExcelFile = new ProcessItemCostDetailExcelFile();
            processItemCostDetailExcelFile.setProcessItemCostDetailExcelFileAtAddProcessItemCostDetailExcelFile(userInfo);
            processItemCostDetailExcelFileRepository.save(processItemCostDetailExcelFile);

            return proc.getResult(true, processItemCostDetailExcelFile.getId(), "system.process_cost_service.make_process_item_cost_detail_excel_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
