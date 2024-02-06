package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.ProcessItemCostPay;
import com.devo.bim.model.entity.QGisungProcessItemCostDetail;
import com.devo.bim.model.entity.QProcessItem;
import com.devo.bim.model.entity.QProcessItemCostPay;
import com.devo.bim.model.enumulator.GisungStatus;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.enumulator.TaskType;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.devo.bim.model.entity.QGisung.gisung;
import static com.devo.bim.model.entity.QGisungProcessItemCostDetail.gisungProcessItemCostDetail;
import static com.devo.bim.model.entity.QProcessInfo.processInfo;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QProcessItemCategory.processItemCategory;
import static com.devo.bim.model.entity.QProcessItemCostDetail.processItemCostDetail;
import static com.devo.bim.model.entity.QProcessItemCostPay.processItemCostPay;
import static com.devo.bim.model.entity.QVmProcessItem.vmProcessItem;

@Repository
@RequiredArgsConstructor
public class ProcessItemCostDslRepository {
    private final JPAQueryFactory queryFactory;
    QProcessItem subProcessItem = new QProcessItem("subProcessItem");

    public List<ProcessItemCostDTO> findProcessItemCostCateCurrentVersion(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDTO.class
                        , processItem.cate1
                        , processItem.cate2
                        , processItem.cate3
                        , processItem.cate4
                        , processItem.cate5
                        , processItem.cate6
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate1)
                                        ),
                                "cate1Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate2)
                                        ),
                                "cate2Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate3)
                                        ),
                                "cate3Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate4)
                                        ),
                                "cate4Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate5)
                                        ),
                                "cate5Name")
                ))
                .from(processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , processItem.work.isNotNull()
                        , processItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                        , getWhereCate1Eq(searchProcessItemCostVO.getCate1())
                        , getWhereCate2Eq(searchProcessItemCostVO.getCate2())
                        , getWhereCate3Eq(searchProcessItemCostVO.getCate3())
                        , getWhereCate4Eq(searchProcessItemCostVO.getCate4())
                        , getWhereCate5Eq(searchProcessItemCostVO.getCate5())
                        , getWhereCate6Eq(searchProcessItemCostVO.getCate6())
                ).orderBy(
                        processItem.rowNum.asc()
                        , processItem.parentRowNum.asc()
                ).fetch();
    }

    public List<ProcessItemCostDTO> findGisungProcessItemCostCurrentVersionBack(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        QProcessItemCostPay subProcessItemCostPay = new QProcessItemCostPay("subProcessItemCostPay");
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDTO.class, processItem
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCostPay.cost)
                                        .from(processItemCostPay)
                                        .where(
                                                processItemCostPay.processItem.id.eq(processItem.id)
                                                , processItemCostPay.id.eq(
                                                        JPAExpressions.select(subProcessItemCostPay.id.max())
                                                                .from(subProcessItemCostPay)
                                                                .where(
                                                                        subProcessItemCostPay.processItem.id.eq(processItem.id)
                                                                )
                                                )
                                        ),
                                "todayCost")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCostPay.progressRate)
                                        .from(processItemCostPay)
                                        .where(
                                                processItemCostPay.processItem.id.eq(processItem.id)
                                                , processItemCostPay.id.eq(
                                                        JPAExpressions.select(subProcessItemCostPay.id.max())
                                                                .from(subProcessItemCostPay)
                                                                .where(
                                                                        subProcessItemCostPay.processItem.id.eq(processItem.id)
                                                                )
                                                )
                                        ),
                                "todayProgressRate")))
                .from(processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , processItem.work.isNotNull()
                        , processItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(searchProcessItemCostVO.getWorkId())
                        , getWherePhasingCodeContains(searchProcessItemCostVO.getPhasingCode())
                        , getWhereTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereStartDateGoe(searchProcessItemCostVO.getStartDate())
                        , getWhereEndDateLoe(searchProcessItemCostVO.getEndDate())
                ).orderBy(
                        processItem.rowNum.asc()
                        , processItem.parentRowNum.asc()
                ).fetch();
    }


    public List<Long> findProcessItemIdCurrentVersion(long projectId, SearchProcessItemCostVO searchProcessItemCostVO) {
        return queryFactory
                .select(vmProcessItem.id)
                .from(vmProcessItem)
                .innerJoin(vmProcessItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , vmProcessItem.work.isNotNull()
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereWorkIdEq(searchProcessItemCostVO.getWorkId())
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

    public List<ProcessItemCostDetailDTO> findProcessItemCostDetail(long projectId, Long processItemId, String rowState) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDetailDTO.class, processItemCostDetail, Expressions.asString(rowState)))
                .from(processItemCostDetail)
                .innerJoin(processItemCostDetail.processItem, processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processItem.ganttTaskType.eq(TaskType.TASK)
                        , processItemCostDetail.processItem.id.eq(processItemId)
                ).orderBy(
                        processItemCostDetail.code.asc()
                        , processItemCostDetail.id.asc()
                ).fetch();
    }

    public List<ProcessItemCostPayDTO> findProcessItemCostPay(long projectId, long processItemId) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCostPayDTO.class, processItemCostPay))
                .from(processItemCostPay)
                .innerJoin(processItemCostPay.processItem, processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processItem.ganttTaskType.eq(TaskType.TASK)
                        , processItemCostPay.processItem.id.eq(processItemId)
                ).orderBy(
                        processItemCostPay.costNo.desc()
                        , processItemCostPay.id.desc()
                ).fetch();
    }

    public ProcessItemCostPay getProcessItemCostPayAtLatest(long projectId, long processItemId) {
        return queryFactory
                .select(processItemCostPay)
                .from(processItemCostPay)
                .innerJoin(processItemCostPay.processItem, processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processItem.ganttTaskType.eq(TaskType.TASK)
                        , processItemCostPay.processItem.id.eq(processItemId)
                ).limit(1)
                .orderBy(
                        processItemCostPay.costNo.desc()
                        , processItemCostPay.id.desc()
                ).fetchOne();
    }

    public PaidCostAllDTO getProcessItemCostPayLatestCostNo(long projectId, long processItemInfoId)
    {
        return queryFactory
                .select(Projections.constructor(PaidCostAllDTO.class, processItemCostPay))
                .from(processItemCostPay)
                .innerJoin(processItemCostPay.processItem, processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        ,processInfo.id.eq(processItemInfoId)
                        , processItem.ganttTaskType.eq(TaskType.TASK)
                ).limit(1)
                .orderBy(
                        processItemCostPay.costNo.desc()
                ).fetchOne();
    }

    public List<ProcessItemCostDetailDTO> getProcessItemCostPayGisungProcessItemCost(long processId) {
        QGisungProcessItemCostDetail subGisungProcessItemCostDetail = new QGisungProcessItemCostDetail("subGisungProcessItemCostDetail");
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDetailDTO.class, vmProcessItem,
                        processItemCostDetail
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.jobSheetProgressCount)
                                        .from(gisungProcessItemCostDetail)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "jobSheetProgressCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.jobSheetProgressAmount)
                                        .from(gisungProcessItemCostDetail)
                                        .leftJoin(gisungProcessItemCostDetail.gisung, gisung)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "jobSheetProgressAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.paidProgressCount)
                                        .from(gisungProcessItemCostDetail)
                                        .leftJoin(gisungProcessItemCostDetail.gisung, gisung)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "paidProgressCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.paidCost)
                                        .from(gisungProcessItemCostDetail)
                                        .leftJoin(gisungProcessItemCostDetail.gisung, gisung)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "paidCost")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.progressCount)
                                        .from(gisungProcessItemCostDetail)
                                        .leftJoin(gisungProcessItemCostDetail.gisung, gisung)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "progressCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungProcessItemCostDetail.progressCost)
                                        .from(gisungProcessItemCostDetail)
                                        .leftJoin(gisungProcessItemCostDetail.gisung, gisung)
                                        .where(
                                                gisungProcessItemCostDetail.id.eq(
                                                        JPAExpressions.select(subGisungProcessItemCostDetail.id.max())
                                                                .from(subGisungProcessItemCostDetail)
                                                                .leftJoin(subGisungProcessItemCostDetail.gisung, gisung)
                                                                .where(
                                                                        subGisungProcessItemCostDetail.processItem.id.eq(vmProcessItem.id)
                                                                        , subGisungProcessItemCostDetail.code.eq(processItemCostDetail.code)
                                                                        , getWhereStatus()
                                                                )
                                                )
                                        ),
                                "progressCost")))
                .from(vmProcessItem)
                .rightJoin(processItemCostDetail).on(processItemCostDetail.processItem.id.eq(vmProcessItem.id))
                .where(
                        vmProcessItem.processInfo.id.eq(processId)
                        , vmProcessItem.taskStatus.eq(TaskStatus.REG)
                ).orderBy(
                        vmProcessItem.cate1Display.asc().nullsFirst()
                        , vmProcessItem.cate2Display.asc().nullsFirst()
                        , vmProcessItem.cate3Display.asc().nullsFirst()
                        , vmProcessItem.cate4Display.asc().nullsFirst()
                        , vmProcessItem.cate5Display.asc().nullsFirst()
                        , vmProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<ProcessItemCostDetailDTO> findCostDetailByCode(long processId, String code, long gisungId) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDetailDTO.class, processItem.taskName
                        , processItem.phasingCode
                        , processItemCostDetail
                        , gisungProcessItemCostDetail.progressCount
                        , gisungProcessItemCostDetail.progressCost))
                .from(processItem)
                .rightJoin(processItemCostDetail).on(processItemCostDetail.processItem.id.eq(processItem.id))
                .leftJoin(gisungProcessItemCostDetail).on(gisungProcessItemCostDetail.processItem.id.eq(processItem.id).and(gisungProcessItemCostDetail.code.eq(processItemCostDetail.code)))
                .where(
                        processItem.processInfo.id.eq(processId)
                        , processItemCostDetail.code.eq(code)
                        , getWhereGisungIdEq(gisungId)
                        , getWhereCompareProcessCount()
                ).orderBy(
                        processItem.phasingCode.asc()
                ).fetch();
    }

    public List<ProcessItemCostDTO> findProcessItemCostSearchCode(long projectId, SearchProcessItemCostVO searchProcessItemCostVO, List<Long> processItemIds) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCostDTO.class, processItem
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate1)
                                        ),
                                "cate1Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate2)
                                        ),
                                "cate2Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate3)
                                        ),
                                "cate3Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate4)
                                        ),
                                "cate4Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItemCategory.name)
                                        .from(processItemCategory)
                                        .where(
                                                processItemCategory.code.eq(processItem.cate5)
                                        ),
                                "cate5Name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subProcessItem.progressRate.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(subProcessItem)
                                        .innerJoin(subProcessItem.processInfo, processInfo)
                                        .where(
                                                processInfo.projectId.eq(projectId)
                                                , processInfo.isCurrentVersion.eq(true)
                                                , subProcessItem.work.isNotNull()
                                                , subProcessItem.taskStatus.eq(TaskStatus.REG)
                                        ),
                                "cateProgressRate")
                ))
                .from(processItemCostDetail)
                .innerJoin(processItemCostDetail.processItem, processItem)
                .innerJoin(processItem.processInfo, processInfo)
                .where(
                        processInfo.projectId.eq(projectId)
                        , processInfo.isCurrentVersion.eq(true)
                        , processItem.work.isNotNull()
                        , processItem.taskStatus.eq(TaskStatus.REG)
                        , getWhereSubTaskNameCodeContains(searchProcessItemCostVO.getTaskName())
                        , getWhereGisungProcessItemCostDetailCodeEq(searchProcessItemCostVO.getCode())
                        , getWhereProcessItemIdNotIn(processItemIds)
                        , getWhereProcessItemCostCheck(searchProcessItemCostVO.getIsMinCost())
                        , getWherePaidProgressRate()
                ).orderBy(
                        processItem.phasingCode.asc()
                        , processItemCostDetail.code.asc()
                ).fetch();
    }

    private BooleanExpression getWhereStartDateGoe(String startDate) {
        if (StringUtils.isEmpty(startDate)) return null;
        return processItem.startDate.goe(startDate);
    }

    private BooleanExpression getWhereEndDateLoe(String endDate) {
        if (StringUtils.isEmpty(endDate)) return null;
        return processItem.endDate.loe(endDate);
    }

    private BooleanExpression getWhereSubStartDateGoe(String startDate) {
        if (StringUtils.isEmpty(startDate)) return null;
        return subProcessItem.startDate.goe(startDate);
    }

    private BooleanExpression getWhereSubEndDateLoe(String endDate) {
        if (StringUtils.isEmpty(endDate)) return null;
        return subProcessItem.endDate.loe(endDate);
    }

    private BooleanExpression getWhereWorkIdEq(long workId) {
        if (workId == 0) return null;
        return processItem.work.id.eq(workId);
    }
    private BooleanExpression getWhereSubWorkIdEq(long workId) {
        if (workId == 0) return null;
        return subProcessItem.work.id.eq(workId);
    }

    private BooleanExpression getWherePhasingCodeContains(String phasingCode) {
        if (StringUtils.isEmpty(phasingCode)) return null;
        return processItem.phasingCode.contains(phasingCode);
    }

    private BooleanExpression getWhereSubPhasingCodeContains(String phasingCode) {
        if (StringUtils.isEmpty(phasingCode)) return null;
        return subProcessItem.phasingCode.contains(phasingCode);
    }

    private BooleanExpression getWhereTaskNameCodeContains(String taskName) {
        if (StringUtils.isEmpty(taskName)) return null;
        return processItem.taskName.contains(taskName);
    }

    private BooleanExpression getWhereParentIdEq(long parentProcessItemId) {
        if (parentProcessItemId == 0) return null;
        return processItem.parentId.eq(parentProcessItemId);
    }

    private BooleanExpression getWhereIdEq(long parentProcessItemId){
        if (parentProcessItemId == 0) return null;
        return processItem.id.eq(parentProcessItemId);
    }

    private BooleanExpression getWhereStatus() {
        return gisung.status.eq(GisungStatus.COMPLETE);
    }

    private BooleanExpression getWhereSubTaskNameCodeContains(String taskName) {
        if (StringUtils.isEmpty(taskName)) return null;
        return subProcessItem.taskName.contains(taskName);
    }

    private BooleanExpression getWhereCateEq(int cateNo, String cate1, String cate2, String cate3, String cate4, String cate5, String cate6) {

        if (cateNo == 1) {
            return processItem.cate2.eq(cate2);
        } else if (cateNo == 2) {
            return processItem.cate1.eq(cate1)
                    .and(processItem.cate2.ne(cate2));
        } else if (cateNo == 3) {
            return processItem.cate1.eq(cate1)
                    .and(processItem.cate2.eq(cate2))
                    .and(processItem.cate3.ne(cate3));
        } else if (cateNo == 4) {
            return processItem.cate1.eq(cate1)
                    .and(processItem.cate2.eq(cate2))
                    .and(processItem.cate3.eq(cate3))
                    .and(processItem.cate4.ne(cate4));
        } else if (cateNo == 5) {
            return processItem.cate1.eq(cate1)
                    .and(processItem.cate2.eq(cate2))
                    .and(processItem.cate3.eq(cate3))
                    .and(processItem.cate4.eq(cate4))
                    .and(processItem.cate5.ne(cate5));
        } else if (cateNo == 6) {
            return processItem.cate1.eq(cate1)
                    .and(processItem.cate2.eq(cate2))
                    .and(processItem.cate3.eq(cate3))
                    .and(processItem.cate4.eq(cate4))
                    .and(processItem.cate5.eq(cate5))
                    .and(processItem.cate6.ne(cate6));
        } else {
            return null;
        }
    }

    private BooleanExpression getWhereUpCodeEq(int cateNo, String upCode) {

        if (cateNo == 1) {
            return processItem.cate2.isNull().or(processItem.cate2.eq(""));
        } else if (cateNo == 2) {
            return processItem.cate1.eq(upCode)
                    .and(processItem.cate3.isNull().or(processItem.cate3.eq("")));
        } else if (cateNo == 3) {
            return processItem.cate2.eq(upCode)
                    .and(processItem.cate4.isNull().or(processItem.cate4.eq("")));
        } else if (cateNo == 4) {
            return processItem.cate3.eq(upCode)
                    .and(processItem.cate5.isNull().or(processItem.cate5.eq("")));
        } else if (cateNo == 5) {
            return processItem.cate4.eq(upCode)
                    .and(processItem.cate6.isNull().or(processItem.cate6.eq("")));
        } else if (cateNo == 6) {
            return processItem.cate5.eq(upCode);
        } else {
            return null;
        }
    }
    private BooleanExpression getWhereSubCateEq(int cateNo) {
        if (cateNo == 1) {
            return subProcessItem.cate1.eq(processItem.cate1);
        } else if (cateNo == 2) {
            return subProcessItem.cate1.eq(processItem.cate1)
                    .and(subProcessItem.cate2.eq(processItem.cate2));
        } else if (cateNo == 3) {
            return subProcessItem.cate1.eq(processItem.cate1)
                    .and(subProcessItem.cate2.eq(processItem.cate2))
                    .and(subProcessItem.cate3.eq(processItem.cate3));
        } else if (cateNo == 4) {
            return subProcessItem.cate1.eq(processItem.cate1)
                    .and(subProcessItem.cate2.eq(processItem.cate2))
                    .and(subProcessItem.cate3.eq(processItem.cate3))
                    .and(subProcessItem.cate4.eq(processItem.cate4));
        } else if (cateNo == 5) {
            return subProcessItem.cate1.eq(processItem.cate1)
                    .and(subProcessItem.cate2.eq(processItem.cate2))
                    .and(subProcessItem.cate3.eq(processItem.cate3))
                    .and(subProcessItem.cate4.eq(processItem.cate4))
                    .and(subProcessItem.cate5.eq(processItem.cate5));
        } else if (cateNo == 6) {
            return subProcessItem.cate1.eq(processItem.cate1)
                    .and(subProcessItem.cate2.eq(processItem.cate2))
                    .and(subProcessItem.cate3.eq(processItem.cate3))
                    .and(subProcessItem.cate4.eq(processItem.cate4))
                    .and(subProcessItem.cate5.eq(processItem.cate5))
                    .and(subProcessItem.cate6.eq(processItem.cate6));
        } else {
            return null;
        }
    }
    private BooleanExpression getWhereCate1Eq(String cate1) {
        if (StringUtils.isEmpty(cate1)) return null;
        return processItem.cate1.eq(cate1);
    }
    private BooleanExpression getWhereCate2Eq(String cate2) {
        if (StringUtils.isEmpty(cate2)) return null;
        return processItem.cate2.eq(cate2);
    }
    private BooleanExpression getWhereCate3Eq(String cate3) {
        if (StringUtils.isEmpty(cate3)) return null;
        return processItem.cate3.eq(cate3);
    }
    private BooleanExpression getWhereCate4Eq(String cate4) {
        if (StringUtils.isEmpty(cate4)) return null;
        return processItem.cate4.eq(cate4);
    }
    private BooleanExpression getWhereCate5Eq(String cate5) {
        if (StringUtils.isEmpty(cate5)) return null;
        return processItem.cate5.eq(cate5);
    }
    private BooleanExpression getWhereCate6Eq(String cate6) {
        if (StringUtils.isEmpty(cate6)) return null;
        return processItem.cate6.eq(cate6);
    }

    private BooleanExpression getWhereGisungIdEq(long gisungId){
        if (gisungId == 0) return null;
        return gisungProcessItemCostDetail.gisung.id.eq(gisungId);
    }

    private BooleanExpression getWhereGisungProcessItemCostDetailCodeEq(String code) {
        if (StringUtils.isEmpty(code)) return null;
        return processItemCostDetail.code.eq(code);
    }

    private BooleanExpression getWhereProcessItemIdNotIn(List<Long> processItemIds){
        if (processItemIds.size() == 0) return null;
        return processItem.id.notIn(processItemIds);
    }

    private BooleanExpression getWherePaidProgressRate() {
        return processItem.paidProgressRate.lt(1.0000);
    }

    private BooleanExpression getWhereProcessItemCostCheck(Boolean isMinCost) {
        if (!isMinCost) return null;
        return processItem.progressRate.goe(100);
    }

    private BooleanExpression getWhereCompareProcessCount() {
        return gisungProcessItemCostDetail.paidProgressCount.add(gisungProcessItemCostDetail.progressCount).lt(gisungProcessItemCostDetail.count);
    }
}
