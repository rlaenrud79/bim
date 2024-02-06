package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.TaskDTO;
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
import static com.devo.bim.model.entity.QVmProcessItem.vmProcessItem;

@Repository
@RequiredArgsConstructor
public class TaskDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<TaskDTO> findProcessItemsCurrentVersion(long projectId) {
        return queryFactory
                .select(Projections.constructor(TaskDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEqTrue()
                        , getWhereTaskStatus(TaskStatus.REG)
                ).orderBy(
                        //getOrderByGanttSortNo(true)
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<TaskDTO> findProcessItemsByProcessId(long projectId, Long processId) {
        return queryFactory
                .select(Projections.constructor(TaskDTO.class, processItem))
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereProcessInfoIdEq(processId)
                ).orderBy(
                        getOrderByGanttSortNo(false)
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIsCurrentVersionEqTrue() {
        return processInfo.isCurrentVersion.eq(true);
    }

    private BooleanExpression getWhereTaskStatus(TaskStatus taskStatus){
        return vmProcessItem.taskStatus.eq(taskStatus);
    }

    private BooleanExpression getWhereProcessInfoIdEq(Long processId) {
        return processInfo.id.eq(processId);
    }

    private OrderSpecifier<Integer> getOrderByGanttSortNo(boolean isAsc) {
        if(isAsc) return processItem.ganttSortNo.asc();
        return processItem.ganttSortNo.desc();
    }
}
