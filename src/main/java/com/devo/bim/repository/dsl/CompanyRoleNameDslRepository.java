package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.CompanyRoleName;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QCompanyRoleName.companyRoleName;

@Repository
@RequiredArgsConstructor
public class CompanyRoleNameDslRepository {
    private final JPAQueryFactory queryFactory;

    public CompanyRoleName findCompanyRoleNameByCompanyRoleIdAndLanguageCode(long companyRoleId, String languageCode) {
        return queryFactory
                .select(companyRoleName)
                .from(companyRoleName)
                .where(
                        getWhereCompanyRoleIdEq(companyRoleId),
                        getWhereLanguageCodeEq(languageCode)
                ).fetchOne();

    }

    private BooleanExpression getWhereCompanyRoleIdEq(long companyRoleId) {
        return companyRoleName.companyRole.id.eq(companyRoleId);
    }
    private BooleanExpression getWhereLanguageCodeEq(String languageCode) {
        return companyRoleName.languageCode.eq(languageCode);
    }
}
