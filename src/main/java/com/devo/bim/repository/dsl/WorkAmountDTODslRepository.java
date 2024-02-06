package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.JobSheetProcessItemDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.WorkAmountDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.devo.bim.model.vo.SearchWorkAmountVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.devo.bim.model.entity.QBulletin.bulletin;
import static com.devo.bim.model.entity.QGisungWorkCost.gisungWorkCost;
import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QJobSheetProcessItem.jobSheetProcessItem;
import static com.devo.bim.model.entity.QVmJobSheetProcessItemCost.vmJobSheetProcessItemCost;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkAmount.workAmount;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class WorkAmountDTODslRepository {
    private final JPAQueryFactory queryFactory;

    QJobSheet subJobSheet = new QJobSheet("subJobSheet");
    QJobSheetProcessItem subJobProcessItem = new QJobSheetProcessItem("subJobProcessItem");

    public PageDTO<WorkAmountDTO> findWorkAmountDTOs(SearchWorkAmountVO searchWorkAmountVO, Pageable pageable) {
        QueryResults<WorkAmountDTO> results = queryFactory.select(Projections.constructor(WorkAmountDTO.class
                        , workAmount.id
                        , workAmount.projectId
                        , workAmount.year
                        , workAmount.totalAmount
                        , workAmount.prevAmount
                        , workAmount.amount
                        , work.id
                        , workName.name
                        , workAmount.writeEmbedded.writeDate
                        , workAmount.writeEmbedded.writer
                ))
                .from(workAmount)
                .leftJoin(workAmount.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchWorkAmountVO.getLang())
                )
                .where(
                        getWhereProjectIdEnabledEq(searchWorkAmountVO.getProjectId()),
                        getWhereWorkType(searchWorkAmountVO.getSearchWorkId())
                )
                .orderBy(
                        getOrderBySortProp(searchWorkAmountVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                )
                .limit(
                        pageable.getPageSize()
                )
                .fetchResults();
        return new PageDTO<>(results.getResults(), pageable, results.getTotal());
    }

    public List<WorkAmountDTO> findAllWorkAmountListForWork(long projectId, String languageCode, String reportDate, long jobSheetId) {
        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class,
                        workAmount.id
                        , workAmount.projectId
                        , workAmount.year
                        , workAmount.totalAmount
                        , workAmount.prevAmount
                        , workAmount.amount
                        , work.id.as("workId")
                        , work.upId
                        , workName.name
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItem.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(jobSheetProcessItem)
                                        .leftJoin(jobSheetProcessItem.processItem, processItem)
                                        .leftJoin(jobSheetProcessItem.jobSheet, jobSheet)
                                        .where(
                                                processItem.work.eq(work)
                                                , getWhereReportDateBetween2(reportDate.substring(0, 4) + "-01-01", reportDate)
                                                , getWhereStatus(jobSheetId)
                                                , getWhereEnabledEqTrue()
                                        ),
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        .where(
                                                gisungWorkCost.work.id.eq(work.id)
                                                , gisungWorkCost.year.eq(reportDate.substring(0, 4))
                                        ),
                                "paidCost")
                ))
                .from(work)
                .leftJoin(work.workAmounts, workAmount)
                .on(
                    workAmount.year.eq(reportDate.substring(0, 4))
                )
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(languageCode)
                )
                .where(
                        getWhereWorkProjectIdEnabledEq(projectId)
                        , workAmount.year.eq(reportDate.substring(0, 4))
                ).orderBy(
                        work.sortNo.asc()
                ).fetch();
    }

    public List<WorkAmountDTO> findAllWorkAmountListForWork_back(long projectId, String languageCode, String reportDate, long jobSheetId) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class,
                        workAmount.id
                        , workAmount.projectId
                        , workAmount.year
                        , workAmount.totalAmount
                        , workAmount.prevAmount
                        , workAmount.amount
                        , work.id.as("workId")
                        , workName.name
                        , workAmount.writeEmbedded.writeDate
                        , workAmount.writeEmbedded.writer
                        , ExpressionUtils.as(
                                JPAExpressions.select(vmJobSheetProcessItemCost.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(vmJobSheetProcessItemCost)
                                        .where(
                                                vmJobSheetProcessItemCost.workId.eq(workAmount.work.id)
                                                , getWhereReportDateBetween(reportDate.substring(0, 4) + "-01-01", reportDate)
                                        ),
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        .where(
                                                gisungWorkCost.work.id.eq(workAmount.work.id)
                                                , gisungWorkCost.year.eq(reportDate.substring(0, 4))
                                        ),
                                "paidCost")
                ))
                .from(workAmount)
                .join(workAmount.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(languageCode)
                )
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , workAmount.year.eq(reportDate.substring(0, 4))
                ).orderBy(
                        work.sortNo.asc()
                ).fetch();
    }

    public WorkAmountDTO findSumWorkAmountListForYear(long projectId, String reportDate, long jobSheetId, long workUpId) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class
                        , workAmount.totalAmount.sum().as("totalAmount")
                        , workAmount.prevAmount.sum().as("prevAmount")
                        , workAmount.amount.sum().as("amount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItem.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(jobSheetProcessItem)
                                        .leftJoin(jobSheetProcessItem.jobSheet, jobSheet)
                                        .where(
                                                getWhereReportDateBetween2(reportDate.substring(0, 4) + "-01-01", reportDate)
                                                , getWhereStatus(jobSheetId)
                                                , getWhereEnabledEqTrue()
                                        ),
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        .where(
                                                gisungWorkCost.year.eq(reportDate.substring(0, 4))
                                        ),
                                "paidCost")
                ))
                .from(workAmount)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , workAmount.year.eq(reportDate.substring(0, 4))
                        , getWhereWorkAmountWorkUpIdType(workUpId)
                ).fetchOne();
    }

    public WorkAmountDTO findSumWorkAmountListForYear_back(long projectId, String reportDate, long jobSheetId) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class
                                , workAmount.totalAmount.sum().as("totalAmount")
                                , workAmount.prevAmount.sum().as("prevAmount")
                                , workAmount.amount.sum().as("amount")
                                , ExpressionUtils.as(
                                        JPAExpressions.select(vmJobSheetProcessItemCost.todayProgressAmount.sum().longValue().coalesce(0L))
                                                .from(vmJobSheetProcessItemCost)
                                                .where(
                                                        getWhereReportDateBetween(reportDate.substring(0, 4) + "-01-01", reportDate)
                                                ),
                                        "todayAmount")
                                , ExpressionUtils.as(
                                        JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                                .from(gisungWorkCost)
                                                .where(
                                                        gisungWorkCost.year.eq(reportDate.substring(0, 4))
                                                ),
                                        "paidCost")
                        ))
                                .from(workAmount)
                                .where(
                                        getWhereProjectIdEnabledEq(projectId)
                                        , workAmount.year.eq(reportDate.substring(0, 4))
                ).fetchOne();
    }

    public List<WorkAmountDTO> findAllWorkAmountList(long projectId, String languageCode) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class,
                        workAmount.id
                        , workAmount.projectId
                        , workAmount.year
                        , workAmount.totalAmount
                        , workAmount.prevAmount
                        , workAmount.amount
                        , work.id.as("workId")
                        , workName.name
                        , workAmount.writeEmbedded.writeDate
                        , workAmount.writeEmbedded.writer
                        , ExpressionUtils.as(
                                JPAExpressions.select(vmJobSheetProcessItemCost.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(vmJobSheetProcessItemCost)
                                        .where(
                                                vmJobSheetProcessItemCost.workId.eq(workAmount.work.id)
                                        ),
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        .where(
                                                gisungWorkCost.work.id.eq(workAmount.work.id)
                                        ),
                                "paidCost")
                ))
                .from(workAmount)
                .join(workAmount.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(languageCode)
                )
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                ).orderBy(
                        work.sortNo.asc()
                ).fetch();
    }

    public List<WorkAmountDTO> findAllWorkAmountList_back(long projectId, String languageCode) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class,
                        workAmount.id
                        , workAmount.projectId
                        , workAmount.year
                        , workAmount.totalAmount
                        , workAmount.prevAmount
                        , workAmount.amount
                        , work.id.as("workId")
                        , workName.name
                        , workAmount.writeEmbedded.writeDate
                        , workAmount.writeEmbedded.writer
                        , ExpressionUtils.as(
                                JPAExpressions.select(vmJobSheetProcessItemCost.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(vmJobSheetProcessItemCost)
                                        .where(
                                                vmJobSheetProcessItemCost.workId.eq(workAmount.work.id)
                                        ),
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        .where(
                                                gisungWorkCost.work.id.eq(workAmount.work.id)
                                        ),
                                "paidCost")
                ))
                .from(workAmount)
                .join(workAmount.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(languageCode)
                )
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                ).orderBy(
                        work.sortNo.asc()
                ).fetch();
    }

    public WorkAmountDTO findSumWorkAmountList(long projectId) {

        return queryFactory
                .select(Projections.constructor(WorkAmountDTO.class
                        , workAmount.totalAmount.sum().as("totalAmount")
                        , workAmount.prevAmount.sum().as("prevAmount")
                        , workAmount.amount.sum().as("amount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(vmJobSheetProcessItemCost.todayProgressAmount.sum().longValue().coalesce(0L))
                                        .from(vmJobSheetProcessItemCost)
                                        ,
                                "todayAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(gisungWorkCost.paidCost.sum().longValue().coalesce(0L))
                                        .from(gisungWorkCost)
                                        ,
                                "paidCost")
                ))
                .from(workAmount)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                ).fetchOne();
    }

    public List<JobSheetProcessItemDTO> findSumJobSheetProcessItemListForYear(long workId, long jobSheetId, String reportDate) {
        QProcessItem subProcessItem = new QProcessItem("subProcessItem");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDTO.class
                        , processItem.taskName
                        , processItem.taskFullPath
                        , processItem.cost
                        , jobSheetProcessItem.beforeProgressAmount
                        , jobSheetProcessItem.beforeProgressRate
                        , jobSheetProcessItem.todayProgressAmount
                        , jobSheetProcessItem.todayProgressRate
                        , jobSheetProcessItem.afterProgressAmount
                        , jobSheetProcessItem.afterProgressRate
                        , processItem.parentId
                        , ExpressionUtils.as(
                                JPAExpressions.select(subProcessItem.taskFullPath)
                                        .from(subProcessItem)
                                        .where(
                                                processItem.parentId.eq(subProcessItem.id)
                                        ),
                                "parentTaskFullPath")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subJobProcessItem.todayProgressRate.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(subJobProcessItem)
                                        .leftJoin(subJobProcessItem.jobSheet, subJobSheet)
                                        .where(
                                                getWhereReportDate3(reportDate.substring(0, 4), reportDate)
                                                , jobSheetProcessItem.processItem.id.eq(subJobProcessItem.processItem.id)
                                                , getWhereYearStatus()
                                                , getWhereYearEnabledEqTrue()
                                        ),
                                "yearProgressRate")
                ))
                .from(jobSheetProcessItem)
                .join(jobSheetProcessItem.processItem, processItem)
                .where(
                        jobSheetProcessItem.jobSheet.id.eq(jobSheetId)
                        , processItem.work.id.eq(workId)
                ).orderBy(
                        processItem.rowNum.asc()
                ).fetch();
    }

    public List<JobSheetProcessItemDTO> findSumJobSheetProcessItemListForYear_back(long workId, long jobSheetId, String reportDate) {
        QProcessItem subProcessItem = new QProcessItem("subProcessItem");
        QVmJobSheetProcessItemCost subVmJobProcessItemCost = new QVmJobSheetProcessItemCost("subVmJobProcessItemCost");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDTO.class
                        , processItem.taskName
                        , processItem.taskFullPath
                        , processItem.cost
                        , vmJobSheetProcessItemCost.beforeProgressAmount
                        , vmJobSheetProcessItemCost.beforeProgressRate
                        , vmJobSheetProcessItemCost.todayProgressAmount
                        , vmJobSheetProcessItemCost.todayProgressRate
                        , vmJobSheetProcessItemCost.afterProgressAmount
                        , vmJobSheetProcessItemCost.afterProgressRate
                        , processItem.parentId
                        , ExpressionUtils.as(
                                JPAExpressions.select(subProcessItem.taskFullPath)
                                        .from(subProcessItem)
                                        .where(
                                                processItem.parentId.eq(subProcessItem.id)
                                        ),
                                "parentTaskFullPath")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobProcessItemCost.todayProgressRate)
                                        .from(subVmJobProcessItemCost)
                                        .where(
                                                getWhereReportDate2(reportDate.substring(0, 4))
                                                , vmJobSheetProcessItemCost.id.eq(subVmJobProcessItemCost.id)
                                        ),
                                "yearProgressRate")
                ))
                .from(vmJobSheetProcessItemCost)
                .join(vmJobSheetProcessItemCost.processItem, processItem)
                .where(
                        vmJobSheetProcessItemCost.jobSheet.id.eq(jobSheetId)
                        , processItem.work.id.eq(workId)
                ).orderBy(
                        processItem.rowNum.asc()
                ).fetch();
    }

    private BooleanExpression getOnWorkNameLanguageCodeEq(String lang) {
        if (StringUtils.isBlank(lang)) return null;
        return workName.languageCode.equalsIgnoreCase(lang);
    }

    private BooleanExpression getWhereReportDate(String year) {
        if (!Strings.isBlank(year)) {
            return jobSheet.reportDate.year().eq(Integer.parseInt(year));
        }
        return null;
    }

    private BooleanExpression getWhereReportDate2(String year) {
        if (!Strings.isBlank(year)) {
            return vmJobSheetProcessItemCost.reportDate.year().eq(Integer.parseInt(year));
        }
        return null;
    }

    private BooleanExpression getWhereReportDate3(String year, String reportDate) {
        if (!Strings.isBlank(year)) {
            return subJobSheet.reportDate.year().eq(Integer.parseInt(year)).and((subJobSheet.reportDate.before(Utils.parseLocalDateTimeEnd(reportDate))).or(subJobSheet.reportDate.eq(Utils.parseLocalDateTimeEnd(reportDate))));
        }
        return null;
    }

    private BooleanExpression getWhereReportDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return vmJobSheetProcessItemCost.reportDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereReportDateBetween2(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return jobSheet.reportDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereWorkType(long workId) {
        if (workId == 0) return null;
        return work.id.eq(workId);
    }
    private BooleanExpression getWhereWorkAmountWorkUpIdType(long workUpId) {
        if (workUpId == 0) return null;
        return workAmount.work.upId.eq(workUpId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return workAmount.projectId.eq(projectId);
    }

    private BooleanExpression getWhereWorkProjectIdEnabledEq(long projectId) {
        return work.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("workNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.asc()};
        if ("workNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workAmount.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workAmount.writeEmbedded.writeDate.desc()};

        return new OrderSpecifier[]{
                workAmount.writeEmbedded.writeDate.desc()
        };
    }

    private BooleanExpression getWhereStatus(long jobSheetId) {
        if (jobSheetId == 0) return jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
        return jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE)).or(jobSheet.id.eq(jobSheetId));
    }

    private BooleanExpression getWhereEnabledEqTrue() {
        return jobSheet.enabled.eq(true);
    }

    private BooleanExpression getWhereYearStatus() {
        return subJobSheet.status.eq(JobSheetStatus.GOING).or(subJobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getWhereYearEnabledEqTrue() {
        return subJobSheet.enabled.eq(true);
    }
}
