package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Company;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.RoleCode;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QRole.role;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class AccountDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<Account> findByIds(long projectId, List<Long> ids) {
        return queryFactory
                .select(account).distinct()
                .from(account)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereEnabledEqOne(),
                        getWhereIdsEq(ids)
                ).orderBy(
                        getOrderByIdDESC()
                ).fetch();
    }

    private BooleanExpression getWhereIdsEq(List<Long> ids) {
        return account.id.in(ids);
    }

    public List<Account> findByWorkIds(long projectId, List<Work> works) {

        return queryFactory
                .select(account).distinct()
                .from(account)
                .innerJoin(account.works, work)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereEnabledEqOne(),
                        work.id.in(convertWorksIds(works))
                ).orderBy(
                        getOrderByIdDESC()
                ).fetch();
    }

    public List<Account> findByProjectId(long projectId){
        List<Long> companyIds = getCompaniesByProjectId(projectId)
                .stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());

        List<Account> accounts = queryFactory
                .select(account)
                .from(account)
                .where(
                        getWhereAccountCompanyIdsIn(companyIds)
                ).fetch();
        return accounts;
    }

    private BooleanExpression getWhereAccountCompanyIdsIn(List<Long> companyIds) {
        return account.company.id.in(companyIds);
    }

    private List<Long> convertWorksIds(List<Work> works){
        return works.stream().map(o -> o.getId()).collect(Collectors.toList());
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return account.company.project.id.eq(projectId);
    }

    private BooleanExpression getWhereEnabledEqOne() {
        return account.enabled.eq(1);
    }

    private OrderSpecifier<Long> getOrderByIdDESC() {
        return account.id.desc();
    }

    public List<Account> findUsersByRoleCode(long projectId, RoleCode roleCode){
        return queryFactory
                .select(account).distinct()
                .from(account)
                .innerJoin(account.roles, role)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereEnabledEqOne(),
                        getWhereRoleCodeEq(roleCode)
                ).orderBy(
                        getOrderByIdDESC()
                ).fetch();
    }

    private BooleanExpression getWhereRoleCodeEq(RoleCode roleCode) {
        return role.code.eq(roleCode);
    }

    private List<Company> getCompaniesByProjectId(long projectId) {
        return queryFactory
                .select(company)
                .from(company)
                .where(
                        getWhereCompanyProjectIdEq(projectId)
                ).fetch();
    }

    private BooleanExpression getWhereCompanyProjectIdEq(long projectId) {
        return company.project.id.eq(projectId);
    }
}
