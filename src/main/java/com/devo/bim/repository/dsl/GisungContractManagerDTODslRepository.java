package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.GisungContractManagerDTO;
import com.devo.bim.model.dto.ModelingDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchGisungContractManagerVO;
import com.devo.bim.model.vo.SearchModelingVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QGisungContractManager.gisungContractManager;
import static com.devo.bim.model.entity.QModeling.modeling;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class GisungContractManagerDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<GisungContractManagerDTO> findGisungContractManagerDTOs(SearchGisungContractManagerVO searchGisungContractManagerVO, Pageable pageable) {
        QueryResults<GisungContractManagerDTO> results = queryFactory.select(Projections.constructor(GisungContractManagerDTO.class
                        , gisungContractManager.id
                        , gisungContractManager.projectId
                        , gisungContractManager.company
                        , gisungContractManager.damName
                        , gisungContractManager.stampPath
                        , gisungContractManager.writeEmbedded.writeDate
                        , gisungContractManager.writeEmbedded.writer
                ))
                .from(gisungContractManager)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungContractManagerVO.getProjectId()),
                        getWhereWriter(searchGisungContractManagerVO.getSearchUserId()),
                        getWhereSearchValue(searchGisungContractManagerVO.getSearchType(), searchGisungContractManagerVO.getSearchValue())
                )
                .orderBy(
                        getOrderBySortProp(searchGisungContractManagerVO.getSortProp())
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

    public List<GisungContractManagerDTO> findGisungContractManagerDTOs(SearchGisungContractManagerVO searchGisungContractManagerVO){
        return getQueryGisungContractManagerDTOJPAQuery(searchGisungContractManagerVO).fetch();
    }

    private JPAQuery<GisungContractManagerDTO> getQueryGisungContractManagerDTOJPAQuery(SearchGisungContractManagerVO searchGisungContractManagerVO) {
        return queryFactory
                .select(Projections.constructor(GisungContractManagerDTO.class
                        , gisungContractManager.id
                        , gisungContractManager.projectId
                        , gisungContractManager.company
                        , gisungContractManager.damName
                        , gisungContractManager.stampPath
                ))
                .from(gisungContractManager)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungContractManagerVO.getProjectId()),
                        getWhereWriter(searchGisungContractManagerVO.getSearchUserId()),
                        getWhereSearchValue(searchGisungContractManagerVO.getSearchType(), searchGisungContractManagerVO.getSearchValue())
                ).orderBy(
                        getOrderBySortProp(searchGisungContractManagerVO.getSortProp())
                );
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return gisungContractManager.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisungContractManager.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungContractManager.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungContractManager.writeEmbedded.writeDate.desc()};

        return new OrderSpecifier[]{
                gisungContractManager.writeEmbedded.writeDate.desc()
        };
    }

    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("company".equalsIgnoreCase(searchType)) {
            return gisungContractManager.company.containsIgnoreCase(searchValue);
        }
        if ("damName".equalsIgnoreCase(searchType)) {
            return gisungContractManager.damName.containsIgnoreCase(searchValue);
        }
        return null;
    }
}
