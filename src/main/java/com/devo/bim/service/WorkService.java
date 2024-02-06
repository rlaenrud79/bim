package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.entity.WorkName;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.WorkVO;
import com.devo.bim.repository.dsl.WorkDTODslRepository;
import com.devo.bim.repository.dsl.WorkDslRepository;
import com.devo.bim.repository.dsl.WorkNameDslRepository;
import com.devo.bim.repository.spring.WorkNameRepository;
import com.devo.bim.repository.spring.WorkRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService extends AbstractService {

    private final WorkRepository workRepository;
    private final WorkDTODslRepository workDTODslRepository;
    private final WorkDslRepository workDslRepository;
    private final WorkNameRepository workNameRepository;
    private final WorkNameDslRepository workNameDslRepository;

    public List<Work> findAll() {
        return workRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"))
                .stream()
                .filter(t -> t.getProjectId() == userInfo.getProjectId())
                .collect(Collectors.toList());
    }

    public List<Work> findAllUp() {
        return workRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"))
                .stream()
                .filter(t -> t.getProjectId() == userInfo.getProjectId() && (t.getUpId() == 0))
                .collect(Collectors.toList());
    }

    public List<Work> findUseWorkAll(){
        return workRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"))
                .stream()
                .filter(t->t.getStatus() == WorkStatus.USE && t.getProjectId() == userInfo.getProjectId())
                .collect(Collectors.toList());
    }

    public List<Work> findUseWorkAllUp(){
        return workRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"))
                .stream()
                .filter(t->t.getStatus() == WorkStatus.USE && t.getProjectId() == userInfo.getProjectId() && (t.getUpId() == 0))
                .collect(Collectors.toList());
    }

    public List<WorkDTO> findWorkDTOs(SearchWorkVO searchWorkVO) {
        return workDTODslRepository.findWorkDTOs(searchWorkVO);
    }

    public List<WorkDTO> findWorkUpDTOs(SearchWorkVO searchWorkVO) {
        return workDTODslRepository.getQueryWorkDTOListQuery(searchWorkVO);
    }

    public Work findById(long workId) {
        return workRepository.findByIdAndProjectId(workId, userInfo.getProjectId()).orElseGet(Work::new);
    }

    public Work findBySortNo(int sortNo) {
        return workRepository.findByProjectIdAndSortNo(userInfo.getProjectId(), sortNo).orElseGet(Work::new);
    }

    @Transactional
    public JsonObject postWorkAndWorkName(WorkVO workVO) {
        try {
            //int sortNo = workVO.getSortNo();
            int sortNo = workRepository.getMaxWorkSortNo().orElse(0) + 1;
            int upId = workVO.getUpId();
            String enWorkName = workVO.getWorkName("EN");

            // 신규 Work의 sortNo 값이 기존 목록의 sortNo 사이로 추가되는 경우 기존 목록의 sortNo 하나씩 증가
            workRepository.addOneGOESortNo(sortNo, userInfo.getProjectId());

            long workId = addWork(sortNo, enWorkName, userInfo, upId);
            addWorkNames(workVO.getWorkNames(), workId);

            // schedule중 법정 공휴일 신규 공종에 연결
            workRepository.addNewWorkSchedule(workId);

            return proc.getResult(true, "system.admin_service.post_work");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void addWorkNames(List<WorkName> workNames, long workId) {
        for (WorkName workName : workNames) {
            workName.setWorkId(workId);
            workNameRepository.save(workName);
        }
    }

    private long addWork(int sortNo, String enWorkName, UserInfo userInfo, int upId) {
        Work work = new Work();
        work.setWorkAtAddWork(userInfo.getProjectId(), sortNo, enWorkName, userInfo.getId(), upId);
        return workRepository.save(work).getId();
    }

    @Transactional
    public JsonObject putWorkAndWorkName(WorkVO workVO) {

        long workId = workVO.getWorkId();
        int newSortNo = workVO.getSortNo();
        int upId = workVO.getUpId();
        String enWorkName = workVO.getWorkName("EN");

        long projectId = userInfo.getProjectId();

        // Work 조회
        Work savedWork = workRepository.findByIdAndProjectId(workId, projectId).orElseGet(Work::new);
        if (savedWork.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_work");

        try {
            // 새로 입력한 sortNo 값이 기존 저장된 sortNo 보다 큰 경우
            if (savedWork.getSortNo() < newSortNo)
                workRepository.minusOneGTSavedSOrtNoAndLOESortNo(projectId, savedWork.getSortNo(), newSortNo);

            // 새로 입력한 sortNo 값이 기존 저장된 sortNo 보다 작은 경우
            if (savedWork.getSortNo() > newSortNo)
                workRepository.addOneLTSavedSortNoAndGOESortNo(projectId, savedWork.getSortNo(), newSortNo);

            updateWork(newSortNo, enWorkName, userInfo, savedWork, upId);
            updateWorkName(workVO.getWorkNames(), savedWork);

            return proc.getResult(true, "system.admin_service.put_work");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void updateWorkName(List<WorkName> workNames, Work savedWork) {
        for (WorkName workName : workNames) {
            WorkName savedWorkName = workNameDslRepository.findWorkNameByWorkIdAndLanguageCode(savedWork.getId(), workName.getLanguageCode());
            if (savedWorkName == null) savedWorkName = new WorkName();
            savedWorkName.setNameAtUpdateWork(workName.getLanguageCode(), workName.getName());
        }
    }

    private void updateWork(int newSortNo, String enWorkName, UserInfo userInfo, Work savedWork, int upId) {
        savedWork.setWorkAtUpdateWork(newSortNo, enWorkName, userInfo.getId(), upId);
    }

    @Transactional
    public JsonObject putWorkStatus(long workId, WorkStatus status) {
        Work savedWork = workRepository.findById(workId).orElseGet(Work::new);
        if (savedWork.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_work");

        try {
            savedWork.setWorkStatusAtUpdateStatus(status);
            String message = (status.equals(WorkStatus.USE))
                    ? "system.admin_service.put_work_status_use"
                    : "system.admin_service.put_work_status_del";
            return proc.getResult(true, message);

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putWorkSortNoDESC(long workId) {
        long projectId = userInfo.getProjectId();

        // Work 조회
        Work savedWork = workRepository.findByIdAndProjectId(workId, projectId).orElseGet(Work::new);
        if (savedWork.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_work");

        // 조회한 Work 의 다음 SortNo 에 해당하는 Work 조회
        Work savedWorkNext = workDslRepository.findNextWorkBySortNo(projectId, savedWork.getSortNo());

        try {
            // swap sortNo
            int tmpSortNo = savedWork.getSortNo();
            savedWork.setSortNoAtUpdateSortNo(savedWorkNext.getSortNo());
            savedWorkNext.setSortNoAtUpdateSortNo(tmpSortNo);
            return proc.getResult(true, "system.admin_service.put_work_sort_no_asc");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putWorkSortNoASC(long workId) {
        long projectId = userInfo.getProjectId();

        // Work 조회
        Work savedWork = workRepository.findByIdAndProjectId(workId, projectId).orElseGet(Work::new);
        if (savedWork.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_work");

        // 조회한 Work 의 이전 SortNo 에 해당하는 Work 조회
        Work savedWorkPrevious = workDslRepository.findPreviousWorkBySortNo(projectId, savedWork.getSortNo());

        try {
            // swap sortNo
            int tmpSortNo = savedWork.getSortNo();
            savedWork.setSortNoAtUpdateSortNo(savedWorkPrevious.getSortNo());
            savedWorkPrevious.setSortNoAtUpdateSortNo(tmpSortNo);
            return proc.getResult(true, "system.admin_service.put_work_sort_no_desc");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<WorkName> findByLanguageCode() {
        return workNameDslRepository.findByProjectIdAndLanguageCode(userInfo.getProjectId(), getLanguage());
    }

    public JsonObject getWorkAllSecond(long upId) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setSortProp("SORT_NO");
        searchWorkVO.setUpId(upId);

        return proc.getResult(
                true
                , workDTODslRepository.getQueryWorkDTOListQuery(searchWorkVO)
        );
    }
}