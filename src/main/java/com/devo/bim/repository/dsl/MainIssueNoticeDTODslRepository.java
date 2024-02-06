package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.MainIssueNoticeDTO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.IssueStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCoWorkIssue.coWorkIssue;
import static com.devo.bim.model.entity.QCoWorkIssueJoiner.coWorkIssueJoiner;
import static com.devo.bim.model.entity.QIssue.issue;
import static com.devo.bim.model.entity.QIssueManager.issueManager;
import static com.devo.bim.model.entity.QNotice.notice;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class MainIssueNoticeDTODslRepository {

    private final JPAQueryFactory queryFactory;

    // 이슈
    public List<MainIssueNoticeDTO> findMainIssueByWriterOrIssueManager(String dtoType, long accountId, long projectId, boolean isAdminProject, int maxSize){

        QueryResults<MainIssueNoticeDTO> results = queryFactory
                .select(Projections.constructor(MainIssueNoticeDTO.class, issue))
                .from(issue)
                .where(
                        getWhereWriterOrManagerEq(dtoType, accountId, isAdminProject),
                        getWhereProjectIdEq(dtoType, projectId),
                        getWhereStatusNotEqWrite(dtoType)
                ).orderBy(
                        getOrderBySortProps(dtoType)
                ).limit(maxSize)
                .fetchResults();

        List<MainIssueNoticeDTO> mainIssueNoticeDTOs = results.getResults();

        return mainIssueNoticeDTOs;
    }

    // 협업 이슈
    public List<MainIssueNoticeDTO> findMainCoWorkIssueByWriterOrIssueJoiner(String dtoType, long accountId, long projectId, boolean isAdminProject, int maxSize){

        QueryResults<MainIssueNoticeDTO> results = queryFactory
                .select(Projections.constructor(MainIssueNoticeDTO.class, coWorkIssue, coWorkIssue.coWork.projectId))
                .from(coWorkIssue)
                .where(
                        getWhereWriterOrManagerEq(dtoType, accountId, isAdminProject),
                        getWhereProjectIdEq(dtoType, projectId),
                        getWhereStatusNotEqWrite(dtoType)
                ).orderBy(
                        getOrderBySortProps(dtoType)
                ).limit(maxSize)
                .fetchResults();

        List<MainIssueNoticeDTO> mainIssueNoticeDTOs = results.getResults();

        return mainIssueNoticeDTOs;
    }

    // 공지
    public List<MainIssueNoticeDTO> findMainNoticeByWriterOrSameWork(String dtoType, long accountId, long projectId, boolean isAdminProject, int maxSize){
        List<Long> accountWorkIds = queryFactory
                .select(work.id)
                .from(account)
                .join(account.works, work)
                .where(
                        account.id.eq(accountId)
                ).fetch();

        QueryResults<MainIssueNoticeDTO> results = queryFactory
                .select(Projections.constructor(MainIssueNoticeDTO.class, notice)).distinct()
                .from(notice)
                .join(notice.works, work)
                .where(
                        getWhereProjectIdEq(dtoType, projectId)
                        , getWhereStatusNotEqWrite(dtoType)
                        , getWhereWriteOrManagerEqForNotice(accountId, isAdminProject, accountWorkIds)
                ).orderBy(
                        getOrderBySortProps(dtoType)
                ).limit(maxSize)
                .fetchResults();

        List<MainIssueNoticeDTO> mainIssueNoticeDTOs = results.getResults();

        return mainIssueNoticeDTOs;
    }

    private BooleanExpression getWhereWriterOrManagerEq(String dtoType, long accountId, boolean isAdminProject){
        if("ISSUE".equalsIgnoreCase(dtoType)) return issue.writeEmbedded.writer.id.eq(accountId).or(getSubQueryForIssueManagers(accountId));
        if("CO_WORK_ISSUE".equalsIgnoreCase(dtoType)) return coWorkIssue.writeEmbedded.writer.id.eq(accountId).or(getSubQueryForCoWorkIssueJoiner(accountId));
        return null;
    }

    private BooleanExpression getWhereWriteOrManagerEqForNotice(long accountId, boolean isAdminProject, List<Long> accountWorkIds){
        if(!isAdminProject)
            return notice.writeEmbedded.writer.id.eq(accountId)
                    .or (
                            work.id.in(accountWorkIds)
                    );

        return null;
    }

    private BooleanExpression getWhereProjectIdEq(String dtoType, long projectId) {
        if("ISSUE".equalsIgnoreCase(dtoType)) return issue.projectId.eq(projectId);
        if("CO_WORK_ISSUE".equalsIgnoreCase(dtoType)) return coWorkIssue.coWork.projectId.eq(projectId);
        return notice.projectId.eq(projectId);
    }

    private BooleanExpression getWhereStatusNotEqWrite(String dtoType){
        if("ISSUE".equalsIgnoreCase(dtoType)) return issue.status.notIn(IssueStatus.WRITE, IssueStatus.COMPLETE);
        if("CO_WORK_ISSUE".equalsIgnoreCase(dtoType)) return coWorkIssue.status.notIn(IssueStatus.WRITE, IssueStatus.COMPLETE);
        return notice.enabled.eq(true);
    }

    private BooleanExpression getSubQueryForIssueManagers(long accountId) {
        return issue.id.in (
                JPAExpressions
                        .select(issueManager.issue.id)
                        .from(issueManager)
                        .where(issueManager.manager.id.eq(accountId))
        );
    }

    private BooleanExpression getSubQueryForCoWorkIssueJoiner(long accountId) {
        return coWorkIssue.id.in (
                JPAExpressions
                        .select(coWorkIssueJoiner.coWorkIssue.id)
                        .from(coWorkIssueJoiner)
                        .where(coWorkIssueJoiner.joiner.id.eq(accountId))
        );
    }

    private JPQLQuery<Work> getSubQueryForWorks(long accountId) {
        return JPAExpressions
                .select(work)
                .from(account)
                .innerJoin(account.works, work)
                .where(
                        account.id.eq(accountId)
                );
    }

    @NotNull
    private OrderSpecifier[] getOrderBySortProps(String dtoType) {
        if("ISSUE".equalsIgnoreCase(dtoType)) return getIssueOrderBy();
        if("CO_WORK_ISSUE".equalsIgnoreCase(dtoType)) return getCoWorkIssueOrderBy();
        return getNoticeOrderBy();
    }

    @NotNull
    private OrderSpecifier[] getIssueOrderBy() {
        return new OrderSpecifier[]{
                    issue.status.asc()
                    , issue.writeEmbedded.writeDate.desc()
                    , issue.title.asc()
                    , issue.requestDate.desc()
                    , issue.writeEmbedded.writer.userName.asc()
                    , issue.priority.asc()
                    , issue.id.desc()
            };
    }

    @NotNull
    private OrderSpecifier[] getCoWorkIssueOrderBy() {
        return new OrderSpecifier[]{
                coWorkIssue.status.asc()
                , coWorkIssue.writeEmbedded.writeDate.desc()
                , coWorkIssue.title.asc()
                , coWorkIssue.requestDate.desc()
                , coWorkIssue.writeEmbedded.writer.userName.asc()
                , coWorkIssue.priority.asc()
                , coWorkIssue.id.desc()
        };
    }

    @NotNull
    private OrderSpecifier[] getNoticeOrderBy(){
        return new OrderSpecifier[]{
                notice.writeEmbedded.writeDate.desc()
                , notice.title.asc()
                , notice.startDate.desc()
                , notice.id.desc()
        };
    }
}
