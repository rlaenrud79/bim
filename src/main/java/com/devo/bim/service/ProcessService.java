package com.devo.bim.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.*;
import com.devo.bim.repository.spring.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.ProcessValidateStatus;
import com.devo.bim.model.enumulator.RoleCode;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcessService extends AbstractService {

    private final ProjectRepository projectRepository;
    private final HolidayDTODslRepository holidayDTODslRepository;
    private final TaskDTODslRepository taskDTODslRepository;
    private final ProcessItemLinkDslRepository processItemLinkDslRepository;
    private final ProcessInfoRepository processInfoRepository;
    private final ProcessItemRepository processItemRepository;
    private final ProcessItemLinkRepository processItemLinkRepository;
    private final ProcessItemDslRepository processItemDslRepository;
    private final WorkService workService;
    private final ProcessDTODslRepository processDTODslRepository;
    private final ProcessItemCostDetailRepository processItemCostDetailRepository;
    private final ProcessItemCostPayRepository processItemCostPayRepository;
    private final ProcessInfoDTODslRepository processInfoDTODslRepository;
    private final SelectProgressConfigRepository selectProgressConfigRepository;
    private final ConfigService configService;
    private final ModelingAttributeDslRepository modelingAttributeDslRepository;
    private final AccountDslRepository accountDslRepository;
    private final AlertService alertService;
    private final MainProgressDTODslRepository mainProgressDTODslRepository;
    private final SimulationDslRepository simulationDslRepository;

    private final ProcessCostService processCostService;
    private final ProcessItemCategoryDslRepository processItemCategoryDslRepository;
    private final ProcessItemCategoryRepository processItemCategoryRepository;
    private final ProcessItemExcelFileRepository processItemExcelFileRepository;

    public JsonObject getGanttDataInit() {

        ProcessItemDTO processItemDTOS = new ProcessItemDTO();
        processItemDTOS.setTasks(getProcessItemCurrentVersion());
        processItemDTOS.setLinks(getLinkDTOsCurrentVersion());

        return proc.getResult(true, processItemDTOS);
    }

    private List<TaskDTO> getProcessItemCurrentVersion() {
        return taskDTODslRepository.findProcessItemsCurrentVersion(userInfo.getProjectId());
    }

    private List<LinkDTO> getLinkDTOsCurrentVersion() {
        return processItemLinkDslRepository.findLinkDTOsCurrentVersion(userInfo.getProjectId());
    }

    public JsonObject getGanttData(long processId) {

        ProcessItemDTO processItemDTOS = new ProcessItemDTO();
        processItemDTOS.setTasks(taskDTODslRepository.findProcessItemsByProcessId(userInfo.getProjectId(), processId));
        processItemDTOS.setLinks(processItemLinkDslRepository.findProcessItemLinksByProcessId(userInfo.getProjectId(), processId));

        return proc.getResult(true, processItemDTOS);
    }

    public List<HolidayClassifiedWorkDTO> getHolidaysClassifiedWorkId() {

        // 0. 리턴할 List 선언
        List<HolidayClassifiedWorkDTO> holidays = new ArrayList<>();

        // 1. 프로젝트에서 사용하는 공종 조회
        List<Work> works = workService.findAll();

        // 2. project 시작일과 마지막 일자 조회용
        Project project = projectRepository.findById(userInfo.getProjectId()).orElseGet(Project::new);

        // 3. 전체 공종 휴일 정보 조회
        List<HolidayDTO> allWorksHolidayDTOs = holidayDTODslRepository.findHolidaysAllWorkByProjectId(project.getId()
                , project.getStartDate().getYear()
                , project.getStartDate().getMonth().getValue()
                , project.getEndDate().getYear()
                , project.getEndDate().getMonth().getValue() + 1
                , "HOLIDAY");

        // 4. 전체 휴일 문자열 저장용 임시 List
        List<String> tmpHolidayList = new ArrayList<>();

        // 공종별 holidayDTOs 들중 휴일정보를 추출
        allWorksHolidayDTOs.forEach(t -> {
            for (int idx = 0; idx < t.getHolidayCnt(); idx++) {
                tmpHolidayList.add(Utils.getDateTimeByNationAndFormatType(ChronoUnit.DAYS.addTo(t.getStartDate(), idx), DateFormatEnum.GANTT));
            }
        });

        // 4. 최종 리턴형으로 생성해서 리턴
        holidays.add(new HolidayClassifiedWorkDTO ("0", tmpHolidayList.stream().map(o -> String.valueOf(o)).collect(Collectors.joining("||"))));

        // 5. 공종별 그룹핑할 휴일 정보 조회
        List<HolidayDTO> classifiedWorkHolidayDTOs = holidayDTODslRepository.findHolidaysClassifiedWorkIdByProjectId(project.getId()
                , project.getStartDate().getYear()
                , project.getStartDate().getMonth().getValue()
                , project.getEndDate().getYear()
                , project.getEndDate().getMonth().getValue() + 1
                , "HOLIDAY");

        // 3. classifiedWorkHolidayDTOs -> List<HolidayClassifiedWorkDTO>으로 변환(추석같은 연휴 start ~ end를 각 일자로 변환)
        works.forEach(t -> {
            tmpHolidayList.clear();

            // classifiedWorkHolidayDTOs 에서 공종별로 필터링
            List<HolidayDTO> tmpHolidays = classifiedWorkHolidayDTOs.stream()
                    .filter(s -> s.getWorkId() == t.getId())
                    .collect(Collectors.toList());

            // 공종별 holidayDTOs 들중 휴일정보를 추출
            tmpHolidays.forEach(s -> {
                for (int idx = 0; idx < s.getHolidayCnt(); idx++) {
                    tmpHolidayList.add(Utils.getDateTimeByNationAndFormatType(ChronoUnit.DAYS.addTo(s.getStartDate(), idx), DateFormatEnum.GANTT));
                }
            });

            // 4. 최종 리턴형으로 생성해서 리턴
            holidays.add(new HolidayClassifiedWorkDTO (Long.toString(t.getId()), tmpHolidayList.stream().map(o -> String.valueOf(o)).collect(Collectors.joining("||"))));
        });

        return holidays;
    }

    @Transactional
    public JsonObject postProcessInfo(String uploadFileName) {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");

        try {
            // 2. 신규 process_info 저장하고 id값 리턴
            long newProcessInfoId = saveNewProcessInfoAndGetId(uploadFileName);
            return proc.getResult(true, newProcessInfoId);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject postProcessItems(long newProcessInfoId, List<ProcessItem> processItems) {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            for (ProcessItem processItem : processItems) {
                // 2. 최초 건인 경우 Project task 한개 추가 삽입
                if (!checkRowNumAndSaveProjectAsTask(newProcessInfoId, processItem))
                    return proc.getResult(false, "system.process_service.post_process_file.no_project_info");

                // 3. process Item 저장
                long newProcessItemId = saveProcessItemAndGet(processItem).getId();

                // 4. process item link 저장
                saveProcessItemLink(processItem, newProcessItemId);
            }
            //DB에 전부 저장이 된 후
            for (ProcessItem processItem : processItems){
                // 엑셀에 계산식이 존재하는지 확인
                if (processItem.getFirstCountFormula().length() > 0){
                    // 오류 체크를 위해 계산식에 # 또는 @가 포함되어 있는지 확인
                    if(processItem.getFirstCountFormula().contains("#") || processItem.getFirstCountFormula().contains("@")){
                        try{
                            String operator = "";
                            String variable = "";
                            BigDecimal additionalFormula = new BigDecimal(0);

                            //추가적인 계산식이 있는지 확인
                            if(processItem.getFirstCountFormula().contains("+")){
                                operator = "+";
                                variable = processItem.getFirstCountFormula().split("\\+")[0];
                                additionalFormula = new BigDecimal(processItem.getFirstCountFormula().split("\\+")[1]);
                            }
                            else if(processItem.getFirstCountFormula().contains("-")){
                                operator = "-";
                                variable = processItem.getFirstCountFormula().split("-")[0];
                                additionalFormula = new BigDecimal(processItem.getFirstCountFormula().split("-")[1]);
                            }
                            else if(processItem.getFirstCountFormula().contains("*")){
                                operator = "*";
                                variable = processItem.getFirstCountFormula().split("\\*")[0];
                                additionalFormula = new BigDecimal(processItem.getFirstCountFormula().split("\\*")[1]);
                            }
                            else if(processItem.getFirstCountFormula().contains("/")){
                                operator = "/";
                                variable = processItem.getFirstCountFormula().split("/")[0];
                                additionalFormula = new BigDecimal(processItem.getFirstCountFormula().split("/")[1]);
                            }
                            else if(processItem.getFirstCountFormula().contains("%")){
                                operator = "%";
                                variable = processItem.getFirstCountFormula().split("%")[0];
                                additionalFormula = new BigDecimal(processItem.getFirstCountFormula().split("%")[1]);
                            }
                            else{
                                variable = processItem.getFirstCountFormula();
                            }

                            //기본 계산식 계산
                            FormulaVariableValueDTO formulaVariableValueDTO = processCostService.getFormulaVariableValue(variable, userInfo.getProjectId());

                            //기본 계산식이 성공했을 때 그 값을 DB에 업데이트
                            if(formulaVariableValueDTO.isSuccess()) {
                                //추가적인 계산식 계산
                                switch (operator){
                                    case "+" :
                                        formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().add(additionalFormula));
                                        break;
                                    case "-" :
                                        formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().subtract(additionalFormula));
                                        break;
                                    case "*" :
                                        formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().multiply(additionalFormula));
                                        break;
                                    case "/" :
                                        formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().divide(additionalFormula));
                                        break;
                                    case "%" :
                                        formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().remainder(additionalFormula));
                                        break;
                                    default:
                                        break;
                                }

                                processItem.setFirstCount(formulaVariableValueDTO.getValue());
                                processItemRepository.save(processItem);
                            }
                        } catch(Exception e){

                        }
                    }
                    //테스트용
                    else{
                        System.out.println("Formula Error :" + processItem.getFirstCountFormula());
                    }
                }
            }
            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean checkRowNumAndSaveProjectAsTask(long newProcessInfoId, ProcessItem processItem) {
        if (processItem.getRowNum() == 1) {
            Project project = projectRepository.findById(userInfo.getProjectId()).orElseGet(Project::new);
            if (project.getId() == 0) return false;
            saveProcessItemAndGet(new ProcessItem(newProcessInfoId, project));
        }
        return true;
    }

    @Transactional
    public JsonObject putIsCurrentVersion(long newProcessInfoId, boolean isAddNewFile) {

        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            // 2. 새로 저장된 process_info 버전을 최신으로 교체
            updateIsCurrentVersion(newProcessInfoId);

            // 3. 내역 담당자에게 alert 전송
            if(isAddNewFile) saveAlert("NEW_VERSION", null);

            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateIsCurrentVersion(long newProcessInfoId) {
        ProcessInfo newProcessInfo = processInfoRepository.findById(newProcessInfoId).orElseGet(ProcessInfo::new);
        List<ProcessInfo> oldProcessInfos = processInfoRepository.findByProjectIdAndIsCurrentVersion(userInfo.getProjectId(), true);

        if (newProcessInfo.getId() > 0) newProcessInfo.setIsCurrentVersion(true);
        for (ProcessInfo oldProcessInfo : oldProcessInfos) {
            if (newProcessInfo.isCurrentVersion() && oldProcessInfo.getId() > 0) oldProcessInfo.setIsCurrentVersion(false);
        }
    }

    @Transactional
    public JsonObject updateProcessItems(TaskVO taskVO) {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            for (ProcessItem processItem : taskVO.getProcessItems()) {
                processItemRepository.updateParentId(taskVO.getNewProcessInfoId(), processItem.getRowNum(), processItem.getParentRowNum());
                processItemLinkRepository.updatePredecessorItemIdByPhasingCode(taskVO.getNewProcessInfoId(), processItem.getRowNum());
            }
            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject updateProcessItemParentId(long newProcessInfoId) {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            processItemRepository.updateParentIds(newProcessInfoId);
            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject updateProcessItemLinkPredecessorId(long newProcessInfoId) {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            processItemLinkRepository.updatePredecessorItemIdByPhasingCodes(newProcessInfoId);
            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private ProcessItem saveProcessItemAndGet(ProcessItem processItem) {
        return processItemRepository.save(processItem);
    }

    private ProcessItemLink saveProcessItemLinkAndGetId(ProcessItemLink processItemLink) {
        return processItemLinkRepository.save(processItemLink);
    }

    public JsonObject getUseWorkAll() {
        List<Work> works = workService.findUseWorkAll();

        if (works.size() == 0) return proc.getResult(false, "system.process_service.post_process_file.error_no_use_work");

        List<WorkIdDTO> workIdDTOs = new ArrayList<>();
        for (Work work : works) {
            WorkIdDTO workIdDTO = new WorkIdDTO(work.getId(), work.getWorkName());
            workIdDTOs.add(workIdDTO);
        }
        return proc.getResult(true, workIdDTOs);
    }

    private void saveProcessItemLink(ProcessItem processItem, long newProcessItemId) {
        if (processItem.getProcessItemLinks().size() > 0) {
            int sortNo = 0;
            for (ProcessItemLink processItemLink : processItem.getProcessItemLinks()) {
                sortNo++;
                processItemLink.setProcessItemLinkIdAndSortNo(newProcessItemId, sortNo);
                processItemLinkRepository.save(processItemLink);
            }
        }
    }

    private long saveNewProcessInfoAndGetId(String uploadFileName) {
        return processInfoRepository.save(
                new ProcessInfo(userInfo.getProjectId()
                        , userInfo.getId()
                        , uploadFileName
                        , getProcessTitle()
                        , getDefaultDescription(uploadFileName)
                )
        ).getId();
    }

    private boolean haveRightForAddUpdateDelete() {
        if (userInfo.isRoleAdminProcess()) return true;
        return false;
    }

    private String getProcessTitle() {
        return "[" + Utils.getSaveFileNameDate() + "] " + proc.translate("system.process_service.post_process_file.list_upload_process_file");
    }

    private String getDefaultDescription(String fileName) {
        return proc.translate("system.process_service.post_process_file.this_is_upload_file") + "-" + proc.translate("system.process_service.post_process_file.upload_file_name") + " : " + fileName;
    }

    @Transactional
    public JsonObject putGanttDataAfterAutoSchedule(List<ProcessItem> processItems) {
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");

        try {
            for (ProcessItem processItem : processItems) {
                updateTaskAtPutGanttData(processItem, true);
            }
            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateTaskAtPutGanttData(ProcessItem processItem, boolean isAutoSchedule) {
        ProcessItem savedProcessItem = getProcessItemById(processItem.getId());
        savedProcessItem.setProcessItemDataAtUpdateTaskAtPutGanttData(processItem, isAutoSchedule);
    }

    @Transactional
    public JsonObject postGanttData(TaskPostPutVO taskPostPutVO) {
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            if ("TASK".equalsIgnoreCase(taskPostPutVO.getEntity())) {

                // 1. 현재 사용중인 process_info.id 조회
                long currentProcessInfoId = getCurrentProcessInfoId();

                // 2. 공종 등록시 이미 사용중인 공종이면 오류
                if (taskPostPutVO.getProcessItem().getGanttTaskType() == TaskType.WORK
                        && isUsedWorkIdAlready(currentProcessInfoId, taskPostPutVO.getProcessItem().getWork().getId())) {
                    return proc.getResult(false, "system.process_service.post_gantt_data.error_already_used_work");
                }

                // 3. 부모 process_item 조회
                ProcessItem parentProcessItem = getParentProcessItem(taskPostPutVO, currentProcessInfoId);

                // 4. 신규 gantt_sort_no 조회
                int newGanttSortNo = getNewGanttSortNoForInsert(parentProcessItem);

                // 5. workId와 taskName 셋팅
                setWorkIdAndTaskName(taskPostPutVO, parentProcessItem);

                // 6. 기타 데이터 추가 셋팅
                taskPostPutVO.getProcessItem().setAddDataAtPostGanttDataForTask(currentProcessInfoId
                        , getNewTaskDepth(parentProcessItem)
                        , newGanttSortNo
                        , getNewTaskFullPath(taskPostPutVO, parentProcessItem));

                // 7. gantt_sort_no 전부 한개씩 뒤로 이동
                updateGanttSortNoPlusOne(currentProcessInfoId, newGanttSortNo);

                // 8. process_info 갱신
                updateProcessInfoValidateStatusAndMessage("ADD", currentProcessInfoId);

                // 9. 내역 담당자에게 alert 전송
                saveAlert("PROCESS_ITEM_INSERT", taskPostPutVO.getProcessItem());
                return proc.getResult(true, saveProcessItemAndGet(taskPostPutVO.getProcessItem()));
            }

            if ("LINK".equalsIgnoreCase(taskPostPutVO.getEntity())){
                return proc.getResult(true, saveProcessItemLinkAndGetId(taskPostPutVO.getProcessItemLink()));
            }

            return proc.getResult(false, "system.process_service.post_gantt_data.error_no_entity");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void saveAlert(String actionType, ProcessItem processItem){
        List<Account> estimateUsers = accountDslRepository.findUsersByRoleCode(userInfo.getProjectId(), RoleCode.ROLE_ADMIN_ESTIMATE);

        estimateUsers.forEach(t -> {
            alertService.insertAlertForProcessItem(userInfo.getProjectId(), t.getId(), processItem, actionType);
        });

    }

    private String getNewTaskFullPath(TaskPostPutVO taskPostPutVO, ProcessItem parentProcessItem) {
        if (taskPostPutVO.getProcessItem().getGanttTaskType() == TaskType.WORK) return taskPostPutVO.getProcessItem().getTaskName();
        if (parentProcessItem != null) return parentProcessItem.getTaskFullPath() + " > " + taskPostPutVO.getProcessItem().getTaskName();
        return taskPostPutVO.getProcessItem().getTaskName();
    }

    private int getNewTaskDepth(ProcessItem parentProcessItem) {
        if (parentProcessItem != null) return parentProcessItem.getTaskDepth() + 1;
        else return 0;
    }

    private void setWorkIdAndTaskName(TaskPostPutVO taskPostPutVO, ProcessItem parentProcessItem) {
        if (taskPostPutVO.getProcessItem().getGanttTaskType() == TaskType.WORK)
            taskPostPutVO.getProcessItem().setWorkIdAndTaskNameAtPostProcessItem(taskPostPutVO.getProcessItem().getWork().getId()
                    , workService.findById(taskPostPutVO.getProcessItem().getWork().getId()).getWorkName()
                    , parentProcessItem);
        else
            taskPostPutVO.getProcessItem().setWorkIdAndTaskNameAtPostProcessItem(parentProcessItem.getWork().getId()
                    , taskPostPutVO.getProcessItem().getTaskName()
                    , parentProcessItem);
    }

    private ProcessItem getParentProcessItem(TaskPostPutVO taskPostPutVO, long currentProcessInfoId) {
        if (taskPostPutVO.getProcessItem().getGanttTaskType() == TaskType.WORK) return getProjectProcessItem(currentProcessInfoId);
        if (taskPostPutVO.getProcessItem().getParentId() > 0) return getProcessItemById(taskPostPutVO.getProcessItem().getParentId());
        return getParentProcessItemWhenNoParentId(currentProcessInfoId);
    }

    private void updateGanttSortNoPlusOne(long currentProcessInfoId, int newGanttSortNo) {
        processItemRepository.updateGanttSortNoPlusOne(currentProcessInfoId, newGanttSortNo);
    }

    private boolean isUsedWorkIdAlready(long processInfoId, long workId) {
        return processItemDslRepository.findWorksByProcessInfoIdAndWorkId(processInfoId,workId).size() > 0;
    }

    private long getCurrentProcessInfoId() {
        return processInfoRepository.findByProjectIdAndIsCurrentVersion(userInfo.getProjectId(), true).stream().findFirst().get().getId();
    }

    @Transactional
    public JsonObject putGanttData(TaskPostPutVO taskPutVO){
        if(!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            if ("TASK".equalsIgnoreCase(taskPutVO.getEntity())) updateTaskAtPutGanttDataForUpdate(taskPutVO.getProcessItem(), taskPutVO.getLocalIndex());
            if ("LINK".equalsIgnoreCase(taskPutVO.getEntity())) updateTaskLinkAtPutGanttData(taskPutVO);
            return proc.getResult(true);
        }
        catch ( Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateTaskLinkAtPutGanttData(TaskPostPutVO taskPutVO) {
        ProcessItemLink savedProcessItemLink = processItemLinkRepository.findById(taskPutVO.getProcessItemLink().getId()).orElseGet(ProcessItemLink::new);
        String predecessor = processItemLinkRepository.findByPredecessorId(taskPutVO.getProcessItemLink().getPredecessorId()).orElseGet(ProcessItemLink::new).getPredecessor();
        savedProcessItemLink.setProcessItemLinkAtPutGanttData(taskPutVO.getProcessItemLink(), predecessor);
    }

    private void updateTaskAtPutGanttDataForUpdate(ProcessItem processItem, int localIndex) {

        int newStartSortNo = 0;

        ProcessItem savedProcessItem = getProcessItemById(processItem.getId());
        ProcessItem savedProcessItemParent = getProcessItemById(savedProcessItem.getParentId());
        ProcessItem parentProcessItem = getParentProcessItem(processItem.getParentId());

        // 이동 대상 저장
        List<ProcessItem> targetProcessItems = new ArrayList<>();
        List<ProcessItem> tmpProcessItems = new ArrayList<>();
        targetProcessItems.add(savedProcessItem);
        tmpProcessItems.add(savedProcessItem);

        // System.out.println("0. processItem.ganttSortNo = " + processItem.getGanttSortNo());
        // parentId가 바뀌면 task 가 이동한 경우임. 전체적인 sortNo 변경 필요. ganttSortNo만 바뀌면 현재 부모에서 위치만 바뀜
        // 둘다 동일한 경우 task 의 content 만 바뀜
        if(!isSameParentId(processItem, savedProcessItem)
                || (isSameParentId(processItem, savedProcessItem) && localIndex != getPrevLocalIndex(savedProcessItem, savedProcessItemParent))){

            // 이동 대상을 아래 새끼들까지 모두 tmpProcessItems 에 넣음
            addTargetProcessItemsForChildren(targetProcessItems, tmpProcessItems, savedProcessItem.getProcessInfo().getId());

            // 이동 대상 시작 gantt_sort_no && 이동 대상 마지막 gantt_sort_no && 이동 대상 갯수
            int startGanttSortNo = getMoveStartGanttSortNo(targetProcessItems);
            int endGanttSortNo = getMoveEndGanttSortNo(targetProcessItems);
            int sizeGantSortNo = endGanttSortNo - startGanttSortNo + 1;

            // 신규 sortNo를 계산
            newStartSortNo = getNewStartSortNo(processItem, savedProcessItem, localIndex, parentProcessItem, savedProcessItemParent);

            // 이동 대상을 제외한 task 자리 이동
            moveOtherTaskGanttSortNo(processItem, newStartSortNo, savedProcessItem, endGanttSortNo, sizeGantSortNo);

            // 대상 task들 gantt_sort_no 변경
            for (ProcessItem targetProcessItem : targetProcessItems.stream().sorted(Comparator.comparingInt(ProcessItem::getGanttSortNo)).collect(Collectors.toList())) {

                String newTaskFullPath = parentProcessItem.getTaskDepth() == 0 ? "" : parentProcessItem.getTaskFullPath();

                if(parentProcessItem.getTaskDepth() >  0) newTaskFullPath += " > " + getTaskFullPathWithOutParentDepth(targetProcessItem.getTaskFullPath(), savedProcessItemParent.getTaskDepth());
                else newTaskFullPath = getTaskFullPathWithOutParentDepth(targetProcessItem.getTaskFullPath(), savedProcessItemParent.getTaskDepth());

                int newTaskDepth = parentProcessItem.getTaskDepth() + getNewTaskDepth(targetProcessItem.getTaskFullPath(), savedProcessItemParent.getTaskDepth());

                if(newStartSortNo > targetProcessItem.getGanttSortNo()) {
                    targetProcessItem.setGanttSortNoForUpdateTaskAtPutGanttData(newStartSortNo - sizeGantSortNo, newTaskDepth, newTaskFullPath);
                }
                else {
                    targetProcessItem.setGanttSortNoForUpdateTaskAtPutGanttData(newStartSortNo, newTaskDepth, newTaskFullPath);
                }
                newStartSortNo++;
            }
        }

        setProcessItemTaskFullPaht(processItem, savedProcessItem);
        savedProcessItem.setProcessItemDataAtUpdateTaskAtPutGanttData(processItem, false);
    }

    private void setProcessItemTaskFullPaht(ProcessItem processItem, ProcessItem savedProcessItem) {
        String[] arrayTaskFullPath = savedProcessItem.getTaskFullPath().replace(" ", "").split(">");
        arrayTaskFullPath[arrayTaskFullPath.length - 1] = processItem.getTaskName();
        processItem.setTaskFullPathAtUpdateTaskAtPutGanttData(arrayTaskFullPath);
    }

    private int getNewTaskDepth(String taskFullPath, int parentTaskDepth){
        String[] arrayTaskFullPath = taskFullPath.replace(" ", "").split(">");
        int result = 0;
        for(int idx = 0; idx < arrayTaskFullPath.length; idx ++){
            if(idx >= parentTaskDepth) result++;
        }
        return result;
    }

    private String getTaskFullPathWithOutParentDepth(String taskFullPath, int parentTaskDepth){
        String[] arrayTaskFullPath = taskFullPath.replace(" ", "").split(">");
        String result = "";
        for(int idx = 0; idx < arrayTaskFullPath.length; idx ++){
            if(idx >= parentTaskDepth) result += arrayTaskFullPath[idx];
            if(idx >= parentTaskDepth && idx < arrayTaskFullPath.length - 1) result += " > ";
        }

        return result;
    }

    private int getNewStartSortNo(ProcessItem processItem, ProcessItem savedProcessItem, int localIndex, ProcessItem parentProcessItem, ProcessItem savedProcessItemParent) {

        int newStartSortNo = getNewGanttSortNoForUpdate(parentProcessItem, localIndex);

        if(isSameParentId(processItem, savedProcessItem)
                && localIndex > getPrevLocalIndex(savedProcessItem, savedProcessItemParent)) {
            newStartSortNo++;
        }
        return newStartSortNo;
    }

    private void moveOtherTaskGanttSortNo(ProcessItem processItem, int newStartSortNo, ProcessItem savedProcessItem, int endGanttSortNo, int sizeGantSortNo) {

        // 대상이 빠진 만큼 gantt_sort_no 땡김.
        updateGanttSortNo(savedProcessItem, endGanttSortNo + 1, -1 * sizeGantSortNo);

        if(newStartSortNo > savedProcessItem.getGanttSortNo()){
            updateGanttSortNo(savedProcessItem, newStartSortNo - sizeGantSortNo, sizeGantSortNo); // 아래로 후진
        }
        else {
            updateGanttSortNo(savedProcessItem, newStartSortNo, sizeGantSortNo); // 위로 전진
        }
    }

    private void updateGanttSortNo(ProcessItem savedProcessItem, int newGanttSortNo, int different) {
        processItemRepository.updateGanttSortNo(savedProcessItem.getProcessInfo().getId(), newGanttSortNo, different);
    }

    @NotNull
    private ProcessItem getParentProcessItem(Long parentId) {
        return processItemRepository.findById(parentId).orElseGet(ProcessItem::new);
    }

    @NotNull
    private ProcessItem getParentProcessItemCate(long processId, long taskDepth, String category) {
        ProcessItem processItem = new ProcessItem();
        if (taskDepth == 1) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate(processId).orElse(new ProcessItem());
        } else if (taskDepth == 2) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate1(processId, 1, category).orElse(new ProcessItem());
        } else if (taskDepth == 3) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate2(processId, 2, category).orElse(new ProcessItem());
        } else if (taskDepth == 4) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate3(processId, 3, category).orElse(new ProcessItem());
        } else if (taskDepth == 5) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate4(processId, 4, category).orElse(new ProcessItem());
        } else if (taskDepth == 6) {
            processItem = processItemRepository.findFirstProcessItemByProcessIdAndTaskDepthAndCate5(processId, 5, category).orElse(new ProcessItem());
        }
        return processItem;
    }

    private int getMoveEndGanttSortNo(List<ProcessItem> targetProcessItems) {
        return targetProcessItems.stream()
                .sorted(Comparator.comparingInt(ProcessItem::getGanttSortNo).reversed())
                .findFirst()
                .orElseGet(ProcessItem::new)
                .getGanttSortNo();
    }

    private int getMoveStartGanttSortNo(List<ProcessItem> targetProcessItems) {
        return targetProcessItems.stream()
                .sorted(Comparator.comparingInt(ProcessItem::getGanttSortNo))
                .findFirst()
                .orElseGet(ProcessItem::new)
                .getGanttSortNo();
    }

    private void addTargetProcessItemsForChildren(List<ProcessItem> targetProcessItems, List<ProcessItem> tmpProcessItems, long processInfoId) {
        while (tmpProcessItems.size() > 0) {
            tmpProcessItems = processItemDslRepository.findChildrenByParentIds(processInfoId, tmpProcessItems.stream().map(o -> o.getId()).collect(Collectors.toList()));
            targetProcessItems.addAll(tmpProcessItems);
        }
    }

    private boolean isSameParentId(ProcessItem processItem, ProcessItem savedProcessItem) {
        return savedProcessItem.getParentId().equals(processItem.getParentId());
    }

    private int getPrevLocalIndex(ProcessItem savedProcessItem, ProcessItem savedProcessItemParent) {
        return (int)processItemDslRepository.findChildrenByParentProcessItem(savedProcessItemParent)
                .stream()
                .filter(t -> t.getGanttSortNo() < savedProcessItem.getGanttSortNo())
                .count();
    }

    private int getNewGanttSortNoForUpdate(ProcessItem parentProcessItem, int localIndex){

        if(localIndex == 0) return parentProcessItem.getGanttSortNo() + 1;

        List<ProcessItem> targetProcessItems = new ArrayList<>();
        List<ProcessItem> tmpProcessItems = new ArrayList<>();

        tmpProcessItems.add(parentProcessItem);

        // localIndex가 큰 형제들만 조회
        tmpProcessItems = processItemDslRepository.findChildrenByParentIds(parentProcessItem.getProcessInfo().getId(), tmpProcessItems.stream().map(o -> o.getId())
                .collect(Collectors.toList()))
                .stream()
                .sorted(Comparator.comparingInt(ProcessItem::getGanttSortNo))
                .collect(Collectors.toList())
                .subList(0, localIndex);

        targetProcessItems.addAll(tmpProcessItems);

        // 위 형제들의 새끼들 모두 조회
        addTargetProcessItemsForChildren(targetProcessItems, tmpProcessItems, parentProcessItem.getProcessInfo().getId());

        return parentProcessItem.getGanttSortNo() + targetProcessItems.size() + 1;
    }

    private int getNewGanttSortNoForInsert(ProcessItem parentProcessItem){

        List<ProcessItem> targetProcessItems = new ArrayList<>();
        List<ProcessItem> tmpProcessItems = new ArrayList<>();

        // 부모 저장
        targetProcessItems.add(parentProcessItem);
        tmpProcessItems.add(parentProcessItem);

        // 모든 자식까지 찾아서 리스트화
        addTargetProcessItemsForChildren(targetProcessItems, tmpProcessItems, parentProcessItem.getProcessInfo().getId());

        // 부모의 모든 자식들위치 마지막에 추가
        return getMoveEndGanttSortNo(targetProcessItems) + 1;
    }

    @Transactional
    public JsonObject deleteGanttData(String entity, long id) {
        if(!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            if ("TASK".equalsIgnoreCase(entity)) updateProcessItemTaskStatusAtDeleteGanttData(id);;
            if ("LINK".equalsIgnoreCase(entity)) deleteProcessItemLink(id);
            return proc.getResult(true);
        }
        catch ( Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void deleteProcessItemLink(long id) {
        processItemLinkRepository.deleteById(id);
    }

    private void updateProcessItemTaskStatusAtDeleteGanttData(long id) {
        ProcessItem savedProcessItem = getProcessItemById(id);
        updateGanttSortNo(savedProcessItem, savedProcessItem.getGanttSortNo() + 1, -1);
        savedProcessItem.setTaskStatusDELAtUpdateTaskStatusAtDeleteGanttData();

        // 8. process_info 갱신
        updateProcessInfoValidateStatusAndMessage("DELETE", savedProcessItem.getProcessInfo().getId());

        saveAlert("PROCESS_ITEM_DELETE", savedProcessItem);
    }

    private ProcessItem getProjectProcessItem(long currentProcessInfoId){
        return processItemDslRepository.findProjectProcessItemByProcessId(currentProcessInfoId).stream().findFirst().orElseGet(ProcessItem::new);
    }

    private ProcessItem getProcessItemById(long id){
        return getParentProcessItem(id);
    }

    @NotNull
    private ProcessItem getParentProcessItemWhenNoParentId(long currentProcessInfoId) {
        return processItemDslRepository.findFirstProcessItemByProcessId(currentProcessInfoId).stream().findFirst().orElseGet(ProcessItem::new);
    }

    public List<ProcessDTO> findProcessDTOs(){
        List<ProcessDTO> processDTOs = processDTODslRepository.findProcessDTOsForExcel(userInfo.getProjectId());
        List<ProcessItemLink> processItemLinks = processItemLinkDslRepository.findProcessItemLinksCurrentVersion(userInfo.getProjectId());

        processDTOs.forEach( t -> {
            t.setFsInfo(processItemLinks.stream().filter(s -> s.getProcessItem().getId() == t.getId()).collect(Collectors.toList()));
        });

        return processDTOs;
    }

    public List<ProcessInfoDTO> findProcessInfoDTOs(){
        return processInfoDTODslRepository.findProcessInfoDTOsByProjectId(userInfo.getProjectId());
    }

    @Transactional
    public JsonObject postNewVersion(String description) {
        if(!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");

        try {

            // 1. 현재 사용 중인 process_info 조회
            ProcessInfo currentProcessInfo = getCurrentVersionProcessInfo();

            // 2. currentProcessInfo 를 복사하여 process_info 정보 신규 추가
            ProcessInfo newProcessInfo = saveNewProcessInfoAndGet(currentProcessInfo, description);

            // 3. 일단 newProcessInfo flush
            processInfoRepository.flush();

            // 3. process_item 정보 신규 추가
            processItemRepository.insertSelectForNewVersion(currentProcessInfo.getId(), newProcessInfo.getId());

            // 4. process_item.parent_id 수정
            processItemRepository.updateParentIdsForNewVersion(currentProcessInfo.getId(), newProcessInfo.getId());

            // 5. 기존 is_bookmark false 처리
            processItemRepository.updateFalseIsBookMarkCurrentVersion(currentProcessInfo.getId());

            // 6. process_item_link 정보 신규 추가
            processItemLinkRepository.insertSelectForNewVersion(currentProcessInfo.getId(), newProcessInfo.getId());

            // 7. process_item_cost_detail 정보 추가
            processItemCostDetailRepository.insertSelectForNewVersion(currentProcessInfo.getId(), newProcessInfo.getId(), userInfo.getId());

            // 8. process_item_cost_pay 정보 추가
            processItemCostPayRepository.insertSelectForNewVersion(currentProcessInfo.getId(), newProcessInfo.getId(), userInfo.getId());

            // 9. 최신 버전 변경
            updateIsCurrentVersion(newProcessInfo.getId());

            // 10. 내역 담당자에게 alert 전송
            saveAlert("NEW_VERSION", null);

            return proc.getResult(true);
        }
        catch ( Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @NotNull
    private ProcessInfo saveNewProcessInfoAndGet(ProcessInfo currentProcessInfo, String description) {
        return   processInfoRepository.save(new ProcessInfo(currentProcessInfo.getProjectId()
                , userInfo.getId()
                , currentProcessInfo.getFileName()
                , getNewVersionProcessTitle()
                , StringUtils.isEmpty(description) ? getNewVersionDescription() : description));
    }

    @NotNull
    public ProcessInfo getCurrentVersionProcessInfo() {
        return processInfoRepository.findByProjectIdAndIsCurrentVersion(userInfo.getProjectId(), true)
                .stream()
                .sorted(Comparator.comparingLong(ProcessInfo::getId).reversed())
                .findFirst()
                .orElseGet(ProcessInfo::new);
    }

    private String getNewVersionProcessTitle() {
        return "[" + Utils.getSaveFileNameDate() + "] " + proc.translate("system.process_service.post_new_version.process_info_title");
    }

    private String getNewVersionDescription() {
        return proc.translate("system.process_service.post_new_version.this_is_new_version");
    }

    public JsonObject validateCode(long projectId){
        if(!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");

        try {
            // 0. 현재 process_info 조회 및 상태 벽녕
            ProcessInfo processInfo = getCurrentVersionProcessInfo();
            processInfo.setValidateStatusAndMessage(ProcessValidateStatus.GOING, "0.00");   // 코드검증 진행중, 0.00 으로 초기화
            processInfoRepository.flush();

            // 1. code 값에 해당하는 property 조회
            String codeValidateKey = configService.findConfigForCache("MODEL","WBS_CODE",projectId);

            // 2. process_item 에서 task phasing code 조회
            List<ProcessItem> processItems = getCurrentVersionTaskByProjectId();

            // 3. modeling_attribute 에서 코드에 해당하는 attribute 조회
            List<ModelingAttribute> modelingAttributes = getModelingAttributesByProjectId(codeValidateKey);

            // 4. process_item 코드 검증 실행
            processItems.forEach(t -> {
                t.setProcessItemValidate(modelingAttributes);
            });

            //processItemRepository.flush();

            // 5. process_info 의 코드 검증 결과 저장
            processInfo.setValidateStatusAndMessage(ProcessValidateStatus.COMPLETE, getSuccessRate(processItems));

            return proc.getResult(true);
        }
        catch ( Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateProcessInfoValidateStatusAndMessage(String action, long currentProcessInfoId) {
        ProcessInfo processInfo = processInfoRepository.findById(currentProcessInfoId).orElseGet(ProcessInfo::new);
        if("ADD".equalsIgnoreCase(action)){
            processInfo.setValidateStatusAndMessage(ProcessValidateStatus.READY
                    , getSuccessRate(processInfo.getProcessItems()
                            .stream()
                            .filter(t -> t.getTaskStatus() == TaskStatus.REG && !StringUtils.isEmpty(t.getPhasingCode()))
                            .collect(Collectors.toList())
                    )
            );
        }
        else{
            processInfo.setValidateStatusAndMessage(null
                    , getSuccessRate(processInfo.getProcessItems()
                            .stream()
                            .filter(t -> t.getTaskStatus() == TaskStatus.REG && !StringUtils.isEmpty(t.getPhasingCode()))
                            .collect(Collectors.toList())
                    )
            );
        }

    }

    private List<ProcessItem> getCurrentVersionTaskByProjectId() {
        return processItemDslRepository.findCurrentVersionTaskByProjectId(userInfo.getProjectId());
    }

    private List<ModelingAttribute> getModelingAttributesByProjectId(String codeValidateKey) {
        return modelingAttributeDslRepository.findModelingAttributeByProjectId(userInfo.getProjectId(), codeValidateKey);
    }

    private long getCodeValidationSuccessCount(List<ProcessItem> processItems) {
        return processItems.stream().filter(t -> t.getValidate() == ProcessValidateResult.SUCCESS ).count();
    }

    private String getSuccessRate(List<ProcessItem> processItems) {
        return String.valueOf( Math.round( (float) getCodeValidationSuccessCount(processItems) / processItems.stream().filter(t -> t.getGanttTaskType() == TaskType.TASK).count()  * 100 * 100 ) / 100.0 ) ;
    }

    public JsonObject getCodeValidateStatusCurrentProcessInfo(){
        ProcessInfo processInfo = getCurrentVersionProcessInfo();
        if(processInfo.getValidateStatus() == ProcessValidateStatus.GOING) return proc.getMessageResult(true, processInfo.getValidateMessage());
        return proc.getMessageResult(false, processInfo.getValidateMessage());
    }

    public PageDTO<ProcessItem> findCurrentVersionTaskPageByProjectId(SearchCodeValidationVO searchCodeValidationVO, Pageable pageable){
        return processItemDslRepository.findCurrentVersionTaskPageByProjectId(searchCodeValidationVO, pageable);
    }

    public List<ProcessItem> findCurrentVersionTaskListByProjectId(SearchCodeValidationVO searchCodeValidationVO){
        return processItemDslRepository.findCurrentVersionTaskListByProjectId(searchCodeValidationVO);
    }

    public MainProgressDTO getMainProcessRate(){
        return mainProgressDTODslRepository.getMainProcessRate(userInfo.getProjectId());
    }

    public List<SimulationDTO> findTaskExchangeIds() {
        return simulationDslRepository.findTaskExchangeIds(userInfo.getProjectId());
    }

    public List<SimulationDTO> findTaskExchangeIdsUtilToday() {
        return simulationDslRepository.findTaskExchangeIdsUtilToday(userInfo.getProjectId());
    }

    public JsonObject findTaskExchangeIdsByPhasingCode(String phasingCode) {
        return proc.getResult(true, simulationDslRepository.findTaskExchangeIdsByPhasingCode(userInfo.getProjectId(), phasingCode));
    }
    public List<SimulationDTO> findTaskListByPhasingCodes(String[] phasingCodes) {
    	return simulationDslRepository.findTaskExchangeIdsByPhasingCodes(userInfo.getProjectId(), phasingCodes);
    }

    public List<SelectProgressConfig> getProcessItemWorkerDeviceDetail(Long processItemId) {
        return selectProgressConfigRepository.getProcessItemWorkerDeviceDetail(processItemId);
    }

    public List<Object[]> findProcessItemByWorkIdByTaskDepth(Long processId, Long workId, int taskDepth, String reportDate) {
        return processItemRepository.findProcessItemByWorkIdByTaskDepth(processId, workId, taskDepth, reportDate.substring(0, 4) + "-01-01", reportDate);
    }

    public JsonObject findProcessCategory(SearchProcessItemCategoryVO searchProcessItemCategoryVO) {
        return proc.getResult(true, processItemCategoryDslRepository.selectCateList(searchProcessItemCategoryVO));
    }

    public String getCateCode() {
        String strCode = "";
        String strZero = "0";
        int intCLen = 1;
        int intLen = 5;

        // 문자열 반복하여 생성
        strZero = Utils.strZeroRepeat(strZero, intLen);
        System.out.println(strZero + "------------------------------------");
        String strMaxCode = processItemCategoryRepository.selectMaxCateCode(strZero, intCLen, intLen);
        System.out.println(strMaxCode + "------------------------------------");
        if (strMaxCode == null) {
            strCode = "00001";
            //strCode = String.valueOf(Integer.parseInt(strMaxCode) + 1);
        } else {
            strCode = strMaxCode;
        }
        System.out.println(strCode + "------------------------------------");
        return strCode;
    }

    private ProcessItem getNewParentProcessItem(ProcessItem processItem, long currentProcessInfoId) {
        if (processItem.getGanttTaskType() == TaskType.WORK) return getProjectProcessItem(currentProcessInfoId);
        if (processItem.getTaskDepth() > 1) return getProcessItemById(processItem.getParentId());
        return getParentProcessItemWhenNoParentId(currentProcessInfoId);
    }

    @Transactional
    public JsonObject postProcessItem(ProcessItemVO processItemVO) {
        try {
            // 1. 현재 사용중인 process_info.id 조회
            long currentProcessInfoId = getCurrentProcessInfoId();

            // 2. project 시작일과 마지막 일자 조회용
            Project project = projectRepository.findById(userInfo.getProjectId()).orElseGet(Project::new);

            String upCode = "00000";
            String newCode = getCateCode();
            String taskFullPath = processItemVO.getTaskName();
            String parentTaskFullPath = "";
            ProcessItem parentProcessItem = new ProcessItem();
            if (processItemVO.getTaskDepth() == 1) {
                upCode = "00000";

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate1(newCode);
            } else if (processItemVO.getTaskDepth() == 2) {
                upCode = processItemVO.getCate1();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate2(newCode);
            } else if (processItemVO.getTaskDepth() == 3) {
                upCode = processItemVO.getCate2();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate3(newCode);
            } else if (processItemVO.getTaskDepth() == 4) {
                upCode = processItemVO.getCate3();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate4(newCode);
            } else if (processItemVO.getTaskDepth() == 5) {
                upCode = processItemVO.getCate4();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate5(newCode);
            } else if (processItemVO.getTaskDepth() == 6) {
                upCode = processItemVO.getCate5();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, processItemVO.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                    processItemVO.setParentId(parentProcessItem.getId());
                }

                processItemVO.setCate6(newCode);
            }
            if (processItemVO.getPhasingCode() != null && !processItemVO.getPhasingCode().equals("")) {
                processItemVO.setGanttTaskType(TaskType.TASK);
            } else {
                processItemVO.setGanttTaskType(TaskType.PROJECT);
            }
            processItemVO.setTaskFullPath(taskFullPath);
            //processItemVO.setStartDate(processItemVO.getPlannedStartDate());
            //processItemVO.setEndDate(processItemVO.getPlannedEndDate());
            if (processItemVO.getFirstCountFormula().length() > 0){
                // 오류 체크를 위해 계산식에 # 또는 @가 포함되어 있는지 확인
                if(processItemVO.getFirstCountFormula().contains("#") || processItemVO.getFirstCountFormula().contains("@")){
                    try{
                        String operator = "";
                        String variable = "";
                        BigDecimal additionalFormula = new BigDecimal(0);

                        //추가적인 계산식이 있는지 확인
                        if(processItemVO.getFirstCountFormula().contains("+")){
                            operator = "+";
                            variable = processItemVO.getFirstCountFormula().split("\\+")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("\\+")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("-")){
                            operator = "-";
                            variable = processItemVO.getFirstCountFormula().split("-")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("-")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("*")){
                            operator = "*";
                            variable = processItemVO.getFirstCountFormula().split("\\*")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("\\*")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("/")){
                            operator = "/";
                            variable = processItemVO.getFirstCountFormula().split("/")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("/")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("%")){
                            operator = "%";
                            variable = processItemVO.getFirstCountFormula().split("%")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("%")[1]);
                        }
                        else{
                            variable = processItemVO.getFirstCountFormula();
                        }

                        //기본 계산식 계산
                        FormulaVariableValueDTO formulaVariableValueDTO = processCostService.getFormulaVariableValue(variable, userInfo.getProjectId());

                        //기본 계산식이 성공했을 때 그 값을 DB에 업데이트
                        if(formulaVariableValueDTO.isSuccess()) {
                            //추가적인 계산식 계산
                            switch (operator){
                                case "+" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().add(additionalFormula));
                                    break;
                                case "-" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().subtract(additionalFormula));
                                    break;
                                case "*" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().multiply(additionalFormula));
                                    break;
                                case "/" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().divide(additionalFormula));
                                    break;
                                case "%" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().remainder(additionalFormula));
                                    break;
                                default:
                                    break;
                            }

                            processItemVO.setFirstCount(formulaVariableValueDTO.getValue());
                        }
                    } catch(Exception e){

                    }
                }
                else{
                    System.out.println("Formula Error :" + processItemVO.getFirstCountFormula());
                }
            }

            long display = processItemCategoryRepository.getDisplay(upCode);
            ProcessItemCategory processItemCategory = new ProcessItemCategory();
            processItemCategory.setProcessItemCategory(newCode, processItemVO.getTaskName(), upCode, display, "KO");
            processItemCategoryRepository.save(processItemCategory);


            ProcessItem saveProcessItem = new ProcessItem(currentProcessInfoId, project);

            saveProcessItem.setProcessItemDataAtUpdateTaskAtPutData(processItemVO);

            // 3. process Item 저장
            saveProcessItemAndGet(saveProcessItem);

            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putProcessItem(ProcessItemVO processItemVO) {
        try {
            // 1. 현재 사용중인 process_info.id 조회
            long currentProcessInfoId = getCurrentProcessInfoId();

            // 2. project 시작일과 마지막 일자 조회용
            Project project = projectRepository.findById(userInfo.getProjectId()).orElseGet(Project::new);

            String upCode = "00000";
            String taskFullPath = processItemVO.getTaskName();
            ProcessItem parentProcessItem = new ProcessItem();
            ProcessItem saveProcessItem = getProcessItemById(processItemVO.getId());
            if (saveProcessItem.getTaskDepth() == 1) {

            } else if (saveProcessItem.getTaskDepth() == 2) {
                upCode = saveProcessItem.getCate1();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, saveProcessItem.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                }
            } else if (processItemVO.getTaskDepth() == 3) {
                upCode = saveProcessItem.getCate2();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, saveProcessItem.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                }
            } else if (processItemVO.getTaskDepth() == 4) {
                upCode = saveProcessItem.getCate3();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, saveProcessItem.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                }
            } else if (processItemVO.getTaskDepth() == 5) {
                upCode = saveProcessItem.getCate4();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, saveProcessItem.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                }
            } else if (processItemVO.getTaskDepth() == 6) {
                upCode = saveProcessItem.getCate5();

                parentProcessItem = getParentProcessItemCate(currentProcessInfoId, saveProcessItem.getTaskDepth(), upCode);
                if (parentProcessItem != null) {
                    if (!parentProcessItem.getTaskFullPath().equals("")) {
                        taskFullPath = parentProcessItem.getTaskFullPath() + " > " + processItemVO.getTaskName();
                    }
                }
            }
            processItemVO.setTaskFullPath(taskFullPath);
            //processItemVO.setStartDate(processItemVO.getPlannedStartDate());
            //processItemVO.setEndDate(processItemVO.getPlannedEndDate());
            if (processItemVO.getFirstCountFormula().length() > 0){
                // 오류 체크를 위해 계산식에 # 또는 @가 포함되어 있는지 확인
                if(processItemVO.getFirstCountFormula().contains("#") || processItemVO.getFirstCountFormula().contains("@")){
                    try{
                        String operator = "";
                        String variable = "";
                        BigDecimal additionalFormula = new BigDecimal(0);

                        //추가적인 계산식이 있는지 확인
                        if(processItemVO.getFirstCountFormula().contains("+")){
                            operator = "+";
                            variable = processItemVO.getFirstCountFormula().split("\\+")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("\\+")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("-")){
                            operator = "-";
                            variable = processItemVO.getFirstCountFormula().split("-")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("-")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("*")){
                            operator = "*";
                            variable = processItemVO.getFirstCountFormula().split("\\*")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("\\*")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("/")){
                            operator = "/";
                            variable = processItemVO.getFirstCountFormula().split("/")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("/")[1]);
                        }
                        else if(processItemVO.getFirstCountFormula().contains("%")){
                            operator = "%";
                            variable = processItemVO.getFirstCountFormula().split("%")[0];
                            additionalFormula = new BigDecimal(processItemVO.getFirstCountFormula().split("%")[1]);
                        }
                        else{
                            variable = processItemVO.getFirstCountFormula();
                        }

                        //기본 계산식 계산
                        FormulaVariableValueDTO formulaVariableValueDTO = processCostService.getFormulaVariableValue(variable, userInfo.getProjectId());

                        //기본 계산식이 성공했을 때 그 값을 DB에 업데이트
                        if(formulaVariableValueDTO.isSuccess()) {
                            //추가적인 계산식 계산
                            switch (operator){
                                case "+" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().add(additionalFormula));
                                    break;
                                case "-" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().subtract(additionalFormula));
                                    break;
                                case "*" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().multiply(additionalFormula));
                                    break;
                                case "/" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().divide(additionalFormula));
                                    break;
                                case "%" :
                                    formulaVariableValueDTO.setValue(formulaVariableValueDTO.getValue().remainder(additionalFormula));
                                    break;
                                default:
                                    break;
                            }

                            processItemVO.setFirstCount(formulaVariableValueDTO.getValue());
                        }
                    } catch(Exception e){

                    }
                }
                else{
                    System.out.println("Formula Error :" + processItemVO.getFirstCountFormula());
                }
            }

            processItemCategoryRepository.updateCategoryName(processItemVO.getCode(), processItemVO.getTaskName());

            saveProcessItem.setProcessItemDataAtUpdateAtPutData(processItemVO);

            // 3. process Item 저장
            saveProcessItemAndGet(saveProcessItem);

            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateProcessItemTaskStatusAtDeleteCategory(long cateNo, String code, long id) {
        ProcessItemCategory processItemCategory = processItemCategoryRepository.findByCode(code).orElseGet(ProcessItemCategory::new);
        processItemCategory.setProcessItemCategoryUpdateEnabeld(false);
        processItemCategoryRepository.save(processItemCategory);

        ProcessItem savedProcessItem = getProcessItemById(id);
        savedProcessItem.setTaskStatusDELAtUpdateTaskStatusAtDeleteGanttData();
        saveProcessItemAndGet(savedProcessItem);

        // 8. process_info 갱신
        updateProcessInfoValidateStatusAndMessage("DELETE", savedProcessItem.getProcessInfo().getId());

        saveAlert("PROCESS_ITEM_DELETE", savedProcessItem);
    }

    @Transactional
    public JsonObject deleteProcessItem(long cateNo, String code, long id) {
        if(!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");
        try {
            updateProcessItemTaskStatusAtDeleteCategory(cateNo, code, id);
            return proc.getResult(true);
        }
        catch ( Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putProcessItemDisplay(ProcessItemVO processItemVO) {
        try {
            for (int i = 0; i < processItemVO.getOlist().size(); i++) {
                processItemCategoryRepository.updateCategoryDisplay(processItemVO.getOlist().get(i), (i+1));
            }

            return proc.getResult(true);
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public long getProcessItemProgressRateAvg(long processId) {
        return processItemRepository.getProcessItemProgressRateAvg(processId).orElse(0L);
    }

    public long getProcessItemPaidCostSum(long processId) {
        return processItemRepository.getProcessItemPaidCostSum(processId).orElse(0L);
    }

    public List<ProcessItem> getProcessItemDateListLimit(long processId, String date) {
        return processItemRepository.getProcessItemDateListLimit(processId, date, 5);
    }

    public List<ProcessItem> getProcessItemDateList(long processId, String date) {
        return processItemRepository.getProcessItemDateList(processId, date);
    }

    public JsonObject postProcessItemExcelFile() {
        // 1. 권한 체크
        if (!haveRightForAddUpdateDelete()) return proc.getResult(false, "system.process_service.post_process_file.no_have_right_add");

        try {
            // 2. 신규 process_info 저장하고 id값 리턴
            long newProcessInfoId = saveNewProcessInfoAndGetId("");

            ProcessItemExcelFile processItemExcelFile = new ProcessItemExcelFile();
            processItemExcelFile.setProcessItemExcelFileAtAddProcessItemExcelFile(userInfo, newProcessInfoId);
            processItemExcelFileRepository.save(processItemExcelFile);

            //return proc.getResult(true, newProcessInfoId);
            return proc.getResult(true, processItemExcelFile.getId(), "system.process_service.make_process_item_excel_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public String generateCalendarHtml(long processId, int year, int month) {
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

        // 1일 이전의 빈 날짜 처리
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int lastDayOfPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOffset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7;

        int prevYear = year;
        int prevMonth = month - 1;
        if (month == 1) {
            prevYear--;
            prevMonth = 12;
        }

        html.append("<tr>");
        for (int i = lastDayOfPrevMonth - dayOffset + 1; i <= lastDayOfPrevMonth; i++) {
            date = prevYear + "-" + String.format("%02d", prevMonth) + "-" + String.format("%02d", i);
            List<ProcessItem> processItems = processItemRepository.getProcessItemDateListLimit(processId, date, 3);
            html.append("<td><div class=\"head\">");
            html.append("<strong class=\"txt-color-gray\">").append(i).append("</strong>");
            html.append("</div>");
            html.append("<ul>");
            for (ProcessItem processItem : processItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(processItem.getTaskName()).append("</button></li>");
            }
            html.append("</ul>");
            html.append("</td>");
        }

        // 날짜 삽입
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDay; day++) {
            date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
            List<ProcessItem> processItems = processItemRepository.getProcessItemDateListLimit(processId, year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day), 3);
            html.append("<td><div class=\"head\">");
            if ((firstDayOfWeek + day - 1) % 7 == 1) {
                html.append("<strong class=\"txt-color-red\">").append(day).append("</strong>");
            } else {
                html.append("<strong>").append(day).append("</strong>");
            }
            html.append("</div>");
            html.append("<ul>");
            for (ProcessItem processItem : processItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(processItem.getTaskName()).append("</button></li>");
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
            List<ProcessItem> processItems = processItemRepository.getProcessItemDateListLimit(processId, nextYear + "-" + String.format("%02d", nextMonth) + "-" + String.format("%02d", nextMonthDay), 3);
            html.append("<td><div class=\"head\">");
            html.append("<strong class=\"txt-color-gray\">").append(nextMonthDay).append("</strong>");
            html.append("</div>");
            html.append("<ul>");
            for (ProcessItem processItem : processItems) {
                html.append("<li><button type=\"button\" class=\"pop-open-btn btnDayProcessItem\" data-modal=\"#calendarPopup\" data-date='"+date+"'>").append(processItem.getTaskName()).append("</button></li>");
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
