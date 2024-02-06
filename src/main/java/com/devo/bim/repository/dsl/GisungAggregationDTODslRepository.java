package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.GisungAggregationDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchGisungAggregationVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;

import static com.devo.bim.model.entity.QGisungAggregation.gisungAggregation;


@Repository
@RequiredArgsConstructor
public class GisungAggregationDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<GisungAggregationDTO> findGisungAggregationDTOs(SearchGisungAggregationVO searchGisungAggregationVO, Pageable pageable) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatExpression = "'YYYY-MM-DD'";

        QueryResults<GisungAggregationDTO> results = queryFactory.selectDistinct(Projections.constructor(GisungAggregationDTO.class
                        , gisungAggregation.year
                        , gisungAggregation.projectId
                        , gisungAggregation.writeEmbedded.writer
                ))
                .from(gisungAggregation)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungAggregationVO.getProjectId()),
                        getWhereWriter(searchGisungAggregationVO.getSearchUserId())
                )
                .orderBy(
                        getOrderBySortProp(searchGisungAggregationVO.getSortProp())
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

    private BooleanExpression getWhereIdEq(long id) {
        return gisungAggregation.id.eq(id);
    }



    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return gisungAggregation.title.containsIgnoreCase(searchValue);
        }
        return null;
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return gisungAggregation.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return gisungAggregation.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisungAggregation.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungAggregation.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungAggregation.title.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungAggregation.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungAggregation.writeEmbedded.writeDate.desc()};

        return new OrderSpecifier[]{
                gisungAggregation.writeEmbedded.writeDate.desc()
        };
    }
}
