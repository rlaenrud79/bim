package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ProcessDTO;
import com.devo.bim.model.enumulator.TaskStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QProcessItemLink.processItemLink;

@Repository
@RequiredArgsConstructor
public class ProcessDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProcessDTO> findProcessDTOsForExcel(long projectId) {
        return queryFactory
                .select( Projections.constructor(ProcessDTO.class, processItem))
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEqTrue()
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        //, getWhereTaskDepthNotInZero()
                ).orderBy(
                        getOrderByGanttSortNo(true)
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIsCurrentVersionEqTrue() {
        return processInfo.isCurrentVersion.eq(true);
    }

    private BooleanExpression getWhereTaskStatusEq(TaskStatus taskStatus) {
        return processItem.taskStatus.eq(taskStatus);
    }

    private BooleanExpression getWhereTaskDepthNotInZero() {
        return processItem.taskDepth.notIn(0);
    }

    private OrderSpecifier<Integer> getOrderByGanttSortNo(boolean isAsc) {
        if(isAsc) return processItem.ganttSortNo.asc();
        return processItem.ganttSortNo.desc();
    }


}
