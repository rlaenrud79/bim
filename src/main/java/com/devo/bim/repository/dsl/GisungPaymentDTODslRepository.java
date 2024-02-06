package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.GisungPaymentDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchGisungPaymentVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QGisungPayment.gisungPayment;
import static com.devo.bim.model.entity.QGisungPaymentFile.gisungPaymentFile;

@Repository
@RequiredArgsConstructor
public class GisungPaymentDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<GisungPaymentDTO> findGisungPaymentDTOs(SearchGisungPaymentVO searchGisungPaymentVO, Pageable pageable) {
        QueryResults<GisungPaymentDTO> results = queryFactory.select(Projections.constructor(GisungPaymentDTO.class
                        , gisungPayment.id
                        , gisungPayment.projectId
                        , gisungPayment.title
                        , gisungPayment.description
                        , gisungPaymentFile.id.as("fileId")
                        , gisungPaymentFile.originFileName
                        , gisungPaymentFile.size
                        , gisungPayment.writeEmbedded.writeDate
                        , gisungPayment.writeEmbedded.writer
                ))
                .from(gisungPayment)
                .join(gisungPayment.gisungPaymentFiles, gisungPaymentFile)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungPaymentVO.getProjectId()),
                        getWhereWriter(searchGisungPaymentVO.getSearchUserId()),
                        getWhereSearchValue(searchGisungPaymentVO.getSearchType(), searchGisungPaymentVO.getSearchValue()),
                        getWhereWriteDateBetween(searchGisungPaymentVO.getStartDateString(), searchGisungPaymentVO.getEndDateString())
                )
                .orderBy(
                        getOrderBySortProp(searchGisungPaymentVO.getSortProp())
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

    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return gisungPayment.title.containsIgnoreCase(searchValue);
        }
        if ("fileName".equalsIgnoreCase(searchType)) {
            return gisungPaymentFile.originFileName.containsIgnoreCase(searchValue);
        }
        if ("description".equalsIgnoreCase(searchType)) {
            return gisungPayment.description.containsIgnoreCase(searchValue);
        }
        if ("titleWithDescription".equalsIgnoreCase(searchType)) {
            return gisungPayment.title.containsIgnoreCase(searchValue).or(gisungPayment.description.containsIgnoreCase(searchValue));
        }
        return null;
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return gisungPayment.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return gisungPayment.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisungPayment.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPayment.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPayment.title.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPayment.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPayment.writeEmbedded.writeDate.desc()};
        if ("fileNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPaymentFile.originFileName.asc()};
        if ("fileNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungPaymentFile.originFileName.desc()};

        return new OrderSpecifier[]{
                gisungPayment.writeEmbedded.writeDate.desc()
        };
    }
}
