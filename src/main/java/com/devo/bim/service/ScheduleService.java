package com.devo.bim.service;

import com.devo.bim.model.dto.ScheduleDTO;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Company;
import com.devo.bim.model.entity.Schedule;
import com.devo.bim.model.entity.ScheduleIndex;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.ScheduleVO;
import com.devo.bim.repository.dsl.ScheduleDTODslRepository;
import com.devo.bim.repository.spring.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService extends AbstractService {

    private final ScheduleDTODslRepository scheduleDTODslRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleIndexRepository scheduleIndexRepository;
    private final AccountRepository accountRepository;
    private final WorkRepository workRepository;
    private final WorkService workService;
    private final CompanyRepository companyRepository;

    public JsonObject getAdminEventByYearMonth(String yearMonth){

        if(!userInfo.isRoleAdminProject()) return proc.getResult(false, "system.schedule_service.get_schedule.no_have_right");

        yearMonth = initYearMonth(yearMonth);
        int searchYear = getSearchYear(yearMonth);
        int searchMonth = getSearchMonth(yearMonth);

        List<ScheduleDTO> scheduleDTOs = scheduleDTODslRepository.findByYearMonthForAdmin(userInfo.getProjectId(), searchYear, searchMonth);

        return proc.getResult(true, scheduleDTOs);
    }

    public JsonObject getUserEventByYearMonth(String yearMonth){
        yearMonth = initYearMonth(yearMonth);
        int searchYear = getSearchYear(yearMonth);
        int searchMonth = getSearchMonth(yearMonth);

        List<ScheduleDTO> scheduleDTOs = scheduleDTODslRepository.findByYearMonthForUser(userInfo.getProjectId(), searchYear, searchMonth, getUserWorkIds(), userInfo.getId());

        return proc.getResult(true, scheduleDTOs);
    }

    private int getSearchMonth(String yearMonth) {
        return Integer.parseInt(yearMonth.split("-")[1]) + 1;
    }

    private int getSearchYear(String yearMonth) {
        return Integer.parseInt(yearMonth.split("-")[0]);
    }

    @NotNull
    private String initYearMonth(String yearMonth) {
        if (StringUtils.isEmpty(yearMonth)) yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
        return yearMonth;
    }

    @NotNull
    private List<Long> getUserWorkIds() {
        // 관리자인 경우 모든 사용 공종 리턴
        if(userInfo.isRoleAdminProject()) {
            return workRepository.findAll()
                    .stream()
                    .filter(t -> t.getProjectId() == userInfo.getProjectId() && t.getStatus() == WorkStatus.USE)
                    .collect(Collectors.toList())
                    .stream()
                    .map(o -> o.getId())
                    .collect(Collectors.toList());
        }
        
        // 사용자 공종 리턴
        return accountRepository.findById(userInfo.getId())
                .orElseGet(Account::new)
                .getWorks()
                .stream().filter(t -> t.getStatus() == WorkStatus.USE)
                .collect(Collectors.toList())
                .stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());
    }

    public Schedule findById(long mScheduleId){
        Schedule schedule = scheduleRepository.findById(mScheduleId).orElseGet(Schedule::new);
        if(schedule.getId() == 0) proc.translate("system.schedule_service.not_found_schedule");
        return schedule;
    }

    public JsonObject postScheduleAtAdmin(ScheduleVO scheduleVO) {

        // 1. 등록 권한 체크
        if(!haveRightForAdminAddUpdateDelete())
            return proc.getResult(false, "system.schedule_service.post_schedule.no_have_right_add");

        try{
            // 2. 일정 저장
            Schedule newSchedule = saveSchedule(scheduleVO, workService.findUseWorkAll().size());

            // 3. 일정 타입이 공휴일(HOLIDAY)이면 공정 다시 계산
            reCalculateSchedule("ADD", newSchedule);

            return proc.getResult(true, "system.schedule_service.post_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject putScheduleAtAdmin(ScheduleVO scheduleVO) {

        // 1. Schedule 조회
        Schedule savedSchedule = scheduleRepository.findById(scheduleVO.getScheduleId()).orElseGet(Schedule::new);
        if(savedSchedule.getId() == 0)
            return proc.getResult(false, "system.schedule_service.error_no_exist_schedule");

        // 2. 권한 체크
        if(!haveRightForAdminAddUpdateDelete())
            return proc.getResult(false, "system.schedule_service.put_schedule.no_have_right_update");

        try{
            // 3. 일정 업데이트
            updateSchedule(scheduleVO, savedSchedule);

            // 4. 일정 타입이 공휴일(HOLIDAY)이면 공정 다시 계산
            reCalculateSchedule("UPDATE", savedSchedule);

            return proc.getResult(true, "system.schedule_service.put_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject deleteScheduleAtAdmin(long secheduleId){
        // 1. Schedule 조회
        Schedule savedSchedule = scheduleRepository.findById(secheduleId).orElseGet(Schedule::new);
        if(savedSchedule.getId() == 0)
            return proc.getResult(false, "system.schedule_service.error_no_exist_schedule");

        // 2. 권한 체크
        if(!haveRightForAdminAddUpdateDelete())
            return proc.getResult(false, "system.schedule_service.delete_schedule.no_have_right_delete");

        try {
            // 3. 일정 삭제
            deleteSchedule(savedSchedule);

            // 4. 일정 타입이 공휴일(HOLIDAY)이면 공정 다시 계산
            reCalculateSchedule("DELETE", savedSchedule);

            return proc.getResult(true, "system.schedule_service.delete_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject postScheduleAtUser(ScheduleVO scheduleVO) {
        // 0. 공종 체크
        Company company = companyRepository.findById(userInfo.getCompanyId()).orElseGet(Company::new);
        if(company.getId() == 0) return proc.getResult(false, "system.schedule_service.no_have_company_info");
        if(company.getWorks().size() == 0) return proc.getResult(false, "system.schedule_service.no_have_work_info");

        try{
            // 1. 일정 저장
            Schedule newSchedule = saveSchedule(scheduleVO, workService.findUseWorkAll().size());

            return proc.getResult(true, "system.schedule_service.post_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject putScheduleAtUser(ScheduleVO scheduleVO) {
        Company company = companyRepository.findById(userInfo.getCompanyId()).orElseGet(Company::new);
        if(company.getId() == 0) return proc.getResult(false, "system.schedule_service.no_have_company_info");
        if(company.getWorks().size() == 0) return proc.getResult(false, "system.schedule_service.no_have_work_info");

        // 1. Schedule 조회
        Schedule savedSchedule = scheduleRepository.findById(scheduleVO.getScheduleId()).orElseGet(Schedule::new);
        if(savedSchedule.getId() == 0) return proc.getResult(false, "system.schedule_service.error_no_exist_schedule");

        // 2. 권한 체크
        if(!haveRightForUserUpdateDelete(savedSchedule)) return proc.getResult(false, "system.schedule_service.put_schedule.no_have_right_update");

        try{
            // 3. 일정 업데이트
            updateSchedule(scheduleVO, savedSchedule);

            return proc.getResult(true, "system.schedule_service.put_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject deleteScheduleAtUser(long secheduleId){
        // 1. Schedule 조회
        Schedule savedSchedule = scheduleRepository.findById(secheduleId).orElseGet(Schedule::new);
        if(savedSchedule.getId() == 0)
            return proc.getResult(false, "system.schedule_service.error_no_exist_schedule");

        // 2. 권한 체크
        if(!haveRightForUserUpdateDelete(savedSchedule))
            return proc.getResult(false, "system.schedule_service.delete_schedule.no_have_right_delete");

        try {
            // 3. 일정 삭제
            deleteSchedule(savedSchedule);

            return proc.getResult(true, "system.schedule_service.delete_schedule");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void saveScheduleIndex(Schedule newSchedule, int monthCnt) {
        for(int idx = 0; idx <= monthCnt; idx++){
            LocalDate tmpDate = newSchedule.getStartDate().toLocalDate().plusMonths(idx);
            scheduleIndexRepository.save(new ScheduleIndex(tmpDate.getYear(), tmpDate.getMonth().getValue(), newSchedule.getId()));
        }
    }

    private Schedule saveSchedule(ScheduleVO scheduleVO, int allUseWorkCnt) {
        // 1 schedule & schedule_work 등록
        Schedule newSchedule = scheduleRepository.save(new Schedule(scheduleVO, allUseWorkCnt));

        // 2. 기간 시작일과 종료일로 schedule_index 갯수 구하기
        int monthCnt = getMonthCnt(newSchedule);

        // 3. schedule_index 등록 - 기간 시작일과 종료일의 월이 다르면 2개 이상의 schedule_index 를 등록해야 함.
        saveScheduleIndex(newSchedule, monthCnt);

        return newSchedule;
    }

    private void updateSchedule(ScheduleVO scheduleVO, Schedule savedSchedule) {
        // 1. schedule_index 와 schedule_work 제거거
        savedSchedule.getScheduleIndexes().removeAll(savedSchedule.getScheduleWorksIds());
        savedSchedule.getWorks().removeAll(savedSchedule.getWorks());

        // 2 schedule & schedule_work
        savedSchedule.setScheduleAtAdminPutSchedule(scheduleVO, workService.findUseWorkAll().size());

        // 3. 기간 시작일과 종료일로 schedule_index 갯수 구하기
        int monthCnt = getMonthCnt(savedSchedule);

        // 4. schedule_index 등록 - 기간 시작일과 종료일의 월이 다르면 2개 이상의 schedule_index 를 등록해야 함.
        saveScheduleIndex(savedSchedule, monthCnt);
    }

    private void deleteSchedule(Schedule savedSchedule) {
        // 1. schedule_index 삭제
        for (ScheduleIndex scheduleIndex : savedSchedule.getScheduleIndexes()) {
            scheduleIndexRepository.delete(scheduleIndex);
        }

        // 2. schedule_work 삭제
        savedSchedule.getWorks().removeAll(savedSchedule.getWorks());

        // 3. schedule 삭제
        scheduleRepository.delete(savedSchedule);
    }

    private int getMonthCnt(Schedule newSchedule) {
        int monthCnt = (newSchedule.getEndDate().getYear() * 12 + newSchedule.getEndDate().getMonth().getValue()) - (newSchedule.getStartDate().getYear() * 12 + newSchedule.getStartDate().getMonth().getValue());

        if(monthCnt > 0) return monthCnt;

        return monthCnt * (-1);
    }

    private boolean haveRightForAdminAddUpdateDelete(){
        if(userInfo.isRoleAdminProject()) return true;
        return false;
    }

    private boolean haveRightForUserUpdateDelete(Schedule savedSchedule){
        if(userInfo.isRoleAdminProject()) return true;
        if(savedSchedule.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }

    private void reCalculateSchedule(String ActionType, Schedule schedule){
        if(!"HOLIDAY".equalsIgnoreCase(schedule.getType())) return;;

        // 실제 공정 조정 처리 추가 예정
        if("ADD".equalsIgnoreCase(ActionType)) return;;
        if("UPDATE".equalsIgnoreCase(ActionType)) return;;
        if("DELETE".equalsIgnoreCase(ActionType)) return;;

        return;
    }
}
