package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.entity.QProcessItem;
import com.devo.bim.model.entity.QVmJobSheetProcessItemCost;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.devo.bim.model.enumulator.ProcessValidateResult;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QJobSheetProcessItem.jobSheetProcessItem;
import static com.devo.bim.model.entity.QJobSheetProcessItemDevice.jobSheetProcessItemDevice;
import static com.devo.bim.model.entity.QJobSheetProcessItemWorker.jobSheetProcessItemWorker;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QSelectProgressConfig.selectProgressConfig;
import static com.devo.bim.model.entity.QVmJobSheetProcessItemCost.vmJobSheetProcessItemCost;

@Repository
@RequiredArgsConstructor
public class JobSheetProcessItemDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<JobSheetProcessItemDTO> findAllByProcessId(long processItemId, long jobSheetId, long userId){
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDTO.class, jobSheetProcessItem.id, jobSheetProcessItem.jobSheet.reportDate))
                .from(jobSheetProcessItem)
                .join(jobSheetProcessItem.jobSheet, jobSheet)
                .where(
                        getWhereProcessItemIdEnabledEq(processItemId),
                        getWhereWriterIdEnabledEq(userId),
                        getWhereJobSheetIdEnabledNotEq(jobSheetId),
                        getWhereStatus(),
                        getWhereEnabled()
                ).orderBy(
                        jobSheetProcessItem.jobSheet.id.desc()
                ).fetch();
    }

    public List<JobSheetProcessItemWorkerDTO> findAllProjectListForWorker(long jobSheetId, LocalDateTime reportDate) {

        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemWorkerDTO.class
                        , selectProgressConfig.name.as("name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemWorker.workerAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemWorker)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemWorker.workerName)
                                                , jobSheetProcessItemWorker.jobSheet.id.eq(jobSheetId)
                                        ),
                                "workerAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemWorker.workerAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemWorker)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemWorker.workerName)
                                                , jobSheetProcessItemWorker.jobSheet.enabled.eq(true)
                                                , getWorkerWhereReportDate(reportDate)
                                                , getWorkerWhereStatus()
                                        ),
                                "beforeWorkerAmount")
                ))
                .from(selectProgressConfig)
                .where(
                        selectProgressConfig.ctype.eq("worker")
                ).orderBy(
                        selectProgressConfig.sorder.asc()
                ).fetch();
    }

    public List<JobSheetProcessItemDeviceDTO> findAllProjectListForDevice(long jobSheetId, LocalDateTime reportDate) {
        /**
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDeviceDTO.class
                        , selectProgressConfig.name.as("name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemDevice.deviceAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemDevice)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemDevice.deviceName)
                                                , jobSheetProcessItemDevice.jobSheet.id.eq(jobSheetId)
                                        ),
                                "deviceAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemDevice.beforeDeviceAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemDevice)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemDevice.deviceName)
                                                , jobSheetProcessItemDevice.jobSheet.id.eq(jobSheetId)
                                        ),
                                "beforeDeviceAmount")
                ))
                .from(selectProgressConfig)
                .where(
                        selectProgressConfig.ctype.eq("device")
                ).orderBy(
                        selectProgressConfig.sorder.asc()
                ).fetch();
         **/
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDeviceDTO.class
                        , selectProgressConfig.name.as("name")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemDevice.deviceAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemDevice)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemDevice.deviceName)
                                                , jobSheetProcessItemDevice.jobSheet.id.eq(jobSheetId)
                                        ),
                                "deviceAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetProcessItemDevice.deviceAmount.sum().coalesce(BigDecimal.valueOf(0)))
                                        .from(jobSheetProcessItemDevice)
                                        .where(
                                                selectProgressConfig.name.eq(jobSheetProcessItemDevice.deviceName)
                                                , jobSheetProcessItemDevice.jobSheet.enabled.eq(true)
                                                , getDeviceWhereReportDate(reportDate)
                                                , getDeviceWhereStatus()
                                        ),
                                "beforeDeviceAmount")
                ))
                .from(selectProgressConfig)
                .where(
                        selectProgressConfig.ctype.eq("device")
                ).orderBy(
                        selectProgressConfig.sorder.asc()
                ).fetch();
    }

    public List<VmJobSheetProcessItemCostDTO> findJobSheetProcessItemListIdsByReportDate(long projectId, long processId, String startDate, String endDate) {
        QProcessItem subProcessItem = new QProcessItem("subProcessItem");
        QVmJobSheetProcessItemCost subVmJobSheetProcessItemCost = new QVmJobSheetProcessItemCost("subVmJobSheetProcessItemCost");
        return queryFactory
                .selectDistinct(Projections.constructor(VmJobSheetProcessItemCostDTO.class
                        , vmJobSheetProcessItemCost.phasingCode
                        , processItem.taskName
                        , processItem.taskFullPath
                        , processItem.rowNum
                        , processItem.parentRowNum
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.beforeProgressAmount.max())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode),
                                                subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id),
                                                subVmJobSheetProcessItemCost.jobSheet.id.eq(
                                                        JPAExpressions.select(subVmJobSheetProcessItemCost.jobSheet.id.max())
                                                                .from(subVmJobSheetProcessItemCost)
                                                                .where(
                                                                        subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                                        , subVmJobSheetProcessItemCost.reportDate.between(
                                                                                Utils.parseLocalDateTimeStart(startDate),
                                                                                Utils.parseLocalDateTimeEnd(endDate)
                                                                        )
                                                                )
                                                )
                                        ),
                                "beforeProgressAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.todayProgressAmount.sum())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                , subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id)
                                                , subVmJobSheetProcessItemCost.reportDate.between(
                                                        Utils.parseLocalDateTimeStart(startDate),
                                                        Utils.parseLocalDateTimeEnd(endDate)
                                                )
                                        ),
                                "todayProgressAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.afterProgressAmount.max())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode),
                                                subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id),
                                                subVmJobSheetProcessItemCost.jobSheet.id.eq(
                                                        JPAExpressions.select(subVmJobSheetProcessItemCost.jobSheet.id.max())
                                                                .from(subVmJobSheetProcessItemCost)
                                                                .where(
                                                                        subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                                        , subVmJobSheetProcessItemCost.reportDate.between(
                                                                                Utils.parseLocalDateTimeStart(startDate),
                                                                                Utils.parseLocalDateTimeEnd(endDate)
                                                                        )
                                                                )
                                                )
                                        ),
                                "afterProgressAmount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.beforeProgressRate.max())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode),
                                                subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id),
                                                subVmJobSheetProcessItemCost.jobSheet.id.eq(
                                                        JPAExpressions.select(subVmJobSheetProcessItemCost.jobSheet.id.max())
                                                                .from(subVmJobSheetProcessItemCost)
                                                                .where(
                                                                        subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                                        , subVmJobSheetProcessItemCost.reportDate.between(
                                                                                Utils.parseLocalDateTimeStart(startDate),
                                                                                Utils.parseLocalDateTimeEnd(endDate)
                                                                        )
                                                                )
                                                )
                                        ),
                                "beforeProgressRate")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.todayProgressRate.sum())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                , subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id)
                                                , subVmJobSheetProcessItemCost.reportDate.between(
                                                        Utils.parseLocalDateTimeStart(startDate),
                                                        Utils.parseLocalDateTimeEnd(endDate)
                                                )
                                        ),
                                "todayProgressRate")
                        , ExpressionUtils.as(
                                JPAExpressions.select(subVmJobSheetProcessItemCost.afterProgressRate.max())
                                        .from(subVmJobSheetProcessItemCost)
                                        .where(
                                                subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode),
                                                subVmJobSheetProcessItemCost.processItem.id.eq(vmJobSheetProcessItemCost.processItem.id),
                                                subVmJobSheetProcessItemCost.jobSheet.id.eq(
                                                        JPAExpressions.select(subVmJobSheetProcessItemCost.jobSheet.id.max())
                                                                .from(subVmJobSheetProcessItemCost)
                                                                .where(
                                                                        subVmJobSheetProcessItemCost.phasingCode.eq(vmJobSheetProcessItemCost.phasingCode)
                                                                        , subVmJobSheetProcessItemCost.reportDate.between(
                                                                                Utils.parseLocalDateTimeStart(startDate),
                                                                                Utils.parseLocalDateTimeEnd(endDate)
                                                                        )
                                                                )
                                                )
                                        ),
                                "afterProgressRate")
                        , processItem.complexUnitPrice
                        , processItem.cost
                        , processItem.paidCost
                        , processItem.paidProgressRate
                        , processItem.parentId
                        , ExpressionUtils.as(
                                JPAExpressions.select(subProcessItem.taskFullPath)
                                        .from(subProcessItem)
                                        .where(
                                                processItem.parentId.eq(subProcessItem.id)
                                        ),
                                "parentTaskFullPath")
                        , processItem.work.id
                        , processItem.id))
                .from(vmJobSheetProcessItemCost)
                .join(vmJobSheetProcessItemCost.processItem, processItem)
                .where(
                        getWhereJobSheetCostProjectIdEnabledEq(projectId)
                        , getWhereJobSheetCostReportDateBetween(startDate, endDate)
                        , getWhereProcessIdEnabledEq(processId)
                        , getWherePhasingCodeIsNotNull()
                        , getWherePhasingCodeIsNotSpace()
                        , getWherePaidProgressRateLt()
                ).orderBy(
                        processItem.rowNum.asc(), processItem.parentRowNum.asc()
                ).fetch();
    }

    public VmJobSheetProcessItemCostDTO findJobSheetProcessItemListIdByPhasingCode(long projectId, String phasingCode, long processItemId) {
        QProcessItem subProcessItem = new QProcessItem("subProcessItem");
        return queryFactory
                .select(Projections.constructor(VmJobSheetProcessItemCostDTO.class
                        , vmJobSheetProcessItemCost.phasingCode
                        , processItem.taskName
                        , processItem.taskFullPath
                        , processItem.rowNum
                        , processItem.parentRowNum
                        , vmJobSheetProcessItemCost.beforeProgressAmount
                        , vmJobSheetProcessItemCost.todayProgressAmount
                        , vmJobSheetProcessItemCost.afterProgressAmount
                        , vmJobSheetProcessItemCost.beforeProgressRate
                        , vmJobSheetProcessItemCost.todayProgressRate
                        , vmJobSheetProcessItemCost.afterProgressRate
                        , processItem.complexUnitPrice
                        , processItem.cost
                        , processItem.paidCost
                        , processItem.paidProgressRate
                        , processItem.parentId
                        , ExpressionUtils.as(
                                JPAExpressions.select(subProcessItem.taskFullPath)
                                        .from(subProcessItem)
                                        .where(
                                                processItem.parentId.eq(subProcessItem.id)
                                        ),
                                "parentTaskFullPath")
                        , processItem.work.id
                        , processItem.id))
                .from(vmJobSheetProcessItemCost)
                .join(vmJobSheetProcessItemCost.processItem, processItem)
                .where(
                        getWhereJobSheetCostProjectIdEnabledEq(projectId)
                        , getWhereJobSheetCostProcessItemIdEnabledEq(processItemId)
                        , getWherePhasingCodeEnabledEq(phasingCode)
                        //, getWherePaidProgressRateLt()
                ).orderBy(
                        vmJobSheetProcessItemCost.jobSheet.id.desc()
                ).limit(1)
                .fetchOne();
    }

    public List<JobSheetProcessItemDTO> getModelExchangeIdsByJobSheetProcessItem(String startDate, String endDate) {
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDTO.class, processItem.exchangeIds, processItem.phasingCode))

                .from(jobSheet)
                .join(jobSheet.jobSheetProcessItems, jobSheetProcessItem)
                .join (jobSheetProcessItem.processItem, processItem)
                .where(
                        jobSheet.reportDate.between(
                                Utils.parseLocalDateTimeStart(startDate),
                                Utils.parseLocalDateTimeEnd(endDate))
                        , jobSheet.status.in(JobSheetStatus.GOING, JobSheetStatus.COMPLETE)
                        , jobSheetProcessItem.phasingCode.eq(processItem.phasingCode)

                ).orderBy(
                        jobSheetProcessItem.id.desc()
                ).fetch();
    }

    public List<JobSheetProcessItemDeviceDTO> getJobSheetProcessItemDeviceDate(String reportDate, int limit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDeviceDTO.class
                        , jobSheetProcessItemDevice.deviceName
                        , jobSheetProcessItemDevice.deviceAmount
                        , jobSheetProcessItemDevice.beforeDeviceAmount
                ))
                .from(jobSheetProcessItemDevice)
                .leftJoin(jobSheetProcessItemDevice.jobSheet, jobSheet)
                .where(
                        jobSheet.status.in(JobSheetStatus.GOING, JobSheetStatus.COMPLETE)
                        , jobSheet.reportDate.eq(LocalDateTime.parse(reportDate + " 00:00:00", formatter))
                ).orderBy(
                        jobSheetProcessItemDevice.deviceAmount.desc()
                ).limit(limit)
                .fetch();
    }

    public List<JobSheetProcessItemWorkerDTO> getJobSheetProcessItemWorkerDate(String reportDate, int limit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemWorkerDTO.class
                        , jobSheetProcessItemWorker.workerName
                        , jobSheetProcessItemWorker.workerAmount
                        , jobSheetProcessItemWorker.beforeWorkerAmount
                ))
                .from(jobSheetProcessItemWorker)
                .leftJoin(jobSheetProcessItemWorker.jobSheet, jobSheet)
                .where(
                        jobSheet.status.in(JobSheetStatus.GOING, JobSheetStatus.COMPLETE)
                        , jobSheet.reportDate.eq(LocalDateTime.parse(reportDate + " 00:00:00", formatter))
                ).orderBy(
                        jobSheetProcessItemWorker.workerAmount.desc()
                ).limit(limit)
                .fetch();
    }

    public List<JobSheetProcessItemDeviceDTO> getJobSheetProcessItemDeviceDateList(String reportDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemDeviceDTO.class
                        , jobSheetProcessItemDevice.deviceName
                        , jobSheetProcessItemDevice.deviceAmount
                        , jobSheetProcessItemDevice.beforeDeviceAmount
                ))
                .from(jobSheetProcessItemDevice)
                .leftJoin(jobSheetProcessItemDevice.jobSheet, jobSheet)
                .where(
                        jobSheet.status.in(JobSheetStatus.GOING, JobSheetStatus.COMPLETE)
                        , jobSheet.reportDate.eq(LocalDateTime.parse(reportDate + " 00:00:00", formatter))
                ).orderBy(
                        jobSheetProcessItemDevice.deviceAmount.desc()
                ).fetch();
    }

    public List<JobSheetProcessItemWorkerDTO> getJobSheetProcessItemWorkerDateList(String reportDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return queryFactory
                .select(Projections.constructor(JobSheetProcessItemWorkerDTO.class
                        , jobSheetProcessItemWorker.workerName
                        , jobSheetProcessItemWorker.workerAmount
                        , jobSheetProcessItemWorker.beforeWorkerAmount
                ))
                .from(jobSheetProcessItemWorker)
                .leftJoin(jobSheetProcessItemWorker.jobSheet, jobSheet)
                .where(
                        jobSheet.status.in(JobSheetStatus.GOING, JobSheetStatus.COMPLETE)
                        , jobSheet.reportDate.eq(LocalDateTime.parse(reportDate + " 00:00:00", formatter))
                ).orderBy(
                        jobSheetProcessItemWorker.workerAmount.desc()
                ).fetch();
    }

    private BooleanExpression getWhereProcessItemIdEnabledEq(long processItemId) {
        return jobSheetProcessItem.processItem.id.eq(processItemId);
    }

    private BooleanExpression getWorkerWhereReportDate(LocalDateTime reportDate) {
        if (reportDate != null) {
            return jobSheetProcessItemWorker.jobSheet.reportDate.loe(reportDate);
        }
        return null;
    }

    private BooleanExpression getDeviceWhereReportDate(LocalDateTime reportDate) {
        if (reportDate != null) {
            return jobSheetProcessItemDevice.jobSheet.reportDate.loe(reportDate);
        }
        return null;
    }

    private BooleanExpression getWhereWriterIdEnabledEq(long userId) {
        if (userId == 0) return null;
        return jobSheet.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereJobSheetIdEnabledNotEq(long jobSheetId) {
        if (jobSheetId > 0) {
            return jobSheetProcessItem.jobSheet.id.ne(jobSheetId);
        } else {
            return jobSheetProcessItem.id.isNotNull();
        }
    }

    private BooleanExpression getWhereStatus() {
        return jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getWorkerWhereStatus() {
        return jobSheetProcessItemWorker.jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getDeviceWhereStatus() {
        return jobSheetProcessItemDevice.jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getWhereEnabled() {
        return jobSheet.enabled.eq(true);
    }

    private BooleanExpression getWhereJobSheetCostProjectIdEnabledEq(long projectId) {
        return vmJobSheetProcessItemCost.projectId.eq(projectId);
    }
    private BooleanExpression getWhereJobSheetCostProcessItemIdEnabledEq(long processItemId) {
        return processItem.id.eq(processItemId);
    }

    private BooleanExpression getWhereJobSheetCostReportDateBetween(String startDateString, String endDateString) {
        if(!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return vmJobSheetProcessItemCost.reportDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWherePhasingCodeIsNotNull() {
        return processItem.phasingCode.isNotNull();
    }

    private BooleanExpression getWherePhasingCodeIsNotSpace() {
        return processItem.phasingCode.notEqualsIgnoreCase("");
    }

    private BooleanExpression getWhereProcessIdEnabledEq(long processId) {
        return processItem.processInfo.id.eq(processId);
    }

    private BooleanExpression getWherePhasingCodeEnabledEq(String phasingCode) {
        return vmJobSheetProcessItemCost.phasingCode.eq(phasingCode);
    }

    private BooleanExpression getWherePaidProgressRateLt() {
        return processItem.paidProgressRate.lt(1.00);
    }
}
