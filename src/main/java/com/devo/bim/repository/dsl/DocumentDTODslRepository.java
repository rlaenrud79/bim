package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.DocumentDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchDocumentVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QDocument.document;
import static com.devo.bim.model.entity.QDocumentCategory.documentCategory;
import static com.devo.bim.model.entity.QDocumentFile.documentFile;
import static com.devo.bim.model.entity.QWork.work;
import static com.devo.bim.model.entity.QWorkName.workName;

@Repository
@RequiredArgsConstructor
public class DocumentDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<DocumentDTO> findDocumentDTOs(SearchDocumentVO searchDocumentVO, Pageable pageable) {
        QueryResults<DocumentDTO> results = queryFactory.select(Projections.constructor(DocumentDTO.class
                        , document.id
                        , document.projectId
                        , document.title
                        , document.description
                        , documentFile.id.as("fileId")
                        , documentFile.originFileName
                        , documentFile.size
                        , work.id.as("workId")
                        , workName.name
                        , document.writeEmbedded.writeDate
                        , document.writeEmbedded.writer
                        , documentCategory.id.as("documentCategoryId")
                        , documentCategory.name.as("category")
                ))
                .from(document)
                .join(document.work, work)
                .leftJoin(document.documentFiles, documentFile)
                .leftJoin(document.documentCategory, documentCategory)
                .leftJoin(work.workNames, workName)
                .on(
                        getOnWorkNameLanguageCodeEq(searchDocumentVO.getLang())
                )
                .where(
                        getWhereProjectIdEnabledEq(searchDocumentVO.getProjectId()),
                        getWhereWorkType(searchDocumentVO.getSearchWorkId()),
                        getWhereWriter(searchDocumentVO.getSearchUserId()),
                        getWhereSearchValue(searchDocumentVO.getSearchType(), searchDocumentVO.getSearchValue()),
                        getWhereWriteDateBetween(searchDocumentVO.getStartDateString(), searchDocumentVO.getEndDateString()),
                        getWhereSearchCategory(searchDocumentVO.getSearchCategory())
                )
                .orderBy(
                        getOrderBySortProp(searchDocumentVO.getSortProp())
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                )
                .limit(
                        pageable.getPageSize()
                )
                .fetchResults();
        return new PageDTO<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return document.title.containsIgnoreCase(searchValue);
        }
        if ("fileName".equalsIgnoreCase(searchType)) {
            return documentFile.originFileName.containsIgnoreCase(searchValue);
        }
        if ("description".equalsIgnoreCase(searchType)) {
            return document.description.containsIgnoreCase(searchValue);
        }
        if ("titleWithDescription".equalsIgnoreCase(searchType)) {
            return document.title.containsIgnoreCase(searchValue).or(document.description.containsIgnoreCase(searchValue));
        }
        return null;
    }

    private BooleanExpression getWhereSearchCategory(long searchCategory) {
        if (searchCategory == 0) return null;
        return documentCategory.id.eq(searchCategory);
    }

    private BooleanExpression getOnWorkNameLanguageCodeEq(String lang) {
        if (StringUtils.isBlank(lang)) return null;
        return workName.languageCode.equalsIgnoreCase(lang);
    }

    private BooleanExpression getWhereWorkType(long workId) {
        if (workId == 0) return null;
        return work.id.eq(workId);
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return document.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return document.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return document.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{document.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{document.title.desc()};
        if ("workNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.asc()};
        if ("workNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{workName.name.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{document.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{document.writeEmbedded.writeDate.desc()};
        if ("fileNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{documentFile.originFileName.asc()};
        if ("fileNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{documentFile.originFileName.desc()};

        return new OrderSpecifier[]{
                document.writeEmbedded.writeDate.desc()
        };
    }
}
