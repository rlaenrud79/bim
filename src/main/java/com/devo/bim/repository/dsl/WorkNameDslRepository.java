package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.WorkName;
import com.devo.bim.model.enumulator.WorkStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class WorkNameDslRepository {
    private final JPAQueryFactory queryFactory;

    public WorkName findWorkNameByWorkIdAndLanguageCode(long workId, String languageCode) {
        return queryFactory
                .select(workName)
                .from(workName)
                .where(
                        getWhereWorkIdEq(workId),
                        getWhereLanguageCodeEq(languageCode)
                ).fetchOne();
    }

    private BooleanExpression getWhereLanguageCodeEq(String languageCode) {
        return workName.languageCode.eq(languageCode);
    }

    private BooleanExpression getWhereWorkIdEq(long workId) {
        return workName.work.id.eq(workId);
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return work.projectId.eq(projectId);
    }

    private BooleanExpression getWhereStatusEq(WorkStatus workStatus) {
        return work.status.eq(workStatus);
    }

    public List<WorkName> findByProjectIdAndLanguageCode(long projectId, String languageCode) {
        return queryFactory
                .select(workName)
                .from(workName)
                .join(workName.work, work)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereStatusEq(WorkStatus.USE),
                        getWhereLanguageCodeEq(languageCode)
                ).fetch();
    }
}
