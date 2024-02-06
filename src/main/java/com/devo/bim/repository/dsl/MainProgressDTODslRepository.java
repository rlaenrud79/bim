package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.MainProgressDTO;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;

@Repository
@RequiredArgsConstructor
public class MainProgressDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public MainProgressDTO getMainProcessRate(long projectId){
        return queryFactory
                .select(Projections.constructor(MainProgressDTO.class,
                    processItem.duration.coalesce(0).asNumber().sum()
                        , Expressions.numberPath(BigDecimal.class, processItem.progressRate
                                        .coalesce(new BigDecimal("0.00"))
                                        .toString()).multiply(Expressions.numberPath(Integer.class, processItem.duration
                                                                            .coalesce(0)
                                                                            .toString())).sum()
                        , processItem.cost.coalesce(new BigDecimal("0.00")).asNumber().sum()
                        , processItem.paidCost.coalesce(new BigDecimal("0.00")).asNumber().sum()
                ))
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEq(true)
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                ).fetchOne();
    }


    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIsCurrentVersionEq(boolean isCurrentVersion) {
        return processInfo.isCurrentVersion.eq(isCurrentVersion);
    }

    private BooleanExpression getWhereTaskStatusEq(TaskStatus taskStatus) {
        return processItem.taskStatus.eq(taskStatus);
    }

    private BooleanExpression getWhereGanttTaskTypeEq(TaskType taskType) {
        return processItem.ganttTaskType.eq(taskType);
    }
}
