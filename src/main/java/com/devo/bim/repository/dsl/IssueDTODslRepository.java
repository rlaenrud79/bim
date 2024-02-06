package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.IssueDTO;
import com.devo.bim.model.enumulator.IssueStatus;
import com.devo.bim.model.vo.SearchIssueVO;
import com.devo.bim.model.entity.IssueManager;
import com.devo.bim.model.dto.PageDTO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.devo.bim.model.entity.QIssue.issue;
import static com.devo.bim.model.entity.QIssueManager.issueManager;
import static com.devo.bim.model.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class IssueDTODslRepository {

    private final JPAQueryFactory queryFactory;


    // 일반 리스트
    public List<IssueDTO> findIssueDTOs(SearchIssueVO searchIssueVO, long accountId, boolean isAdminProject){

        QueryResults<IssueDTO> results = queryFactory
                .select(Projections.constructor(IssueDTO.class, issue))
                .from(issue)
                .where(
                        getWhereIssueWriterOrManagerEq(isAdminProject, accountId),
                        getWhereIssueProjectIdEq(searchIssueVO.getProjectId()),
                        getWhereIssueSearchTextLike(searchIssueVO.getSearchType(), searchIssueVO.getSearchText()),
                        getWhereIssueSearchDateBetween(searchIssueVO),
                        getWhereIssueStatusNotEqWrite(accountId),
                        getWhereWriterName(searchIssueVO.getWriterId())
                ).orderBy(
                        getOrderBySortProps(searchIssueVO.getSortProp())
                ).fetchResults();

        List<IssueDTO> issueDTOS = mappingIssueDTOsIssueManager(results);

        return issueDTOS;
    }

    // 페이징
    public PageDTO<IssueDTO> findIssueDTOs(SearchIssueVO searchIssueVO, long accountId, boolean isAdminProject, Pageable pageable){

        QueryResults<IssueDTO> results = queryFactory
                .select(Projections.constructor(IssueDTO.class, issue))
                .from(issue)
                .where(
                        getWhereIssueWriterOrManagerEq(isAdminProject, accountId),
                        getWhereIssueProjectIdEq(searchIssueVO.getProjectId()),
                        getWhereIssueSearchTextLike(searchIssueVO.getSearchType(), searchIssueVO.getSearchText()),
                        getWhereIssueSearchDateBetween(searchIssueVO),
                        getWhereIssueStatusNotEqWrite(accountId),
                        getWhereWriterName(searchIssueVO.getWriterId())
                ).orderBy(
                        getOrderBySortProps(searchIssueVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        List<IssueDTO> content = mappingIssueDTOsIssueManager(results);

        long total = results.getTotal();
        return new PageDTO<>(content, pageable, total);
    }

    private BooleanExpression getWhereWriterName(long writerId){
        if(writerId != 0) return issue.writeEmbedded.writer.id.eq(writerId);
        return null;
    }

    @NotNull
    private List<IssueDTO> mappingIssueDTOsIssueManager(QueryResults<IssueDTO> results) {
        List<IssueDTO> issueDTOS = results.getResults();
        List<IssueManager> issueManagers = getIssueManagers(convertIssueIds(issueDTOS));

        issueDTOS.forEach(t -> {
            List<IssueManager> tmpIssueManagers = getMatchedIssueManagers(issueManagers, t);
            t.addIssueManagers(tmpIssueManagers);
        });
        return issueDTOS;
    }

    @NotNull
    private List<IssueManager> getMatchedIssueManagers(List<IssueManager> issueManagers, IssueDTO t) {
        return issueManagers.stream()
                .filter(s -> s.getIssue().getId() == t.getId())
                .collect(Collectors.toList());
    }

    private List<Long> convertIssueIds(List<IssueDTO> content){
        return content.stream().map(o -> o.getId()).collect(Collectors.toList());
    }

    private List<IssueManager> getIssueManagers(List<Long> issueIds){
        return   queryFactory
                .selectFrom(issueManager)
                .where(
                        issueManager.issue.id.in(issueIds)
                ).fetch();
    }

    private BooleanExpression getWhereIssueStatusNotEqWrite(long accountId){
        return issue.writeEmbedded.writer.id.eq(accountId).or(issue.writeEmbedded.writer.id.notIn(accountId).and(issue.status.notIn(IssueStatus.WRITE)));
    }

    private BooleanExpression getWhereIssueWriterOrManagerEq(boolean isAdminProject, long accountId){
        if(!isAdminProject) return issue.writeEmbedded.writer.id.eq(accountId)
                .or(
                        getSubQueryForIssueManagers(accountId)
                );
        return null;
    }

    private BooleanExpression getSubQueryForIssueManagers(long accountId) {
        return issue.id.in (
                JPAExpressions
                        .select(issueManager.issue.id)
                        .from(issueManager)
                        .where(issueManager.manager.id.eq(accountId))
                );
    }

    private BooleanExpression getWhereIssueProjectIdEq(long projectId) {
        return issue.projectId.eq(projectId);
    }

    private BooleanExpression getWhereIssueSearchTextLike(String searchType, String searchText) {
        if(!StringUtils.isEmpty(searchType) && "TITLE".equalsIgnoreCase(searchType))
            return issue.title.containsIgnoreCase(searchText);
        if(!StringUtils.isEmpty(searchType) && "CONTENTS".equalsIgnoreCase(searchType))
            return issue.contents.containsIgnoreCase(searchText);
        if(!StringUtils.isEmpty(searchType) && "TITLE_CONTENTS".equalsIgnoreCase(searchType))
            return issue.title.containsIgnoreCase(searchText).or(issue.contents.containsIgnoreCase(searchText));
        return null;
    }

    @NotNull
    private BooleanExpression getWhereIssueSearchDateBetween(SearchIssueVO searchIssueVO) {
        if("WRITE_DATE".equalsIgnoreCase(searchIssueVO.getSearchDateType())
                && searchIssueVO.getSearchDateFrom() != null
                && searchIssueVO.getSearchDateEnd() != null)
            return issue.writeEmbedded.writeDate.between(searchIssueVO.getSearchDateFrom().atStartOfDay(), searchIssueVO.getSearchDateEnd().atTime(23, 59, 59));
        if("REQUEST_DATE".equalsIgnoreCase(searchIssueVO.getSearchDateType())
                && searchIssueVO.getSearchDateFrom() != null
                && searchIssueVO.getSearchDateEnd() != null)
            return issue.requestDate.between(searchIssueVO.getSearchDateFrom().atStartOfDay(), searchIssueVO.getSearchDateEnd().atTime(23, 59, 59));
        return null;
    }

    @NotNull
    private OrderSpecifier[] getOrderBySortProps(String sortProperties) {
        if("titleASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.title.asc() , issue.id.desc() };
        if("titleDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.title.desc(), issue.id.desc() };
        if("writerNameASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.writeEmbedded.writer.userName.asc() , issue.id.desc() };
        if("writerNameDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.writeEmbedded.writer.userName.desc() , issue.id.desc() };
        if("priorityASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.priority.asc() , issue.id.desc() };
        if("priorityDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.priority.desc() , issue.id.desc() };
        if("statusASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.status.asc() , issue.id.desc() };
        if("statusDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.status.desc() , issue.id.desc() };
        if("writeDateASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.writeEmbedded.writeDate.asc() , issue.id.desc() };
        if("writeDateDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { issue.writeEmbedded.writeDate.desc() , issue.id.desc() };
        return new OrderSpecifier[]{
                issue.status.desc()
                , issue.writeEmbedded.writeDate.desc()
                , issue.title.asc()
                , issue.requestDate.desc()
                , issue.writeEmbedded.writer.userName.asc()
                , issue.priority.asc()
                , issue.id.desc()
        };
    }
}
