package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.JobSheetTemplateDTO;
import com.devo.bim.model.vo.SearchJobSheetTemplateVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QJobSheetTemplate.jobSheetTemplate;

@Repository
@RequiredArgsConstructor
public class JobSheetTemplateDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<JobSheetTemplateDTO> findJobSheetTemplateDTOs(SearchJobSheetTemplateVO searchJobSheetTemplateVO) {
        return getQueryJobSheetTemplateDTOJPAQuery(searchJobSheetTemplateVO).fetch();
    }

    public Page<JobSheetTemplateDTO> findJobSheetTemplateDTOs(SearchJobSheetTemplateVO searchJobSheetTemplateVO, Pageable pageable) {
        QueryResults<JobSheetTemplateDTO> results = getQueryJobSheetTemplateDTOJPAQuery(searchJobSheetTemplateVO)
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();
        List<JobSheetTemplateDTO> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private JPAQuery<JobSheetTemplateDTO> getQueryJobSheetTemplateDTOJPAQuery(SearchJobSheetTemplateVO searchJobSheetTemplateVO) {
        return queryFactory
                .select(Projections.constructor(JobSheetTemplateDTO.class
                        , jobSheetTemplate.id
                        , jobSheetTemplate.projectId
                        , jobSheetTemplate.title
                        , jobSheetTemplate.contents
                        , jobSheetTemplate.enabled
                        , jobSheetTemplate.writeEmbedded.writer
                        , jobSheetTemplate.writeEmbedded.writeDate
                ))
                .from(jobSheetTemplate)
                .where(
                        getWhereProjectIdEnabledEq(searchJobSheetTemplateVO.getProjectId()),
                        getWhereEnabledEq(searchJobSheetTemplateVO.isEnabled())
                )
                .orderBy(
                        getOrderBySortProp(searchJobSheetTemplateVO.getSortProp())
                );
    }

    private BooleanExpression getWhereEnabledEq(boolean enabled) {
        if (!enabled) return null;
        return jobSheetTemplate.enabled.eq(enabled);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return jobSheetTemplate.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProperties) {
        if ("writeDateASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.writeEmbedded.writeDate.desc()};
        if ("titleASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.title.desc()};
        if ("enabledASC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.enabled.asc()};
        if ("enabledDESC".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[]{jobSheetTemplate.enabled.desc()};
        return new OrderSpecifier[]{jobSheetTemplate.writeEmbedded.writeDate.desc()};
    }
}
