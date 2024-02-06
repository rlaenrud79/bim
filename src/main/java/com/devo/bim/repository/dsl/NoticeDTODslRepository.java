package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.NoticeDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.SearchNoticeVO;
import com.devo.bim.repository.spring.WorkRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QNotice.notice;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class NoticeDTODslRepository {

    private final JPAQueryFactory queryFactory;
    private final WorkRepository workRepository;

    // 페이징
    public PageDTO<NoticeDTO> findNoticeDTOs(SearchNoticeVO searchNoticeVO, long accountId, boolean isAdminProject, Pageable pageable){

        long totalWorkCnt = workRepository.findAll()
                .stream()
                .filter(t -> t.getProjectId() == searchNoticeVO.getProjectId() && t.getStatus() == WorkStatus.USE)
                .count();

        QueryResults<NoticeDTO> results = queryFactory
                .select(Projections.constructor(NoticeDTO.class, notice
                        , Expressions.asNumber(totalWorkCnt)
                ))
                .from(notice)
                .where(
                        getWhereWriterOrSameWorkEq(isAdminProject, accountId),
                        getWhereProjectIdEq(searchNoticeVO.getProjectId()),
                        getWhereSearchTextLike(searchNoticeVO.getSearchType(), searchNoticeVO.getSearchText()),
                        getWhereWriterName(searchNoticeVO.getWriterId()),
                        getWhereNoticeEnabledTrue(),
                        getWhereSearchDateBetween(searchNoticeVO)
                ).orderBy(
                        getOrderBySortProps(searchNoticeVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        List<NoticeDTO> noticeDTOS = results.getResults();

        long total = results.getTotal();

        return new PageDTO<>(noticeDTOS, pageable, total);
    }

    private BooleanExpression getWhereWriterName(long writerId){
        if(writerId != 0) return notice.writeEmbedded.writer.id.eq(writerId);
        return null;
    }

    private BooleanExpression getWhereWriterOrSameWorkEq(boolean isAdminProject, long accountId){
        if(!isAdminProject)
            return notice.writeEmbedded.writer.id.eq(accountId)
                    .or(
                            notice.works.contains(
                                    getSubQueryForWorks(accountId)
                            )
                    );
        return null;
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

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return notice.projectId.eq(projectId);
    }

    private BooleanExpression getWhereSearchTextLike(String searchType, String searchText) {
        if(!StringUtils.isEmpty(searchType) && "TITLE".equalsIgnoreCase(searchType))
            return notice.title.containsIgnoreCase(searchText);
        if(!StringUtils.isEmpty(searchType) && "CONTENTS".equalsIgnoreCase(searchType))
            return notice.contents.containsIgnoreCase(searchText);
        if(!StringUtils.isEmpty(searchType) && "TITLE_CONTENTS".equalsIgnoreCase(searchType))
            return notice.title.containsIgnoreCase(searchText).or(notice.contents.containsIgnoreCase(searchText));
        return null;
    }

    private BooleanExpression getWhereNoticeEnabledTrue() {
        return notice.enabled.eq(true);
    }

    @NotNull
    private BooleanExpression getWhereSearchDateBetween(SearchNoticeVO searchNoticeVO) {
        if(searchNoticeVO.getSearchDateFrom() != null
                && searchNoticeVO.getSearchDateEnd() != null)
            return notice.writeEmbedded.writeDate.between(searchNoticeVO.getSearchDateFrom().atStartOfDay(), searchNoticeVO.getSearchDateEnd().atTime(23, 59, 59));
        return null;
    }

    @NotNull
    private OrderSpecifier[] getOrderBySortProps(String sortProperties) {
        if("titleASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.title.asc() , notice.id.desc() };
        if("titleDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.title.desc(), notice.id.desc() };
        if("isPopUpASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.isPopup.asc() , notice.id.desc() };
        if("isPopUpDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.isPopup.desc() , notice.id.desc() };
        if("writerNameASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.writeEmbedded.writer.userName.asc() , notice.id.desc() };
        if("writerNameDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.writeEmbedded.writer.userName.desc() , notice.id.desc() };
        if("startDateASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.startDate.asc() , notice.id.desc() };
        if("startDateDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.startDate.desc() , notice.id.desc() };
        if("writeDateASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.writeEmbedded.writeDate.asc() , notice.id.desc() };
        if("writeDateDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { notice.writeEmbedded.writeDate.desc() , notice.id.desc() };
        return new OrderSpecifier[]{
                notice.writeEmbedded.writeDate.desc()
                , notice.title.asc()
                , notice.startDate.desc()
                , notice.writeEmbedded.writer.userName.asc()
                , notice.id.desc()
        };
    }
}
