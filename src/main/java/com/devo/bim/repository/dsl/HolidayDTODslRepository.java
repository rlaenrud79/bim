package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.HolidayDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class HolidayDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<HolidayDTO> findHolidaysAllWorkByProjectId(long projectId, int startYear, int startMonth, int endYear, int endMonth, String type){
        return queryFactory
                .select(Projections.constructor(HolidayDTO.class, schedule))
                .distinct()
                .from(schedule)
                .join(schedule.scheduleIndexes, scheduleIndex)
                .join(schedule.works, work)
                .where(
                        getWhereWorkProjectIdEq(projectId)
                        , getWhereScheduleIndexCalYearMonthBetween(startYear, startMonth,endYear, endMonth)
                        , getWhereScheduleTypeEq(type)
                        , getWhereIsAllWorkEq("true")
                ).fetch();
    }

    public List<HolidayDTO> findHolidaysClassifiedWorkIdByProjectId(long projectId, int startYear, int startMonth, int endYear, int endMonth, String type){
        return queryFactory
                .select(Projections.constructor(HolidayDTO.class, schedule, work))
                .distinct()
                .from(schedule)
                .join(schedule.scheduleIndexes, scheduleIndex)
                .join(schedule.works, work)
                .where(
                        getWhereWorkProjectIdEq(projectId)
                        , getWhereScheduleIndexCalYearMonthBetween(startYear, startMonth,endYear, endMonth)
                        , getWhereScheduleTypeEq(type)
                        , getWhereIsAllWorkEq("false")
                ).fetch();
    }

    private BooleanExpression getWhereWorkProjectIdEq(long projectId){
        return work.projectId.eq(projectId);
    }

    @NotNull
    private BooleanExpression getWhereIsAllWorkEq(String allWork) {
        if(StringUtils.equalsIgnoreCase(allWork, "true")) return schedule.isAllWork.eq(true);
        if(StringUtils.equalsIgnoreCase(allWork, "false")) return schedule.isAllWork.eq(false);
        return null;
    }

    @NotNull
    private BooleanExpression getWhereScheduleIndexCalYearMonthBetween(int startYear, int startMonth, int endYear, int endMonth) {
        String startYearMonth = startYear + "-" + checkMonthDayLength(startMonth);
        String endYearMonth = endYear + "-" + checkMonthDayLength(endMonth);
        return scheduleIndex.calYearMonth.between(startYearMonth, endYearMonth);
    }

    private BooleanExpression getWhereScheduleTypeEq(String type) {
        if(!StringUtils.isEmpty(type)) return schedule.type.equalsIgnoreCase(type);
        return null;
    }

    private String checkMonthDayLength(int month){
        if(month >= 10) return Integer.toString(month);
        return "0" + month;
    }
}
