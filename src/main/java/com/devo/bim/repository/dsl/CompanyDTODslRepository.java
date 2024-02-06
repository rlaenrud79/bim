package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.CompanyDTO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.vo.SearchCompanyVO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class CompanyDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<CompanyDTO> findCompanyDTOs(SearchCompanyVO searchCompanyVO){
        List<CompanyDTO> content = queryFactory
                .select(Projections.constructor(CompanyDTO.class
                        , company
                        , JPAExpressions
                                .select(account)
                                .from(account)
                                .where(
                                        account.company.id.eq(company.id),
                                        account.responsible.eq(true)
                                )
                ))
                .from(company)
                .where(
                        getWhereCompanyProjectIdEq(searchCompanyVO),
                        getWhereCompanyWorksContains(searchCompanyVO),
                        getWhereCompanyNameLike(searchCompanyVO)
                ).fetch();
        return content;
    }

    private BooleanExpression getWhereCompanyNameLike(SearchCompanyVO searchCompanyVO) {
        if(!StringUtils.isEmpty(searchCompanyVO.getCompanyName())) return company.name.contains(searchCompanyVO.getCompanyName());
        return null;
    }

    @NotNull
    private BooleanExpression getWhereCompanyWorksContains(SearchCompanyVO searchCompanyVO) {
        if(searchCompanyVO.getWorkId() > 0) return company.works.contains(getSubQueryWorks(searchCompanyVO));
        return null;
    }

    private JPQLQuery<Work> getSubQueryWorks(SearchCompanyVO searchCompanyVO) {
        return JPAExpressions.select(work).from(work).where(work.id.eq(searchCompanyVO.getWorkId()));
    }

    private BooleanExpression getWhereCompanyProjectIdEq(SearchCompanyVO searchCompanyVO) {
        return company.project.id.eq(searchCompanyVO.getProjectId());
    }
}
