package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.CompanyRoleNameDTO;
import com.devo.bim.model.dto.SearchUserDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCoWorkJoiner.coWorkJoiner;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QCompanyRole.companyRole;
import static com.devo.bim.model.entity.QCompanyRoleName.companyRoleName;
import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QRole.role;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class SearchUserDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<SearchUserDTO> findCoWorkJoinerDTOs(long projectId, long coWorkId){
        List<SearchUserDTO> content = queryFactory
                .select(Projections.constructor(SearchUserDTO.class, account))
                .distinct()
                .from(coWorkJoiner)
                .join(coWorkJoiner.joiner, account)
                .join(account.roles, role)
                .join(account.company, company)
                .where(
                        getWhereAccountEnabledEqTrue(),
                        getWhereCompanyStatusEqREG(),
                        getWhereCompanyProjectIdEq(projectId),
                        getWhereCoWorkJoinerCoWorkIdEq(coWorkId)
                ).orderBy(
                        getOrderBySortProp()
                ).fetch();

        setCompanyRoleName(content);

        return content;
    }

    private void setCompanyRoleName(List<SearchUserDTO> content) {
        List<CompanyRoleNameDTO> companyRoleNames = getCompanyUserNames(toCompanyRoleIds(content));

        content.forEach(t -> {

            List<CompanyRoleNameDTO> tmpCompanyRoleNameDTOs = companyRoleNames.stream()
                    .filter(s -> s.getCompanyId() == t.getAccountDTO().getCompanyId())
                    .collect(Collectors.toList());

            for (CompanyRoleNameDTO item : tmpCompanyRoleNameDTOs) {
                t.addCompanyRoleName(item.getCompanyRoleNameId(), item.getLanguageCode(), item.getCompanyRoleName());
            }
        });
    }

    private BooleanExpression getWhereCoWorkJoinerCoWorkIdEq(long coWorkId) {
        return coWorkJoiner.coWork.id.eq(coWorkId);
    }

    public List<SearchUserDTO> findSearchUserDTOs(long projectId){
        List<SearchUserDTO> content = queryFactory
                .select(Projections.constructor(SearchUserDTO.class, account))
                .distinct()
                .from(account)
                .join(account.roles, role)
                .join(account.company, company)
                .where(
                        getWhereAccountEnabledEqTrue(),
                        getWhereCompanyStatusEqREG(),
                        getWhereCompanyProjectIdEq(projectId)
                ).orderBy(
                        getOrderBySortProp()
                ).fetch();

        setCompanyRoleName(content);
        return content;
    }

    public List<SearchUserDTO> findSearchUserTextDTOs(long projectId, String searchText){
        List<SearchUserDTO> content = queryFactory
                .select(Projections.constructor(SearchUserDTO.class, account))
                .distinct()
                .from(account)
                .join(account.roles, role)
                .join(account.company, company)
                .where(
                        getWhereAccountEnabledEqTrue(),
                        getWhereCompanyStatusEqREG(),
                        getWhereCompanyProjectIdEq(projectId),
                        getWhereNameOrCompanyNameLike(searchText)
                ).orderBy(
                        getOrderBySortProp()
                ).fetch();

        setCompanyRoleName(content);
        return content;
    }

    private List<Long> toCompanyRoleIds(List<SearchUserDTO> content ){
        return content.stream().map(o -> o.getAccountDTO().getCompanyId()).collect(Collectors.toList());
    }

    private List<CompanyRoleNameDTO> getCompanyUserNames(List<Long> companyRoleIds){
        return   queryFactory
                .select(Projections.constructor(CompanyRoleNameDTO.class
                        , company
                        , companyRole
                        , companyRoleName
                ))
                .from(company)
                .join(company.companyRole, companyRole)
                .join(companyRole.companyRoleNames, companyRoleName)
                .where(
                        getWhereCompanyRoleNameCompanyRoleIdsIn(companyRoleIds)
                ).fetch();
    }

    private BooleanExpression getWhereCompanyRoleNameCompanyRoleIdsIn(List<Long> companyRoleIds) {
        return company.id.in(companyRoleIds);
    }

    private BooleanExpression getWhereAccountEnabledEqTrue() {
        return account.enabled.eq(1);
    }

    private BooleanExpression getWhereCompanyStatusEqREG(){
        return company.status.equalsIgnoreCase("REG");
    }

    private BooleanExpression getWhereCompanyProjectIdEq(long projectId) {
        return company.project.id.eq(projectId);
    }

    private OrderSpecifier[]  getOrderBySortProp(){
        return new OrderSpecifier[] { account.userName.asc() };
    }

    private BooleanExpression getWhereNameOrCompanyNameLike(String searchText) {
        return account.userName.containsIgnoreCase(searchText).or(company.name.containsIgnoreCase(searchText));
    }
}
