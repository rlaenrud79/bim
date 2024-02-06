package com.devo.bim.repository.dsl;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QMySnapShot.mySnapShot;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.devo.bim.model.entity.MySnapShot;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MySnapShotDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<MySnapShot> findByProjectIdAndSearchModelIdIn(long projectId, String searchModelId) {
        return queryFactory
                .select(mySnapShot)
                .from(account)
                .join(account.mySnapShots, mySnapShot)
                .where(
                    getWhereProjectIdEnabledEq(projectId),
                        getWhereSearchModelIdContains(searchModelId)
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return account.company.project.id.eq(projectId);
    }
    private BooleanExpression getWhereSearchModelIdContains(String searchModelId) {
        return mySnapShot.searchModelId.contains(searchModelId);
    }

}
