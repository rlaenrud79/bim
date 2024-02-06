package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.dto.PageDTO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class WorkDTODslRepository {

    private final JPAQueryFactory queryFactory;

    // List
    public List<WorkDTO> findWorkDTOs(SearchWorkVO searchWorkVO) {
        return getQueryWorkDTOJPAQuery(searchWorkVO).fetch();
    }

    // Paging
    public PageDTO<WorkDTO> findWorkDTOs(SearchWorkVO searchWorkVO, Pageable pageable) {
        QueryResults<WorkDTO> results = getQueryWorkDTOJPAQuery(searchWorkVO)
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();
        List<WorkDTO> content = results.getResults();
        long total = results.getTotal();
        return new PageDTO<>(content, pageable, total);
    }

    // JPA Query
    private JPAQuery<WorkDTO> getQueryWorkDTOJPAQuery(SearchWorkVO searchWorkVO) {
        return queryFactory
                .select(Projections.constructor(WorkDTO.class
                        , work.id
                        , work.projectId
                        , work.name
                        , workName.name
                        , work.sortNo
                        , work.status
                        , work.writeEmbedded.writer
                        , work.writeEmbedded.writeDate
                        , work.lastModifyEmbedded.lastModifyDate
                        , work.upId
                ))
                .from(work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchWorkVO)
                )
                .where(
                        getWhereProjectIdEnabledEq(searchWorkVO.getProjectId()),
                        getWhereWorkStatusEqUse()
                ).orderBy(
                        getOrderBySortProp(searchWorkVO.getSortProp())
                );
    }

    public List<WorkDTO> getQueryWorkDTOListQuery(SearchWorkVO searchWorkVO) {
        return queryFactory
                .select(Projections.constructor(WorkDTO.class
                        , work.id
                        , work.projectId
                        , work.name
                        , workName.name
                        , work.sortNo
                        , work.status
                        , work.writeEmbedded.writer
                        , work.writeEmbedded.writeDate
                        , work.lastModifyEmbedded.lastModifyDate
                        , work.upId
                ))
                .from(work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchWorkVO)
                )
                .where(
                        getWhereProjectIdEnabledEq(searchWorkVO.getProjectId()),
                        getWhereWorkStatusEqUse(),
                        getWhereUpId(searchWorkVO.getUpId())
                ).orderBy(
                        getOrderBySortProp(searchWorkVO.getSortProp())
                ).fetch();
    }

    private BooleanExpression getOnWorkNameLanguageCodeEq(SearchWorkVO searchWorkVO) {
        if(!StringUtils.isEmpty(searchWorkVO.getLang())) return workName.languageCode.equalsIgnoreCase(searchWorkVO.getLang());
        return null;
    }

    private BooleanExpression getWhereWorkStatusEqUse() {
        return work.status.eq(WorkStatus.USE);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return work.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProperties) {
        if ("NAME".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{work.name.asc()};
        if ("STATUS".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{work.status.desc()};
        return new OrderSpecifier[]{work.sortNo.asc()};
    }

    private BooleanExpression getWhereUpId(long upId) {
        return work.upId.eq(upId);
    }

}
