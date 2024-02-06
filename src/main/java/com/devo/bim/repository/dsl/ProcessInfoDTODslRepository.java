package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ProcessInfoDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QProcessInfo.processInfo;

@Repository
@RequiredArgsConstructor
public class ProcessInfoDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProcessInfoDTO> findProcessInfoDTOsByProjectId(long projectId) {
        return queryFactory
                .select( Projections.constructor(ProcessInfoDTO.class, processInfo))
                .from(processInfo)
                .where(
                        getWhereProjectIdEq(projectId)
                ).orderBy(
                        getOrderById()
                ).fetch();
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return processInfo.projectId.eq(projectId);
    }

    private OrderSpecifier<Long> getOrderById() {
        return processInfo.id.desc();
    }
}
