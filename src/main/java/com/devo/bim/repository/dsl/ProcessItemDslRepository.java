package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.ProcessItemCostDTO;
import com.devo.bim.model.dto.ProcessItemDTO;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.ProcessCategoryDefaultCode;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.devo.bim.model.vo.SearchCodeValidationVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import static com.devo.bim.model.entity.QGisungProcessItemCostDetail.gisungProcessItemCostDetail;
import static com.devo.bim.model.entity.QProcessCategory.processCategory;
import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QProcessItemCostDetail.processItemCostDetail;
import static com.devo.bim.model.entity.QVmGisungProcessItem.vmGisungProcessItem;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class ProcessItemDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProcessItem> findFirstProcessItemByProcessId(long processId) {
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProcessInfoIdEq(processId)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                ).orderBy(
                        getOrderByGanttSortNo(true)
                ).fetch();
    }

    public List<Work> findWorksByProcessInfoIdAndWorkId(long processInfoId, long workId) {
        return queryFactory
                .select(work)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .innerJoin(processItem.work, work)
                .where(
                        getWhereProcessInfoIdEq(processInfoId)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        , getWhereWorkIdEq(workId)
                ).fetch();
    }

    public List<ProcessItem> findProjectProcessItemByProcessId(long processId){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProcessInfoIdEq(processId)
                        , getWhereGanttTaskTypeEq(TaskType.PROJECT)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                ).orderBy(
                        getOrderByGanttSortNo(true)
                ).fetch();
    }

    public List<ProcessItem> findBrothersByParentProcessItem(ProcessItem parentProcessItem){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProcessInfoIdEq(parentProcessItem.getProcessInfo().getId())
                        , getWhereParentIdEq(parentProcessItem.getParentId())
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        , getWhereGanttSortNoGT(parentProcessItem)
                ).orderBy(
                        getOrderByGanttSortNo(false)
                ).fetch();
    }

    public List<ProcessItem> findChildrenByParentProcessItem(ProcessItem parentProcessItem){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProcessInfoIdEq(parentProcessItem.getProcessInfo().getId())
                        , getWhereParentIdEq(parentProcessItem.getId())
                        , getWhereTaskStatusEq(TaskStatus.REG)
                ).orderBy(
                        getOrderByGanttSortNo(false)
                ).fetch();
    }

    public List<ProcessItem> findChildrenByParentIds(long processId, List<Long> parentIds){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProcessInfoIdEq(processId)
                        , getWhereParentIdIn(parentIds)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                ).orderBy(
                        getOrderByGanttSortNo(false)
                ).fetch();
    }

    public List<ProcessItem> findCurrentVersionTaskByProjectId(long projectId){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereIsCurrentVersionEq(true)
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        , getWherePhasingCodeIsNotNull()
                        , getWherePhasingCodeIsNotSpace()
                ).orderBy(
                        getOrderByGanttSortNo(true)
                ).fetch();
    }

    public List<ProcessItem> findCurrentVersionTaskListByProjectId(SearchCodeValidationVO searchCodeValidationVO){
        return queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(searchCodeValidationVO.getProjectId())
                        , getWhereIsCurrentVersionEq(true)
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        , getWherePhasingCodeIsNotNull()
                        , getWherePhasingCodeIsNotSpace()
                        , getWhereValidateEq(searchCodeValidationVO.isMSuccess(), searchCodeValidationVO.isMFail() , searchCodeValidationVO.isMNone())
                        , getWhereSearchCondition(searchCodeValidationVO.getMSearchType(), searchCodeValidationVO.getMSearchText())
                ).orderBy(
                        getOrderByGanttSortNo(true)
                ).fetch();
    }

    public PageDTO<ProcessItem> findCurrentVersionTaskPageByProjectId(SearchCodeValidationVO searchCodeValidationVO, Pageable pageable){
        QueryResults<ProcessItem> results = queryFactory
                .select(processItem)
                .from(processInfo)
                .innerJoin(processInfo.processItems, processItem)
                .where(
                        getWhereProjectIdEq(searchCodeValidationVO.getProjectId())
                        , getWhereIsCurrentVersionEq(true)
                        , getWhereGanttTaskTypeEq(TaskType.TASK)
                        , getWhereTaskStatusEq(TaskStatus.REG)
                        , getWherePhasingCodeIsNotNull()
                        , getWherePhasingCodeIsNotSpace()
                        , getWhereValidateEq(searchCodeValidationVO.isMSuccess(), searchCodeValidationVO.isMFail(), searchCodeValidationVO.isMNone() )
                        , getWhereSearchCondition(searchCodeValidationVO.getMSearchType(), searchCodeValidationVO.getMSearchText())
                ).orderBy(
                        getOrderByGanttSortNo(true)
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

            List<ProcessItem> processItems = findCurrentVersionTaskByProjectId(searchCodeValidationVO.getProjectId());

        return new PageDTO<>(results.getResults(), pageable, results.getTotal(), getListCount(processItems, ProcessValidateResult.SUCCESS), getListCount(processItems, ProcessValidateResult.FAIL), getListCount(processItems, ProcessValidateResult.NONE));
    }

    private long getListCount(List<ProcessItem> processItems, ProcessValidateResult processValidateResult) {
        return processItems.stream().filter(t -> t.getValidate() == processValidateResult).count();
    }

    private BooleanExpression getWhereSearchCondition(String mSearchType, String mSearchText){
        if(StringUtils.isEmpty(mSearchText)) return null;
        if(mSearchType.equalsIgnoreCase("TASK_NAME")) return processItem.taskName.contains(mSearchText);
        if(mSearchType.equalsIgnoreCase("PHASING_CODE")) return processItem.phasingCode.eq(mSearchText);
        return null;
    }

    private BooleanExpression getWhereValidateEq(boolean isMSuccess, boolean isMFail, boolean isMNone) {
        if(isMSuccess && isMFail && isMNone) return processItem.validate.in(ProcessValidateResult.SUCCESS, ProcessValidateResult.FAIL, ProcessValidateResult.NONE);

        if(isMSuccess && isMFail && !isMNone) return processItem.validate.in(ProcessValidateResult.SUCCESS, ProcessValidateResult.FAIL);
        if(isMSuccess && !isMFail && isMNone) return processItem.validate.in(ProcessValidateResult.SUCCESS, ProcessValidateResult.NONE);
        if(!isMSuccess && isMFail && isMNone) return processItem.validate.in(ProcessValidateResult.FAIL, ProcessValidateResult.NONE);

        if(isMSuccess && !isMFail && !isMNone) return processItem.validate.in(ProcessValidateResult.SUCCESS);
        if(!isMSuccess && isMFail && !isMNone) return processItem.validate.in(ProcessValidateResult.FAIL);
        if(!isMSuccess && !isMFail && isMNone) return processItem.validate.in(ProcessValidateResult.NONE);

        if(isMSuccess && !isMFail) return processItem.validate.eq(ProcessValidateResult.SUCCESS);
        if(!isMSuccess && isMFail) return processItem.validate.eq(ProcessValidateResult.FAIL);
        if(!isMSuccess && isMFail) return processItem.validate.eq(ProcessValidateResult.FAIL);

        return processItem.validate.in(ProcessValidateResult.SUCCESS, ProcessValidateResult.FAIL, ProcessValidateResult.NONE);
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIsCurrentVersionEq(boolean isCurrentVersion) {
        return processInfo.isCurrentVersion.eq(isCurrentVersion);
    }

    private BooleanExpression getWhereParentIdEq(long parentId) {
        return processItem.parentId.eq(parentId);
    }

    private BooleanExpression getWhereParentIdIn(List<Long> parentIds){
        return processItem.parentId.in(parentIds);
    }

    private BooleanExpression getWhereProcessInfoIdEq(Long processInfoId) {
        return processInfo.id.eq(processInfoId);
    }

    private OrderSpecifier<Integer> getOrderByGanttSortNo(boolean isAsc) {
        if(isAsc) return processItem.ganttSortNo.asc();
        return processItem.ganttSortNo.desc();
    }

    private BooleanExpression getWhereWorkIdEq(long workId) {
        return work.id.eq(workId);
    }

    private BooleanExpression getWhereGanttTaskTypeEq(TaskType taskType) {
        return processItem.ganttTaskType.eq(taskType);
    }

    private BooleanExpression getWhereTaskStatusEq(TaskStatus taskStatus) {
        return processItem.taskStatus.eq(taskStatus);
    }

    private BooleanExpression getWherePhasingCodeIsNotNull() {
        return processItem.phasingCode.isNotNull();
    }

    private BooleanExpression getWherePhasingCodeIsNotSpace() {
        return processItem.phasingCode.notEqualsIgnoreCase("");
    }

    @NotNull
    private BooleanExpression getWhereGanttSortNoGT(ProcessItem parentProcessItem) {
        return processItem.ganttSortNo.gt(parentProcessItem.getGanttSortNo());
    }
}
