package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.DocumentCategoryDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.entity.DocumentCategory;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.DocumentCategoryStatus;
import com.devo.bim.model.vo.SearchDocumentCategoryVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QDocumentCategory.documentCategory;
import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class DocumentCategoryDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<DocumentCategoryDTO> findDocumentCategoryDTOs(SearchDocumentCategoryVO searchDocumentCategoryVO) {
        return queryFactory
                .select(Projections.constructor(DocumentCategoryDTO.class
                        , documentCategory.id
                        , documentCategory.projectId
                        , documentCategory.name
                        , documentCategory.sortNo
                        , documentCategory.status
                        , documentCategory.writeEmbedded.writeDate
                        , documentCategory.writeEmbedded.writer
                        , documentCategory.lastModifyEmbedded.lastModifyDate
                ))
                .from(documentCategory)
                .where(
                        getWhereProjectIdEnabledEq(searchDocumentCategoryVO.getProjectId()),
                        getWhereSearchName(searchDocumentCategoryVO.getSearchValue()),
                        getWhereDocumentCategoryStatusEqUse()
                ).orderBy(
                        getOrderBySortNoASC()
                ).fetch();
    }

    public List<DocumentCategoryDTO> findDocumentCategorySelectDTOs(SearchDocumentCategoryVO searchDocumentCategoryVO) {
        return queryFactory
                .select(Projections.constructor(DocumentCategoryDTO.class
                        , documentCategory.id
                        , documentCategory.projectId
                        , documentCategory.name
                        , documentCategory.sortNo
                        , documentCategory.status
                        , documentCategory.writeEmbedded.writeDate
                        , documentCategory.writeEmbedded.writer
                ))
                .from(documentCategory)
                .where(
                        getWhereProjectIdEnabledEq(searchDocumentCategoryVO.getProjectId()),
                        getWhereDocumentCategoryStatusEqUse()
                )
                .orderBy(
                        getOrderBySortNoASC()
                ).fetch();
    }

    public DocumentCategory findNextDocumentCategoryBySortNo(long projectId, int sortNo) {
        return queryFactory
                .select(documentCategory)
                .from(documentCategory)
                .join(documentCategory.writeEmbedded.writer, account)
                .fetchJoin()
                .where(
                        getWhereProjectIdEnabledEq(projectId),
                        getWhereDocumentCategoryStatusEqUse(),
                        getWhereSortNoGT(sortNo)
                ).orderBy(
                        getOrderBySortNoASC()
                ).fetchFirst();
    }

    private OrderSpecifier<Integer> getOrderBySortNoASC() {
        return documentCategory.sortNo.asc();
    }

    private BooleanExpression getWhereSearchName(String searchValue) {
        if (searchValue == null) {
            return null;
        }
        return documentCategory.name.containsIgnoreCase(searchValue);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return documentCategory.projectId.eq(projectId);
    }

    private BooleanExpression getWhereDocumentCategoryStatusEqUse() {
        return documentCategory.status.eq(DocumentCategoryStatus.USE);
    }

    @NotNull
    private BooleanExpression getWhereSortNoGT(int sortNo) {
        return documentCategory.sortNo.gt(sortNo);
    }
}
