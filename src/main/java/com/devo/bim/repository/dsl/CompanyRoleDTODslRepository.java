package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.CompanyRoleDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchCompanyRoleVO;
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

import static com.devo.bim.model.entity.QCompanyRole.companyRole;
import static com.devo.bim.model.entity.QCompanyRoleName.companyRoleName;

@Repository
@RequiredArgsConstructor
public class CompanyRoleDTODslRepository {

    private final JPAQueryFactory queryFactory;

    // List
    public List<CompanyRoleDTO> findCompanyRoleDTOs(SearchCompanyRoleVO searchCompanyRoleVO){
        return getQueryCompanyRoleDTOJPAQuery(searchCompanyRoleVO).fetch();
    }

    // 페이징
    public PageDTO<CompanyRoleDTO> findCompanyRoleDTOs(SearchCompanyRoleVO searchCompanyRoleVO, Pageable pageable){
        QueryResults<CompanyRoleDTO> results = getQueryCompanyRoleDTOJPAQuery(searchCompanyRoleVO)
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        List<CompanyRoleDTO> content = results.getResults();
        long total = results.getTotal();
        return new PageDTO<>(content, pageable, total);
    }

    private JPAQuery<CompanyRoleDTO> getQueryCompanyRoleDTOJPAQuery(SearchCompanyRoleVO searchCompanyRoleVO) {
        return queryFactory
                .select(Projections.constructor(CompanyRoleDTO.class
                        , companyRole.id
                        , companyRole.projectId
                        , companyRole.name
                        , companyRoleName.name
                        , companyRole.sortNo
                        , companyRole.enabled
                        , companyRole.writeEmbedded.writer
                        , companyRole.writeEmbedded.writeDate
                        , companyRole.lastModifyEmbedded.lastModifyDate
                ))
                .from(companyRole)
                .leftJoin(companyRole.companyRoleNames, companyRoleName)
                .on(
                        getOnCompanyRoleNameLanguageCodeEq(searchCompanyRoleVO)
                ).where(
                        getWhereProjectIdEnabledEq(searchCompanyRoleVO.getProjectId()),
                        getWhereCompanyRoleEnabledEqTrue()
                ).orderBy(
                        getOrderBySortProp(searchCompanyRoleVO.getSortProp())
                );
    }

    private BooleanExpression getOnCompanyRoleNameLanguageCodeEq(SearchCompanyRoleVO searchCompanyRoleVO) {
        if(!StringUtils.isEmpty(searchCompanyRoleVO.getLang())) return companyRoleName.languageCode.equalsIgnoreCase(searchCompanyRoleVO.getLang());
        return null;
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return companyRole.projectId.eq(projectId);
    }

    private BooleanExpression getWhereCompanyRoleEnabledEqTrue() {
        return companyRole.enabled.eq(true);
    }

    private OrderSpecifier[]  getOrderBySortProp(String sortProperties){
        if("NAME".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { companyRole.name.asc() };
        if("ENABLED".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { companyRole.enabled.asc() };
        return new OrderSpecifier[] { companyRole.sortNo.asc() };
    }
}
