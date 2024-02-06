package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.WorkStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class WorkDslRepository {
    private final JPAQueryFactory queryFactory;

    public Work findNextWorkBySortNo(long projectId, int sortNo) {
        return queryFactory
                .select(work)
                .from(work)
                .join(work.writeEmbedded.writer, account)
                .fetchJoin()
                .where(
                        getWhereProjectIdEnableEq(projectId),
                        getWhereWorkStatusEqUse(),
                        getWhereSortNoGT(sortNo)
                ).orderBy(
                        getOrderBySortNoASC()
                ).fetchFirst();
    }

    private OrderSpecifier<Integer> getOrderBySortNoASC() {
        return work.sortNo.asc();
    }

    @NotNull
    private BooleanExpression getWhereSortNoGT(int sortNo) {
        return work.sortNo.gt(sortNo);
    }

    private BooleanExpression getWhereWorkStatusEqUse() {
        return work.status.eq(WorkStatus.USE);
    }

    private BooleanExpression getWhereProjectIdEnableEq(long projectId) {
        return work.projectId.eq(projectId);
    }

    public Work findPreviousWorkBySortNo(long projectId, int sortNo) {
        return queryFactory
                .select(work)
                .from(work)
                .join(work.writeEmbedded.writer, account)
                .fetchJoin()
                .where(
                        getWhereProjectIdEnableEq(projectId),
                        getWhereWorkStatusEqUse(),
                        getWhereSortNoLT(sortNo)
                ).orderBy(
                        getOrderBySortNoDESC()
                ).fetchFirst();
    }

    private OrderSpecifier<Integer> getOrderBySortNoDESC() {
        return work.sortNo.desc();
    }

    @NotNull
    private BooleanExpression getWhereSortNoLT(int sortNo) {
        return work.sortNo.lt(sortNo);
    }
}
