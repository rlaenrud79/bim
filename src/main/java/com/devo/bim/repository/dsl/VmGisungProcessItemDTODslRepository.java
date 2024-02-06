package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.VmGisungProcessItemDTO;
import com.devo.bim.model.enumulator.GisungCompareResult;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QGisung.gisung;
import static com.devo.bim.model.entity.QVmGisungProcessItem.vmGisungProcessItem;
import static com.devo.bim.model.entity.QVmJobSheetProcessItemCost.vmJobSheetProcessItemCost;
import static com.devo.bim.model.entity.QVmProcessItem.vmProcessItem;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class VmGisungProcessItemDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<VmGisungProcessItemDTO> findVmGisungProcessItemByGisungIdListDTOs(long projectId, long gisungId, String searchCompareResult) {
        return queryFactory
                .select(Projections.constructor(VmGisungProcessItemDTO.class
                        , vmGisungProcessItem.id
                        , vmGisungProcessItem.gisung.id
                        , vmGisungProcessItem.work.id
                        , vmGisungProcessItem.phasingCode
                        , vmGisungProcessItem.taskName
                        , vmGisungProcessItem.complexUnitPrice
                        , vmGisungProcessItem.taskCost
                        , vmGisungProcessItem.paidCost
                        , vmGisungProcessItem.paidProgressRate
                        , vmGisungProcessItem.beforeProgressRate
                        , vmGisungProcessItem.afterProgressRate
                        , vmGisungProcessItem.todayProgressRate
                        , vmGisungProcessItem.beforeProgressAmount
                        , vmGisungProcessItem.afterProgressAmount
                        , vmGisungProcessItem.todayProgressAmount
                        , vmGisungProcessItem.cost
                        , vmGisungProcessItem.progressRate
                        , vmGisungProcessItem.compareCost
                        , vmGisungProcessItem.compareProgressRate
                        , vmGisungProcessItem.compareResult
                        , vmGisungProcessItem.addItem
                        , vmGisungProcessItem.cate1
                        , vmGisungProcessItem.cate2
                        , vmGisungProcessItem.cate3
                        , vmGisungProcessItem.cate4
                        , vmGisungProcessItem.cate5
                        , vmGisungProcessItem.cate6
                        , vmGisungProcessItem.cate1Name
                        , vmGisungProcessItem.cate2Name
                        , vmGisungProcessItem.cate3Name
                        , vmGisungProcessItem.cate4Name
                        , vmGisungProcessItem.cate5Name
                        , vmGisungProcessItem.cate6Name))
                .from(vmGisungProcessItem)
                .join(vmGisungProcessItem.gisung, gisung)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereGisungIdEq(gisungId)
                        , getWhereCompareResultEq(searchCompareResult)
                ).orderBy(
                        vmGisungProcessItem.cate1Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate2Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate3Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate4Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate5Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    public List<VmGisungProcessItemDTO> findByGisungIdAndWorkIdDTOs(long gisungId, long workId, long workUpId) {
        return queryFactory
                .select(Projections.constructor(VmGisungProcessItemDTO.class
                        , vmGisungProcessItem.id
                        , vmGisungProcessItem.gisung.id
                        , vmGisungProcessItem.work.id
                        , vmGisungProcessItem.phasingCode
                        , vmGisungProcessItem.taskName
                        , vmGisungProcessItem.complexUnitPrice
                        , vmGisungProcessItem.taskCost
                        , vmGisungProcessItem.paidCost
                        , vmGisungProcessItem.paidProgressRate
                        , vmGisungProcessItem.beforeProgressRate
                        , vmGisungProcessItem.afterProgressRate
                        , vmGisungProcessItem.todayProgressRate
                        , vmGisungProcessItem.beforeProgressAmount
                        , vmGisungProcessItem.afterProgressAmount
                        , vmGisungProcessItem.todayProgressAmount
                        , vmGisungProcessItem.cost
                        , vmGisungProcessItem.progressRate
                        , vmGisungProcessItem.compareCost
                        , vmGisungProcessItem.compareProgressRate
                        , vmGisungProcessItem.compareResult
                        , vmGisungProcessItem.addItem
                        , vmGisungProcessItem.cate1
                        , vmGisungProcessItem.cate2
                        , vmGisungProcessItem.cate3
                        , vmGisungProcessItem.cate4
                        , vmGisungProcessItem.cate5
                        , vmGisungProcessItem.cate6
                        , vmGisungProcessItem.cate1Name
                        , vmGisungProcessItem.cate2Name
                        , vmGisungProcessItem.cate3Name
                        , vmGisungProcessItem.cate4Name
                        , vmGisungProcessItem.cate5Name
                        , vmGisungProcessItem.cate6Name
                        , work.upId
                ))
                .from(vmGisungProcessItem)
                .join(vmGisungProcessItem.work, work)
                .where(
                        getWhereGisungIdEq(gisungId)
                        , getWhereWorkIdEq(workId)
                        , getWhereWorkUpIdEq(workUpId)
                ).orderBy(
                        vmGisungProcessItem.cate1Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate2Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate3Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate4Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate5Display.asc().nullsFirst()
                        , vmGisungProcessItem.cate6Display.asc().nullsFirst()
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisung.projectId.eq(projectId);
    }
    private BooleanExpression getWhereGisungIdEq(long gisungId) {
        return vmGisungProcessItem.gisung.id.eq(gisungId);
    }
    private BooleanExpression getWhereCompareResultEq(String searchCompareResult) {
        if(!Strings.isBlank(searchCompareResult)) {
            return vmGisungProcessItem.compareResult.eq(GisungCompareResult.AP);
        }
        return null;
    }
    private BooleanExpression getWhereWorkIdEq(long workId) {
        if (workId == 0) return null;
        return vmGisungProcessItem.work.id.eq(workId);
    }
    private BooleanExpression getWhereWorkUpIdEq(long workUpId) {
        if (workUpId == 0) return null;
        return vmGisungProcessItem.work.upId.eq(workUpId);
    }
}
