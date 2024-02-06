package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.GisungProcessItemCostDetailDTO;
import com.devo.bim.model.enumulator.GisungCompareResult;
import com.devo.bim.model.enumulator.GisungStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QGisung.gisung;
import static com.devo.bim.model.entity.QGisungProcessItemCostDetail.gisungProcessItemCostDetail;
import static com.devo.bim.model.entity.QProcessItemCostDetail.processItemCostDetail;

@Repository
@RequiredArgsConstructor
public class GisungProcessItemCostDetailDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<GisungProcessItemCostDetailDTO> findProcessItemCostDetailByProcessItemIdListDTOs(long processItemId) {
        return queryFactory
                .select(Projections.constructor(GisungProcessItemCostDetailDTO.class
                        , processItemCostDetail.code
                        , processItemCostDetail.name
                        , processItemCostDetail.count
                        , processItemCostDetail.unit
                        , processItemCostDetail.unitPrice
                        , processItemCostDetail.cost
                        , processItemCostDetail.isFirst
                        , processItemCostDetail.paidProgressCount
                        , processItemCostDetail.paidCost))
                .from(processItemCostDetail)
                .where(
                        getWhereProcessItemIdEq(processItemId)
                ).orderBy(
                        processItemCostDetail.id.asc()
                ).fetch();
    }

    public List<GisungProcessItemCostDetailDTO> findGisungProcessItemCostDetail(Long gisungProcessItemId) {
        return queryFactory
                .select(Projections.constructor(GisungProcessItemCostDetailDTO.class
                        , gisungProcessItemCostDetail.id
                        , gisungProcessItemCostDetail.code
                        , gisungProcessItemCostDetail.name
                        , gisungProcessItemCostDetail.count
                        , gisungProcessItemCostDetail.unit
                        , gisungProcessItemCostDetail.unitPrice
                        , gisungProcessItemCostDetail.cost
                        , gisungProcessItemCostDetail.isFirst
                        , gisungProcessItemCostDetail.jobSheetProgressCount
                        , gisungProcessItemCostDetail.jobSheetProgressAmount
                        , gisungProcessItemCostDetail.paidProgressCount
                        , gisungProcessItemCostDetail.paidCost
                        , gisungProcessItemCostDetail.progressCount
                        , gisungProcessItemCostDetail.progressCost
                        , gisungProcessItemCostDetail.compareProgressCount
                        , gisungProcessItemCostDetail.compareProgressCost
                        , gisungProcessItemCostDetail.compareResult))
                .from(gisungProcessItemCostDetail)
                .where(
                        gisungProcessItemCostDetail.gisungProcessItem.id.eq(gisungProcessItemId)
                ).orderBy(
                        gisungProcessItemCostDetail.code.asc()
                        , gisungProcessItemCostDetail.id.asc()
                ).fetch();
    }

    public GisungProcessItemCostDetailDTO getGisungProcessItemCostDetailAtLatest(long projectId, long gisungId, long processItemId, String code) {
        return queryFactory
                .select(Projections.constructor(GisungProcessItemCostDetailDTO.class
                        , gisungProcessItemCostDetail.id
                        , gisungProcessItemCostDetail.code
                        , gisungProcessItemCostDetail.progressCount
                        , gisungProcessItemCostDetail.progressCost
                        , gisungProcessItemCostDetail.paidProgressCount
                        , gisungProcessItemCostDetail.paidCost))
                .from(gisungProcessItemCostDetail)
                .innerJoin(gisungProcessItemCostDetail.gisung, gisung)
                .where(
                        gisung.projectId.eq(projectId)
                        , gisung.status.eq(GisungStatus.COMPLETE)
                        , gisung.id.lt(gisungId)
                        , gisungProcessItemCostDetail.processItem.id.eq(processItemId)
                        , gisungProcessItemCostDetail.code.eq(code)
                ).limit(1)
                .orderBy(
                        gisungProcessItemCostDetail.code.asc()
                        , gisungProcessItemCostDetail.id.desc()
                ).fetchOne();
    }

    public List<GisungProcessItemCostDetailDTO> findAllGisungIdByGisungProcessItemCostDestinct(long gisungId) {
        return queryFactory
                .select(Projections.constructor(GisungProcessItemCostDetailDTO.class
                    , gisungProcessItemCostDetail.code))
                .distinct()
                .from(gisungProcessItemCostDetail)
                .where(
                        gisungProcessItemCostDetail.gisung.id.eq(gisungId),
                        gisungProcessItemCostDetail.code.isNotNull().and(gisungProcessItemCostDetail.code.ne(""))
                ).orderBy(
                        gisungProcessItemCostDetail.code.asc()
                ).fetch();
    }

    public List<GisungProcessItemCostDetailDTO> findAllGisungIdByGisungProcessItemCostCompareResult(long gisungId) {
        return queryFactory
                .select(Projections.constructor(GisungProcessItemCostDetailDTO.class
                        , gisungProcessItemCostDetail.code
                        , gisungProcessItemCostDetail.name))
                .distinct()
                .from(gisungProcessItemCostDetail)
                .where(
                        gisungProcessItemCostDetail.gisung.id.eq(gisungId),
                        gisungProcessItemCostDetail.compareResult.eq(GisungCompareResult.AP)
                ).orderBy(
                        gisungProcessItemCostDetail.code.asc()
                ).fetch();
    }

    private BooleanExpression getWhereProcessItemIdEq(long processItemId) {
        return processItemCostDetail.processItem.id.eq(processItemId);
    }
}
