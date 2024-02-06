package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ChattingAlertDTO;
import com.devo.bim.model.dto.CoWorkDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.enumulator.CoWorkStatus;
import com.devo.bim.model.vo.SearchCoWorkVO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QCoWork.coWork;
import static com.devo.bim.model.entity.QCoWorkIssue.coWorkIssue;
import static com.devo.bim.model.entity.QCoWorkJoiner.coWorkJoiner;
import static com.devo.bim.model.entity.QCoWorkModeling.coWorkModeling;


@Repository
@RequiredArgsConstructor
public class CoWorkDslRepository {

    private final JPAQueryFactory queryFactory;

    public PageDTO<CoWorkDTO> findCoWorks(SearchCoWorkVO searchCoWorkVO, long projectId, long joinerId, Pageable pageable){

        QueryResults<CoWorkDTO> results = queryFactory
                .select(Projections.constructor(CoWorkDTO.class, coWork
                    , ExpressionUtils.as(
                            JPAExpressions.select(coWorkJoiner.id.count())
                                    .from(coWorkJoiner)
                                    .where(coWorkJoiner.coWork.id.eq(coWork.id)),
                            "coWorkJoinerCount")
                    , ExpressionUtils.as(
                            JPAExpressions.select(coWorkModeling.id.count())
                                    .from(coWorkModeling)
                                    .where(coWorkModeling.coWork.id.eq(coWork.id)),
                            "coWorkModelingCount")
                    , ExpressionUtils.as(
                            JPAExpressions.select(coWorkIssue.id.count())
                                    .from(coWorkIssue)
                                    .where(coWorkIssue.coWork.id.eq(coWork.id)),
                            "coWorkIssueCount")
                ))
                .from(coWork)
                .innerJoin(coWork.coWorkJoiners, coWorkJoiner)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereCoWorkJoinerEq(joinerId),
                        getWhereTitleSearchTextLike(searchCoWorkVO.getSubject()),
                        getWhereCoWorkWriterIdEq(searchCoWorkVO.getWriterId()),
                        getWhereCoWorkSearchDateBetween(searchCoWorkVO)
                ).orderBy(
                        getOrderBySortProps(searchCoWorkVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        List<CoWorkDTO> content = results.getResults();

        long total = results.getTotal();
        return new PageDTO<>(content, pageable, total);
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return coWork.projectId.eq(projectId);
    }

    private BooleanExpression getWhereCoWorkJoinerEq(long joinerId) {
        return coWorkJoiner.joiner.id.eq(joinerId);
    }

    private BooleanExpression getWhereTitleSearchTextLike(String subject) {
        if(!StringUtils.isEmpty(subject))
            return coWork.subject.containsIgnoreCase(subject);
        return null;
    }

    private BooleanExpression getWhereCoWorkWriterIdEq(long writerId) {
        if(writerId != 0) return coWork.writeEmbedded.writer.id.eq(writerId);
        return null;
    }

    @NotNull
    private BooleanExpression getWhereCoWorkSearchDateBetween(SearchCoWorkVO searchCoWorkVO) {
        if( searchCoWorkVO.getSearchDateStart() != null && searchCoWorkVO.getSearchDateEnd() != null)
            return coWork.writeEmbedded.writeDate.between(searchCoWorkVO.getSearchDateStart().atStartOfDay(), searchCoWorkVO.getSearchDateEnd().atTime(23, 59, 59));

        return null;
    }

    @NotNull
    private OrderSpecifier[] getOrderBySortProps(String sortProperties) {
        if("titleASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.subject.asc() , coWork.id.desc() };
        if("titleDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.subject.desc(), coWork.id.desc() };
        if("writerNameASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.writeEmbedded.writer.userName.asc() , coWork.id.desc() };
        if("writerNameDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.writeEmbedded.writer.userName.desc() , coWork.id.desc() };
        if("writeDateASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.writeEmbedded.writeDate.asc() , coWork.id.desc() };
        if("writeDateDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { coWork.writeEmbedded.writeDate.desc() , coWork.id.desc() };
        return new OrderSpecifier[]{
             coWork.writeEmbedded.writeDate.desc()
            , coWork.subject.asc()
            , coWork.writeEmbedded.writer.userName.asc()
            , coWork.id.desc()
        };
    }

    public List<ChattingAlertDTO> findUserJoinedCoWorks(long projectId, long userId) {
        return queryFactory
                .select(Projections.constructor(ChattingAlertDTO.class,
                        coWork.id,
                        coWork.subject,
                        coWork.chatting.roomId,
                        coWork.chatting.teamName
                ))
                .from(coWork)
                .join(coWork.coWorkJoiners, coWorkJoiner)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereCoWorkStatusEq(CoWorkStatus.GOING),
                        getWhereCoWorkJoinerIdEq(userId)
                ).fetch();
    }

    private BooleanExpression getWhereCoWorkStatusEq(CoWorkStatus status) {
        return coWork.status.eq(status);
    }

    private BooleanExpression getWhereCoWorkJoinerIdEq(long userId) {
        return coWorkJoiner.joiner.id.eq(userId);
    }
}
