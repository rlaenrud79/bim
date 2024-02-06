package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.dto.UserPopUpNoticeDTO;
import com.devo.bim.model.entity.Alert;
import com.devo.bim.model.entity.QAccount;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.devo.bim.model.entity.QAccount.*;
import static com.devo.bim.model.entity.QAlert.alert;
import static com.devo.bim.model.entity.QJobSheet.jobSheet;

@Repository
@RequiredArgsConstructor
public class AlertDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<Alert> findAlertByReceiverId(long projectId, long accountId, AlertType alertType, long topCount) {
        return queryFactory
                .select(alert)
                .from(alert)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereReceiverIdEq(accountId),
                        getWhereAlertTypeHeader(alertType),
                        getWhereIsReadEq(false),
                        getWhereEnabledEq(true)
                ).orderBy(
                        getOrderByIdDESC()
                ).limit(topCount)
                .fetch();
    }

    public List<Alert> findAlertByReceiverId(long projectId, long accountId, AlertType alertType) {
        return queryFactory
                .select(alert)
                .from(alert)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereReceiverIdEq(accountId),
                        getWhereAlertType(alertType),
                        getWhereIsReadEq(false),
                        getWhereEnabledEq(true)
                ).orderBy(
                        getOrderByIdDESC()
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return alert.projectId.eq(projectId);
    }

    private BooleanExpression getWhereReceiverIdEq(long accountId) {
        return alert.receiverId.eq(accountId);
    }

    private BooleanExpression getWhereAlertTypeHeader(AlertType alertType) {
        if(alertType == AlertType.CHATTING) return getWhereAlertTypeEq(AlertType.CHATTING);
        return alert.type.notIn(AlertType.CHATTING);
    }

    private BooleanExpression getWhereAlertType(AlertType alertType) {
        if(alertType == AlertType.CHATTING) return getWhereAlertTypeEq(AlertType.CHATTING);
        if(alertType == AlertType.NOTICE) return getWhereAlertTypeEq(AlertType.NOTICE);
        if(alertType == AlertType.ISSUE) return getWhereAlertTypeEq(AlertType.ISSUE);
        if(alertType == AlertType.JOB_SHEET) return getWhereAlertTypeEq(AlertType.JOB_SHEET);
        if(alertType == AlertType.CO_WORK) return getWhereAlertTypeEq(AlertType.CO_WORK);
        if(alertType == AlertType.CO_WORK_ISSUE) return getWhereAlertTypeEq(AlertType.CO_WORK_ISSUE);
        if(alertType == AlertType.PROCESS_ITEM) return getWhereAlertTypeEq(AlertType.PROCESS_ITEM);
        return null;
    }

    private BooleanExpression getWhereAlertTypeForNotice(){
        return getWhereAlertTypeEq(AlertType.NOTICE);
    }

    private BooleanExpression getWhereIsPopupEqTrue(){
        return alert.isPopup.eq(true);
    }

    private BooleanExpression getWhereNoPopupEqFalse(){
        return alert.noPopup.eq(false);
    }

    private BooleanExpression getWhereStartDateGOENow(){
        return alert.startDate.before(LocalDateTime.now());
    }

    private BooleanExpression getWhereEndDateLOENow(){
        return alert.endDate.after(LocalDateTime.now());
    }

    private BooleanExpression getWhereIsReadEq(boolean value) {
        return alert.isRead.eq(value);
    }

    private BooleanExpression getWhereEnabledEq(boolean value){
        return alert.enabled.eq(value);
    }

    private OrderSpecifier<Long> getOrderByIdDESC() {
        return alert.id.desc();
    }

    public List<AccountDTO> findConfirmUserByRefIdAndType(long projectId, long refId, AlertType alertType){
        return queryFactory
                .select(Projections.constructor(AccountDTO.class, account))
                .from(account)
                .where(
                        account.id.in(
                                getSubQueryForAlertReceiverId(projectId, refId, alertType)
                        )
                ).fetch();
    }

    private JPQLQuery<Long> getSubQueryForAlertReceiverId(long projectId, long refId, AlertType alertType) {
        return JPAExpressions
                .select(alert.receiverId)
                .from(alert)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRefIdEq(refId),
                        getWhereAlertTypeEq(alertType),
                        getWhereIsReadEq(true),
                        getWhereEnabledEq(true)
                );
    }

    private BooleanExpression getWhereAlertTypeEq(AlertType alertType) {
        return alert.type.eq(alertType);
    }

    private BooleanExpression getWhereRefIdEq(long refId) {
        return alert.refId.eq(refId);
    }

    public Alert findByProjectIdAndReceiverIdAndRefIdAndAlertType(long projectId, long receiverId, long refId, AlertType alertType){
        return queryFactory
                .select(alert)
                .from(alert)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereReceiverIdEq(receiverId),
                        getWhereRefIdEq(refId),
                        getWhereAlertTypeEq(alertType),
                        getWhereEnabledEq(true)
                )
                .orderBy(
                    getOrderByIdDESC()
                ).fetchFirst();
    }

    public List<Alert> findAlertNoticesPopupByReceiverId(long projectId, long accountId) {
        return queryFactory
                .select(alert)
                .from(alert)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereReceiverIdEq(accountId),
                        getWhereAlertTypeForNotice(),
                        getWhereEnabledEq(true),
                        getWhereIsPopupEqTrue(),
                        getWhereNoPopupEqFalse(),
                        getWhereStartDateGOENow(),
                        getWhereEndDateLOENow()
                ).orderBy(
                        getOrderByIdDESC()
                ).fetch();
    }
}
