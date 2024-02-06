package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchAccountVO;
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

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QRole.role;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class AccountDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<AccountDTO> findAccountDTOsBySearchCondition(SearchAccountVO searchAccountVO, long projectId) {
        return queryFactory
                .select(Projections.constructor(AccountDTO.class, account))
                .distinct()
                .from(account)
                .join(account.roles, role)
                .leftJoin(account.works, work)
                .join(account.company, company)
                .where(
                        getWhereCompanyProjectIdEq(projectId),
                        getWhereCompanyIdEq(searchAccountVO.getCompanyId()),
                        getWhereRoleIdEq(searchAccountVO.getRoleId()),
                        getWhereWorkIdEq(searchAccountVO.getWorkId()),
                        getWhereSearchTextLike(searchAccountVO.getSearchType(), searchAccountVO.getSearchText())
                ).orderBy(
                        getOrderBySortProp(searchAccountVO.getSortProp())
                ).fetch();
    }

    public PageDTO<AccountDTO> findAccountDTOsByProjectId(SearchAccountVO searchAccountVO, long projectId, Pageable pageable) {
        QueryResults<AccountDTO> results = queryFactory
                .select(Projections.constructor(AccountDTO.class, account))
                .distinct()
                .from(account)
                .join(account.roles, role)
                .leftJoin(account.works, work)
                .join(account.company, company)
                .where(
                        getWhereCompanyProjectIdEq(projectId),
                        getWhereCompanyIdEq(searchAccountVO.getCompanyId()),
                        getWhereRoleIdEq(searchAccountVO.getRoleId()),
                        getWhereWorkIdEq(searchAccountVO.getWorkId()),
                        getWhereSearchTextLike(searchAccountVO.getSearchType(), searchAccountVO.getSearchText())
                ).orderBy(
                        getOrderBySortProp(searchAccountVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        return new PageDTO<>(results, pageable);
    }

    private BooleanExpression getWhereRoleIdEq(long roleId) {
        if (roleId > 0) return role.id.eq(roleId);

        return null;
    }

    private BooleanExpression getWhereWorkIdEq(long workId) {
        if (workId > 0) return work.id.eq(workId);
        return null;
    }

    private BooleanExpression getWhereCompanyIdEq(long companyId) {
        if (companyId > 0) return company.id.eq(companyId);
        return null;
    }

    private BooleanExpression getWhereSearchTextLike(String searchType, String searchText) {
        if (!StringUtils.isEmpty(searchType) && "NAME".equalsIgnoreCase(searchType))
            return account.userName.containsIgnoreCase(searchText);
        if (!StringUtils.isEmpty(searchType) && "EMAIL".equalsIgnoreCase(searchType))
            return account.email.containsIgnoreCase(searchText);
        if (!StringUtils.isEmpty(searchType) && "ALL".equalsIgnoreCase(searchType))
            return account.userName.containsIgnoreCase(searchText).or(account.email.containsIgnoreCase(searchText));
        return null;
    }

    public List<AccountDTO> findAccountDTOsByIds(long projectId, List<Long> userIds) {
        List<AccountDTO> content = queryFactory
                .select(Projections.constructor(AccountDTO.class, account))
                .distinct()
                .from(account)
                .join(account.roles, role)
                .join(account.company, company)
                .where(
                        getWhereAccountEnabledEqTrue(),
                        getWhereCompanyStatusEqREG(),
                        getWhereCompanyProjectIdEq(projectId),
                        getWhereAccountIdIn(userIds)
                ).orderBy(
                        getOrderBySortProp("userNameASC")
                ).fetch();
        return content;
    }

    private BooleanExpression getWhereAccountIdIn(List<Long> userIds) {
        return account.id.in(userIds);
    }

    private BooleanExpression getWhereAccountEnabledEqTrue() {
        return account.enabled.eq(1);
    }

    private BooleanExpression getWhereCompanyStatusEqREG() {
        return company.status.equalsIgnoreCase("REG");
    }

    private BooleanExpression getWhereCompanyProjectIdEq(long projectId) {
        return company.project.id.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("userNameASC".equals(sortProp)) return new OrderSpecifier[]{account.userName.asc()};
        if ("userNameDESC".equals(sortProp)) return new OrderSpecifier[]{account.userName.desc()};
        if ("emailASC".equals(sortProp)) return new OrderSpecifier[]{account.email.asc()};
        if ("emailDESC".equals(sortProp)) return new OrderSpecifier[]{account.email.desc()};
        if ("companyNameASC".equals(sortProp)) return new OrderSpecifier[]{account.company.id.asc()};
        if ("companyNameDESC".equals(sortProp)) return new OrderSpecifier[]{account.company.id.desc()};
        if ("enabledASC".equals(sortProp)) return new OrderSpecifier[]{account.enabled.asc()};
        if ("enabledDESC".equals(sortProp)) return new OrderSpecifier[]{account.enabled.desc()};
        if ("lastModifyDateASC".equals(sortProp)) return new OrderSpecifier[]{account.lastModifyEmbedded.lastModifyDate.asc()};
        if ("lastModifyDateDESC".equals(sortProp)) return new OrderSpecifier[]{account.lastModifyEmbedded.lastModifyDate.desc()};
        return new OrderSpecifier[]{account.id.asc()};
    }
}
