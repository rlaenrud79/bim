package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.VmGisungWorkCostDTO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QVmGisungWorkCost.vmGisungWorkCost;
import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class VmGisungWorkCostDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<VmGisungWorkCostDTO> findVmGisungWorkCostByGisungIdListDTOs(long projectId, long gisungId, String year, SearchWorkVO searchWorkVO) {
        return queryFactory
                .select(Projections.constructor(VmGisungWorkCostDTO.class
                        , vmGisungWorkCost.id
                        , vmGisungWorkCost.gisung.id
                        , vmGisungWorkCost.gisungNo
                        , vmGisungWorkCost.work.id
                        , vmGisungWorkCost.projectId
                        , vmGisungWorkCost.year
                        , vmGisungWorkCost.paidCost
                        , vmGisungWorkCost.prevPaidCost
                        , vmGisungWorkCost.totalPaidCost
                        , workName.name
                        , work.upId
                        , vmGisungWorkCost.workTotalAmount
                        , vmGisungWorkCost.workAmount
                ))
                .from(vmGisungWorkCost)
                .join(vmGisungWorkCost.work, work)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchWorkVO)
                )
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereGisungIdEq(gisungId)
                        , getWhereWorkYearEq(year)
                ).orderBy(
                        work.sortNo.asc()
                ).fetch();
    }

    private BooleanExpression getOnWorkNameLanguageCodeEq(SearchWorkVO searchWorkVO) {
        if(!StringUtils.isEmpty(searchWorkVO.getLang())) return workName.languageCode.equalsIgnoreCase(searchWorkVO.getLang());
        return null;
    }
    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return vmGisungWorkCost.projectId.eq(projectId);
    }
    private BooleanExpression getWhereGisungIdEq(long gisungId) {
        return vmGisungWorkCost.gisung.id.eq(gisungId);
    }

    private BooleanExpression getWhereWorkYearEq(String year) {
        return vmGisungWorkCost.year.eq(year);
    }
}
