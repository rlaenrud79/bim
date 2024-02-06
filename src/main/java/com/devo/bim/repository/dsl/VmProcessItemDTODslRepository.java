package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ProcessItemCostDTO;
import com.devo.bim.model.dto.ProcessItemCostDetailDTO;
import com.devo.bim.model.dto.VmJobSheetProcessItemCostDTO;
import com.devo.bim.model.dto.VmProcessItemDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.ProcessCategoryDefaultCode;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.devo.bim.model.entity.QGisung.gisung;
import static com.devo.bim.model.entity.QGisungProcessItem.gisungProcessItem;
import static com.devo.bim.model.entity.QGisungProcessItemCostDetail.gisungProcessItemCostDetail;
import static com.devo.bim.model.entity.QProcessCategory.processCategory;
import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QProcessItemCostDetail.processItemCostDetail;
import static com.devo.bim.model.entity.QVmJobSheetProcessItemCost.vmJobSheetProcessItemCost;
import static com.devo.bim.model.entity.QVmProcessItem.vmProcessItem;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class VmProcessItemDTODslRepository {
    private final JPAQueryFactory queryFactory;

    QVmProcessItem subVmProcessItem = new QVmProcessItem("subVmProcessItem");
    QWork subWork = new QWork("subWork");

    public List<VmProcessItemDTO> findProcessItemCostCurrentVersion(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem
                        , processInfo.title
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmProcessItem.cost.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(subVmProcessItem)
                                        .innerJoin(subVmProcessItem.processInfo, processInfo)
                                        .where(
                                                processInfo.projectId.eq(projectId)
                                                , processInfo.isCurrentVersion.eq(true)
                                                , subVmProcessItem.work.isNotNull()
                                                , subVmProcessItem.taskStatus.eq(TaskStatus.REG)
                                                , getWhereSubWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                                                , getWhereSubPhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                                                , getWhereSubTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                                                , getWhereSubStartDateGoe(searchProcessItemCostVO.getStartDate())
                                                , getWhereSubEndDateLoe(searchProcessItemCostVO.getEndDate())
                                                , getWhereSubCateEq(searchProcessItemCostVO.getCateNo())
                                        ),
                                "cateProgressRate")
                ))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                        , getWhereUpCodeEq(searchProcessItemCostVO.getCateNo(), searchProcessItemCostVO.getUpCode())
                        , getWhereGanttTaskTypeEq(searchProcessItemCostVO.getGanttTaskType())
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemSearch(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem
                        , processInfo.title
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmProcessItem.progressRate.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(subVmProcessItem)
                                        .innerJoin(subVmProcessItem.processInfo, processInfo)
                                        .where(
                                                processInfo.projectId.eq(projectId)
                                                , processInfo.isCurrentVersion.eq(true)
                                                , subVmProcessItem.work.isNotNull()
                                                , subVmProcessItem.taskStatus.eq(TaskStatus.REG)
                                        ),
                                "cateProgressRate")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItem.id)
                                        .from(gisungProcessItem)
                                        .where(
                                                gisungProcessItem.processItem.id.eq(vmProcessItem.id)
                                                , gisungProcessItem.gisung.id.eq(searchProcessItemCostVO.getId())
                                        ),
                                "gisungProcessItemId")
                ))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereSubTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereProcessItemCostCheck(searchProcessItemCostVO.getIsMinCost())
                        , getWherePaidProgressRate()
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemCostChildrenCurrentVersion(long projectId, long parentProcessItemId) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereParentIdEq(parentProcessItemId)
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findGisungProcessItemCostCurrentVersion(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        QProcessItemCostPay subProcessItemCostPay = new QProcessItemCostPay("subProcessItemCostPay");
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemCostByParentIdCurrentVersion(long projectId, long parentProcessItemId) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereIdEq(parentProcessItemId)
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemCostDetailBookmark(long projectId) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , vmProcessItem.isBookmark.eq(true)
                        , vmProcessItem.ganttTaskType.eq(TaskType.TASK)
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemsCostCurrentVersionByPhasingCodes(long projectId, String phasingCode) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWherePhasingCodeContains(phasingCode)
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmProcessItemDTO> findProcessItemCostForPass(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.ganttTaskType.eq(TaskType.TASK)
                        , getWhereWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public VmProcessItemDTO findProcessItemByCate(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(Projections.constructor(VmProcessItemDTO.class, vmProcessItem
                        , processInfo.title
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmProcessItem.progressRate.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(subVmProcessItem)
                                        .innerJoin(subVmProcessItem.processInfo, processInfo)
                                        .where(
                                                processInfo.projectId.eq(projectId)
                                                , processInfo.isCurrentVersion.eq(true)
                                                , subVmProcessItem.work.isNotNull()
                                                , subVmProcessItem.taskStatus.eq(TaskStatus.REG)
                                                , getWhereSubWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                                                , getWhereSubPhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                                                , getWhereSubTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                                                , getWhereSubStartDateGoe(searchProcessItemCostVO.getStartDate())
                                                , getWhereSubEndDateLoe(searchProcessItemCostVO.getEndDate())
                                                , getWhereSubCateEq(searchProcessItemCostVO.getCateNo())
                                        ),
                                "cateProgressRate")
                ))
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(projectId, searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                        , getWhereCate1Eq(searchProcessItemCostVO.getCate1())
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).limit(1)
                .fetchOne();
    }

    private BooleanExpression getWhereSubWorkIdEq(long projectId, long workId) {
        if (workId == 0) return null;
        //return subVmProcessItem.work.id.eq(workId);
        return subVmProcessItem.work.id.in(
                JPAExpressions.select(work.id)
                        .from(work)
                        .where(work.projectId.eq(projectId)
                                .and(work.status.eq(WorkStatus.USE))
                                .and(work.upId.eq(workId).or(work.id.eq(workId))
                                        .or(work.id.in(
                                                        JPAExpressions.select(subWork.upId)
                                                                .from(subWork)
                                                                .where(subWork.id.eq(workId))
                                                )
                                        ))
                        )
        );
    }

    private BooleanExpression getWhereSubPhasingCodeContains(String phasingCode) {
        if (StringUtils.isEmpty(phasingCode)) return null;
        return subVmProcessItem.phasingCode.eq(phasingCode);
    }

    private BooleanExpression getWhereSubTaskNameCodeContains(String taskName) {
        if (StringUtils.isEmpty(taskName)) return null;
        return subVmProcessItem.taskName.containsIgnoreCase(taskName);
    }

    private BooleanExpression getWhereSubStartDateGoe(String startDate) {
        if (StringUtils.isEmpty(startDate)) return null;
        return subVmProcessItem.startDate.goe(startDate);
    }

    private BooleanExpression getWhereSubEndDateLoe(String endDate) {
        if (StringUtils.isEmpty(endDate)) return null;
        return subVmProcessItem.endDate.loe(endDate);
    }

    private BooleanExpression getWhereWorkIdEq(long projectId, long workId) {
        if (workId == 0) return null;
        //return vmProcessItem.work.id.eq(workId);
        return vmProcessItem.work.id.in(
                JPAExpressions.select(work.id)
                        .from(work)
                        .where(work.projectId.eq(projectId)
                                .and(work.status.eq(WorkStatus.USE))
                                .and(work.upId.eq(workId).or(work.id.eq(workId))
                                        .or(work.id.in(
                                                JPAExpressions.select(subWork.upId)
                                                        .from(subWork)
                                                        .where(subWork.id.eq(workId))
                                                        )
                                        ))
                        )
        );
    }

    private BooleanExpression getWherePhasingCodeContains(String phasingCode) {
        if (StringUtils.isEmpty(phasingCode)) return null;
        return vmProcessItem.phasingCode.eq(phasingCode);
    }

    private BooleanExpression getWhereTaskNameCodeContains(String taskName) {
        if (StringUtils.isEmpty(taskName)) return null;
        return vmProcessItem.taskName.containsIgnoreCase(taskName);
    }

    private BooleanExpression getWhereGanttTaskTypeEq(String ganttTaskType) {
        if (StringUtils.isEmpty(ganttTaskType)) return null;
        return vmProcessItem.ganttTaskType.eq(TaskType.TASK);
    }

    private BooleanExpression getWhereStartDateGoe(String startDate) {
        if (StringUtils.isEmpty(startDate)) return null;
        return vmProcessItem.startDate.goe(startDate);
    }

    private BooleanExpression getWhereEndDateLoe(String endDate) {
        if (StringUtils.isEmpty(endDate)) return null;
        return vmProcessItem.endDate.loe(endDate);
    }

    private BooleanExpression getWhereUpCodeEq(int cateNo, String upCode) {

        if (cateNo == 1) {
            return vmProcessItem.cate2.isNull().or(vmProcessItem.cate2.eq(""));
        } else if (cateNo == 2) {
            return vmProcessItem.cate1.eq(upCode)
                    .and(vmProcessItem.cate2.isNotNull().and(vmProcessItem.cate2.ne("")))
                    .and(vmProcessItem.cate3.isNull().or(vmProcessItem.cate3.eq("")));
        } else if (cateNo == 3) {
            return vmProcessItem.cate2.eq(upCode)
                    .and(vmProcessItem.cate3.isNotNull().and(vmProcessItem.cate3.ne("")))
                    .and(vmProcessItem.cate4.isNull().or(vmProcessItem.cate4.eq("")));
        } else if (cateNo == 4) {
            return vmProcessItem.cate3.eq(upCode)
                    .and(vmProcessItem.cate4.isNotNull().and(vmProcessItem.cate4.ne("")))
                    .and(vmProcessItem.cate5.isNull().or(vmProcessItem.cate5.eq("")));
        } else if (cateNo == 5) {
            return vmProcessItem.cate4.eq(upCode)
                    .and(vmProcessItem.cate5.isNotNull().and(vmProcessItem.cate5.ne("")))
                    .and(vmProcessItem.cate6.isNull().or(vmProcessItem.cate6.eq("")));
        } else if (cateNo == 6) {
            return vmProcessItem.cate5.eq(upCode)
                    .and(vmProcessItem.cate6.isNotNull().and(vmProcessItem.cate6.ne("")));
        } else {
            return null;
        }
    }

    private BooleanExpression getWhereSubCateEq(int cateNo) {
        if (cateNo == 1) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1);
        } else if (cateNo == 2) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1)
                    .and(subVmProcessItem.cate2.eq(vmProcessItem.cate2));
        } else if (cateNo == 3) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1)
                    .and(subVmProcessItem.cate2.eq(vmProcessItem.cate2))
                    .and(subVmProcessItem.cate3.eq(vmProcessItem.cate3));
        } else if (cateNo == 4) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1)
                    .and(subVmProcessItem.cate2.eq(vmProcessItem.cate2))
                    .and(subVmProcessItem.cate3.eq(vmProcessItem.cate3))
                    .and(subVmProcessItem.cate4.eq(vmProcessItem.cate4));
        } else if (cateNo == 5) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1)
                    .and(subVmProcessItem.cate2.eq(vmProcessItem.cate2))
                    .and(subVmProcessItem.cate3.eq(vmProcessItem.cate3))
                    .and(subVmProcessItem.cate4.eq(vmProcessItem.cate4))
                    .and(subVmProcessItem.cate5.eq(vmProcessItem.cate5));
        } else if (cateNo == 6) {
            return subVmProcessItem.cate1.eq(vmProcessItem.cate1)
                    .and(subVmProcessItem.cate2.eq(vmProcessItem.cate2))
                    .and(subVmProcessItem.cate3.eq(vmProcessItem.cate3))
                    .and(subVmProcessItem.cate4.eq(vmProcessItem.cate4))
                    .and(subVmProcessItem.cate5.eq(vmProcessItem.cate5))
                    .and(subVmProcessItem.cate6.eq(vmProcessItem.cate6));
        } else {
            return null;
        }
    }

    private BooleanExpression getWhereCate1Eq(String cate1) {

        return vmProcessItem.cate1.eq(cate1)
                .and(vmProcessItem.cate2.isNull().or(vmProcessItem.cate2.eq("")));
    }

    private BooleanExpression getWherePaidProgressRate() {
        return vmProcessItem.paidProgressRate.lt(1.0000);
    }

    private BooleanExpression getWhereProcessItemCostCheck(Boolean isMinCost) {
        if (!isMinCost) return null;
        return vmProcessItem.progressRate.goe(100);
    }

    private BooleanExpression getWhereGisungProcessItemCostDetailCodeEq(String code) {
        if (StringUtils.isEmpty(code)) return null;
        return processItemCostDetail.code.eq(code);
    }

    private BooleanExpression getWhereProcessItemIdNotIn(List<Long> processItemIds){
        if (processItemIds.size() == 0) return null;
        return vmProcessItem.id.notIn(processItemIds);
    }

    private BooleanExpression getWhereParentIdEq(long parentProcessItemId) {
        if (parentProcessItemId == 0) return null;
        return vmProcessItem.parentId.eq(parentProcessItemId);
    }

    private BooleanExpression getWhereIdEq(long parentProcessItemId){
        if (parentProcessItemId == 0) return null;
        return vmProcessItem.id.eq(parentProcessItemId);
    }
}
