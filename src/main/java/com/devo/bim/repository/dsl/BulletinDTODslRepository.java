package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.BulletinDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchBulletinVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QBulletin.bulletin;
import static com.devo.bim.model.entity.QBulletinLikes.bulletinLikes;
import static com.devo.bim.model.entity.QBulletinReply.bulletinReply;

@Repository
@RequiredArgsConstructor
public class BulletinDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<BulletinDTO> findBulletinDTOs(SearchBulletinVO searchBulletinVO, Pageable pageable) {
        QueryResults<BulletinDTO> results = queryFactory.select(Projections.constructor(BulletinDTO.class
                        , bulletin.id
                        , bulletin.projectId
                        , bulletin.title
                        , bulletin.viewCount
                        , bulletin.writeEmbedded.writeDate
                        , bulletin.writeEmbedded.writer
                        , ExpressionUtils.as(
                                JPAExpressions.select(bulletinReply.id.count())
                                        .from(bulletinReply)
                                        .where(
                                                bulletinReply.bulletin.id.eq(bulletin.id),
                                                bulletinReply.enabled.eq(true)
                                        )
                                , "replyCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(bulletinLikes.id.count())
                                        .from(bulletinLikes)
                                        .where(
                                                bulletinLikes.bulletin.id.eq(bulletin.id),
                                                bulletinLikes.enabled.eq(true)
                                        )
                                , "likesCount")
                ))
                .from(bulletin)
                .where(
                        getWhereEnabledEqTrue(),
                        getWhereProjectIdEnabledEq(searchBulletinVO.getProjectId()),
                        getWhereTitleOrContentsLike(searchBulletinVO.getSearchType(), searchBulletinVO.getSearchValue()),
                        getWhereWriter(searchBulletinVO.getSearchUserId()),
                        getWhereWriteDateBetween(searchBulletinVO.getStartDateString(), searchBulletinVO.getEndDateString())
                )
                .orderBy(
                        getOrderBySortProp(searchBulletinVO.getSortProp())
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

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{bulletin.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{bulletin.title.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{bulletin.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{bulletin.writeEmbedded.writeDate.desc()};
        return new OrderSpecifier[]{
                bulletin.writeEmbedded.writeDate.desc()
        };
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return bulletin.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return bulletin.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereTitleOrContentsLike(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return bulletin.title.containsIgnoreCase(searchValue);
        }
        if ("contents".equalsIgnoreCase(searchType)) {
            return bulletin.contents.containsIgnoreCase(searchValue);
        }
        if ("all".equalsIgnoreCase(searchType)) {
            return bulletin.title.containsIgnoreCase(searchValue).or(bulletin.contents.containsIgnoreCase(searchValue));
        }
        return null;
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return bulletin.projectId.eq(projectId);
    }

    private BooleanExpression getWhereEnabledEqTrue() {
        return bulletin.enabled.eq(true);
    }
}
