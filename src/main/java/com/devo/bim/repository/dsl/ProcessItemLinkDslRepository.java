package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.LinkDTO;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.ProcessItemLink;
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
public class ProcessItemLinkDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<LinkDTO> findLinkDTOsCurrentVersion(long projectId) {
        return queryFactory
                .select(Projections.constructor(LinkDTO.class, processItemLink))
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .innerJoin(processItem.processItemLinks, processItemLink)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEqTrue()
                        , getWhereTaskStatusEqReg()
                ).orderBy(
                        getOrderBySortNo(true)
                ).fetch();
    }

    public List<ProcessItemLink> findProcessItemLinksCurrentVersion(long projectId) {
        return queryFactory
                .select(processItemLink)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .innerJoin(processItem.processItemLinks, processItemLink)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEqTrue()
                        , getWhereTaskStatusEqReg()
                ).orderBy(
                        getOrderBySortNo(true)
                ).fetch();
    }

    public List<LinkDTO> findProcessItemLinksByProcessId(long projectId, Long processId) {
        return queryFactory
                .select(Projections.constructor(LinkDTO.class, processItemLink))
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .innerJoin(processItem.processItemLinks, processItemLink)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereProcessInfoIdEq(processId)
                ).orderBy(
                        getOrderBySortNo(true)
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIsCurrentVersionEqTrue() {
        return processInfo.isCurrentVersion.eq(true);
    }

    private BooleanExpression getWhereProcessInfoIdEq(Long processId) {
        return processInfo.id.eq(processId);
    }

    private BooleanExpression getWhereTaskStatusEqReg() {
        return processItem.taskStatus.eq(TaskStatus.REG);
    }

    private BooleanExpression getWhereTaskDepthInZeroOrOne() {
        return processItem.taskDepth.in(0, 1);
    }

    private OrderSpecifier<Integer> getOrderBySortNo(boolean isAsc) {
        if(isAsc) return processItemLink.sortNo.asc();
        return processItemLink.sortNo.desc();
    }

    private OrderSpecifier<Integer> getOrderByGanttSortNoDesc() {
        return processItem.ganttSortNo.desc();
    }
}
