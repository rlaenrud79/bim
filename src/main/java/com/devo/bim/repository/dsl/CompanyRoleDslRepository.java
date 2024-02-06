package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.entity.CompanyRole;
import com.devo.bim.model.entity.QAccount;
import com.devo.bim.model.vo.SearchAccountVO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QAccount.*;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QCompanyRole.companyRole;
import static com.devo.bim.model.entity.QRole.role;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class CompanyRoleDslRepository {
    private final JPAQueryFactory queryFactory;

    public CompanyRole findNextCompanyRoleBySortNo(long projectId, int sortNo) {
        return queryFactory
                .select(companyRole)
                .from(companyRole)
                .join(companyRole.writeEmbedded.writer, account)
                .fetchJoin()
                .where(
                        getWhereProjectIdEnabledEq(projectId),
                        getWhereEnabledEqTrue(),
                        getWhereSortNoGT(sortNo)
                ).orderBy(
                        getOrderBySortNoASC()
                ).fetchFirst();
    }

    public List<CompanyRole> findAllByProjectId(long projectId) {
        return queryFactory
                .select(companyRole)
                .distinct()
                .from(companyRole)
                .where(
                        getWhereProjectIdEnabledEq(projectId),
                        getWhereEnabledEqTrue()
                ).orderBy(
                        getOrderBySortNoASC()
                ).fetch();
    }

    public CompanyRole findPreviousCompanyRoleBySortNo(long projectId, int sortNo) {
        return queryFactory
                .select(companyRole)
                .from(companyRole)
                .join(companyRole.writeEmbedded.writer, account)
                .fetchJoin()
                .where(
                        getWhereProjectIdEnabledEq(projectId),
                        getWhereEnabledEqTrue(),
                        getWhereSortNoLT(sortNo)
                ).orderBy(
                        getOrderBySortNoDESC()
                ).fetchFirst();
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return companyRole.projectId.eq(projectId);
    }

    private BooleanExpression getWhereEnabledEqTrue() {
        return companyRole.enabled.eq(true);
    }

    @NotNull
    private BooleanExpression getWhereSortNoGT(int sortNo) {
        return companyRole.sortNo.gt(sortNo);
    }

    @NotNull
    private BooleanExpression getWhereSortNoLT(int sortNo) {
        return companyRole.sortNo.lt(sortNo);
    }

    private OrderSpecifier<Integer> getOrderBySortNoDESC() {
        return companyRole.sortNo.desc();
    }

    private OrderSpecifier<Integer> getOrderBySortNoASC() {
        return companyRole.sortNo.asc();
    }
}
