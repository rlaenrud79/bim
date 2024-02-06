package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ScheduleDTO;
import com.devo.bim.model.entity.Schedule;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.repository.spring.WorkRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QSchedule.schedule;
import static com.devo.bim.model.entity.QScheduleIndex.scheduleIndex;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class ScheduleDTODslRepository {
    private final JPAQueryFactory queryFactory;
    private final WorkRepository workRepository;

    public List<ScheduleDTO> findByYearMonthForAdmin(long projectId, int searchYear, int searchMonth){
        return queryFactory
                .select(Projections.constructor(ScheduleDTO.class, schedule, Expressions.asNumber( getTotalWorkCnt(projectId)), Expressions.asNumber(projectId)))
                .distinct()
                .from(work)
                .join(work.schedules, schedule)
                .join(schedule.scheduleIndexes, scheduleIndex)
                .where(
                        getWhereScheduleIndexCalYearEq(searchYear),
                        getWhereScheduleIndexCalMonthEq(searchMonth),
                        getWhereWorkProjectIdEq(projectId)
                ).fetch();
    }

    public List<ScheduleDTO> findByYearMonthForUser(long projectId, int searchYear, int searchMonth, List<Long> userWorksIds, long sessionId){
        return queryFactory
                .select(Projections.constructor(ScheduleDTO.class, schedule, Expressions.asNumber( getTotalWorkCnt(projectId)), Expressions.asNumber(sessionId), Expressions.asNumber(projectId)))
                .distinct()
                .from(work)
                .join(work.schedules, schedule)
                .join(schedule.scheduleIndexes, scheduleIndex)
                .where(
                        getWhereScheduleIndexCalYearEq(searchYear),
                        getWhereScheduleIndexCalMonthEq(searchMonth),
                        getWhereWorkProjectIdEq(projectId),
                        getWhereWorkIdIn(userWorksIds)
                ).fetch();
    }

    private long getTotalWorkCnt(long projectId) {
        return workRepository.findAll().stream().filter(t -> t.getProjectId() == projectId &&t.getStatus() == WorkStatus.USE).count();
    }

    private BooleanExpression getWhereWorkProjectIdEq(long projectId) {
        return work.projectId.eq(projectId);
    }

    private BooleanExpression getWhereScheduleIndexCalMonthEq(int searchMonth) {
        return scheduleIndex.calMonth.eq(searchMonth);
    }

    private BooleanExpression getWhereScheduleIndexCalYearEq(int searchYear) {
        return scheduleIndex.calYear.eq(searchYear);
    }

    private BooleanExpression getWhereWorkIdIn(List<Long> userWorksIds) {
        return work.id.in(userWorksIds);
    }
}
