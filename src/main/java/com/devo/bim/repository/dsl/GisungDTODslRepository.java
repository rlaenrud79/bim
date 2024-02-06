package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.GisungDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Gisung;
import com.devo.bim.model.enumulator.GisungStatus;
import com.devo.bim.model.vo.SearchGisungVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QGisung.gisung;

@Repository
@RequiredArgsConstructor
public class GisungDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<GisungDTO> findGisungDTOs(SearchGisungVO searchGisungVO, Pageable pageable) {
        QueryResults<GisungDTO> results = queryFactory.select(Projections.constructor(GisungDTO.class
                        , gisung.id
                        , gisung.projectId
                        , gisung.title
                        , gisung.year
                        , gisung.month
                        , gisung.gisungNo
                        , gisung.writeEmbedded.writeDate
                        , gisung.writeEmbedded.writer
                        , gisung.status
                        , gisung.sumPaidCost
                        , gisung.sumPaidProgressRate
                ))
                .from(gisung)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungVO.getProjectId()),
                        getWhereWriter(searchGisungVO.getSearchUserId()),
                        getWhereSearchValue(searchGisungVO.getSearchType(), searchGisungVO.getSearchValue()),
                        getWhereWriteDateBetween(searchGisungVO.getStartDateString(), searchGisungVO.getEndDateString())
                )
                .orderBy(
                        getOrderBySortProp(searchGisungVO.getSortProp())
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

    public Gisung getGisungAtId(long projectId, long id) {
        return queryFactory
                .select(gisung)
                .from(gisung)
                .where(
                        gisung.projectId.eq(projectId)
                        , gisung.status.eq(GisungStatus.COMPLETE)
                        , gisung.id.lt(id)
                ).limit(1)
                .orderBy(
                        gisung.id.desc()
                ).fetchOne();
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return gisung.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisung.projectId.eq(projectId);
    }

    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return gisung.title.containsIgnoreCase(searchValue);
        }
        if ("titleWithDescription".equalsIgnoreCase(searchType)) {
            return gisung.title.containsIgnoreCase(searchValue);
        }
        return null;
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return gisung.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisung.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisung.title.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisung.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisung.writeEmbedded.writeDate.desc()};

        return new OrderSpecifier[]{
                gisung.writeEmbedded.writeDate.desc()
        };
    }
}
