package com.devo.bim.repository.dsl;

import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.SimulationDTO;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.TaskType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SimulationDslRepository {
    private final JPAQueryFactory queryFactory;
    //private final String[] params = new String[] { "EA100800", "EA100810", "EA100830" };

    public List<SimulationDTO> findTaskExchangeIds(long projectId) {
        return queryFactory
                .select(Projections.constructor(SimulationDTO.class, processItem))
                .from(processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersion(true)
                        , getWhereValidateEq(ProcessValidateResult.SUCCESS)
                        , getWhereEndDateNotEqualEmpty()
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        //, getWherePhasingCodeIn(params)
                ).orderBy(
                        getOrderByEndDateAsc()
                ).fetch();
    }

    public List<SimulationDTO> findTaskExchangeIdsByPhasingCode(long projectId, String phasingCode) {
        return queryFactory
                .select(Projections.constructor(SimulationDTO.class, processItem))
                .from(processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersion(true)
                        , getWhereValidateEq(ProcessValidateResult.SUCCESS)
                        , getWhereEndDateNotEqualEmpty()
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWherePhasingCodeEq(phasingCode)
                ).orderBy(
                        getOrderByEndDateAsc()
                ).fetch();
    }

    public List<SimulationDTO> findTaskExchangeIdsByPhasingCodes(long projectId, String[] phasingCodes) {
    	return queryFactory
    			.select(Projections.constructor(SimulationDTO.class, processItem))
    			.from(processItem)
    			.innerJoin(processItem.processInfo, processInfo)
    			.where(
    					getWhereProjectIdEq(projectId)
    					, getWhereIsCurrentVersion(true)
    					, getWhereValidateEq(ProcessValidateResult.SUCCESS)
    					, getWhereEndDateNotEqualEmpty()
    					, getWhereGanttTaskTypeEq(TaskType.TASK)
    					, getWherePhasingCodeIn(phasingCodes)
    					).orderBy(
    							getOrderByEndDateAsc()
    							).fetch();
    }

    public List<SimulationDTO> findTaskExchangeIdsUtilToday(long projectId) {
        return queryFactory
                .select(Projections.constructor(SimulationDTO.class, processItem))
                .from(processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersion(true)
                        , getWhereValidateEq(ProcessValidateResult.SUCCESS)
                        , getWhereEndDateNotEqualEmpty()
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWhereEndDateGoe()
                ).orderBy(
                        getOrderByEndDateAsc()
                ).fetch();
    }

    private BooleanExpression getWhereEndDateGoe() {
        return processItem.endDate.loe(Utils.todayPlusDayString(1));
    }

    private OrderSpecifier<String> getOrderByEndDateAsc() {
        return processItem.endDate.asc();
    }

    private BooleanExpression getWhereGanttTaskTypeEq(TaskType taskType) {
        return processItem.ganttTaskType.eq(taskType);
    }

    private BooleanExpression getWhereEndDateNotEqualEmpty() {
        return processItem.endDate.notEqualsIgnoreCase("");
    }

    private BooleanExpression getWhereValidateEq(ProcessValidateResult processValidateResult) {
        return processItem.validate.eq(processValidateResult);
    }

    private BooleanExpression getWhereIsCurrentVersion(boolean isCurrentVersion) {
        return processInfo.isCurrentVersion.eq(isCurrentVersion);
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWherePhasingCodeEq(String phasingCode) {
        return processItem.phasingCode.eq(phasingCode);
    }

    private BooleanExpression getWherePhasingCodeIn(String[] phasingCode){
        return processItem.phasingCode.in(phasingCode);
    }
}
