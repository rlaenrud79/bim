package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ProjectDTO;
import com.devo.bim.model.entity.Project;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.devo.bim.model.entity.QProject.project;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QAccount.account;

@Repository
@RequiredArgsConstructor
public class ProjectDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<Project> findAllProjectList() {
        return queryFactory
                .select(project)
                .from(project)
                .fetch();
    }

    public List<ProjectDTO> findAllProjectListForAdminSystem() {
        return queryFactory
                .select(Projections.constructor(ProjectDTO.class, project
                        , ExpressionUtils.as(
                                JPAExpressions.select(account.id.count())
                                        .from(account)
                                        .innerJoin(account.company, company)
                                        .where(company.project.id.eq(project.id)),
                                "addedUserCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(company.id.count())
                                        .from(company)
                                        .where(company.project.id.eq(project.id)),
                                "addedCompanyCount")
                ))
                .from(project)
                .orderBy(project.id.desc())
                .fetchResults()
                .getResults();
    }

    public List<Project> findEnabledProjectList(LocalDateTime dateTime) {
        return queryFactory
                .select(project)
                .from(project)
                .where(
                        getWhereBetweenPeriod(dateTime)
                )
                .fetch();
    }

    private BooleanExpression getWhereBetweenPeriod(LocalDateTime dateTime) {
        return project.startDate.before(dateTime).and(project.endDate.after(dateTime));
    }
}
