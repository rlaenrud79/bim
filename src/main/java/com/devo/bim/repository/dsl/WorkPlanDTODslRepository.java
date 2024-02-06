package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.WorkPlanDTO;
import com.devo.bim.model.vo.SearchWorkPlanVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;
import static com.devo.bim.model.entity.QWorkPlan.workPlan;

@Repository
@RequiredArgsConstructor
public class WorkPlanDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<WorkPlanDTO> findWorkPlanDTOs(SearchWorkPlanVO searchWorkPlanVO, Pageable pageable) {
        QueryResults<WorkPlanDTO> results = queryFactory.select(Projections.constructor(WorkPlanDTO.class
                        , workPlan.id
                        , workPlan.projectId
                        , workPlan.year
                        , workPlan.month
                        , workPlan.monthRate
                        , workPlan.dayRate
                        , work.id.as("workId")
                        , workName.name
                        , workPlan.writeEmbedded.writeDate
                        , workPlan.writeEmbedded.writer
                ))
                .from(workPlan)
                .join(workPlan.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchWorkPlanVO.getLang())
                )
                .where(
                        getWhereProjectIdEnabledEq(searchWorkPlanVO.getProjectId()),
                        getWhereWorkType(searchWorkPlanVO.getSearchWorkId())
                )
                .orderBy(
                        getOrderBySortProp(searchWorkPlanVO.getSortProp())
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

    public List<WorkPlanDTO> findAllWorkPlanListForWork(long projectId, long workId, String reportDate) {

        return queryFactory
                .select(Projections.constructor(WorkPlanDTO.class,
                        workPlan.work.id
                        , workPlan.year
                        , workPlan.month
                        , workPlan.monthRate
                        , workPlan.dayRate
                ))
                .from(workPlan)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereWorkPlanWorkId(workId)
                        , workPlan.year.eq(reportDate.substring(0, 4))
                        , workPlan.month.loe(reportDate.substring(5, 7))
                ).orderBy(
                        workPlan.month.asc()
                ).fetch();
    }

    public List<WorkPlanDTO> findAllWorkPlanListForWorkSum(long projectId, long workId) {

        return queryFactory
                .select(Projections.constructor(WorkPlanDTO.class,
                        workPlan.work.id
                        , workPlan.year
                        , workPlan.month
                        , workPlan.monthRate
                        , workPlan.dayRate
                ))
                .from(workPlan)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereWorkPlanWorkId(workId)
                ).orderBy(
                        workPlan.month.asc()
                ).fetch();
    }

    private BooleanExpression getOnWorkNameLanguageCodeEq(String lang) {
        if (StringUtils.isBlank(lang)) return null;
        return workName.languageCode.equalsIgnoreCase(lang);
    }

    private BooleanExpression getWhereWorkType(long workId) {
        if (workId == 0) return null;
        return work.id.eq(workId);
    }

    private BooleanExpression getWhereWorkPlanWorkId(long workId) {
        if (workId == 0) return null;
        return workPlan.work.id.eq(workId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return workPlan.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("workNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.asc()};
        if ("workNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workPlan.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workPlan.writeEmbedded.writeDate.desc()};

        return new OrderSpecifier[]{
                workPlan.writeEmbedded.writeDate.desc()
        };
    }
}
