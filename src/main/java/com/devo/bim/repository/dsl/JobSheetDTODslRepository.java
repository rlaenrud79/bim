package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.JobSheetDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.devo.bim.model.vo.SearchJobSheetVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QJobSheetGrantor.jobSheetGrantor;
import static com.devo.bim.model.entity.QJobSheetProcessItem.jobSheetProcessItem;
import static com.devo.bim.model.entity.QJobSheetReference.jobSheetReference;
import static com.devo.bim.model.entity.QProcessItem.processItem;

@Repository
@RequiredArgsConstructor
public class JobSheetDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public PageDTO<JobSheetDTO> findJobSheetDTOs(SearchJobSheetVO searchJobSheetVO, Pageable pageable) {
        QueryResults<JobSheetDTO> results = queryFactory.select(Projections.constructor(JobSheetDTO.class
                        , jobSheet.id
                        , jobSheet.projectId
                        , jobSheet.title
                        , jobSheet.contents
                        , jobSheet.reportDate
                        , jobSheet.writeEmbedded.writeDate
                        , jobSheet.writeEmbedded.writer
                        , jobSheetGrantor.grantor
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetReference.id.count())
                                        .from(jobSheetReference)
                                        .where(jobSheetReference.jobSheet.id.eq(jobSheet.id)),
                                "referenceCount")
                        , jobSheet.status
                ))
                .from(jobSheet)
                .join(jobSheet.jobSheetGrantors, jobSheetGrantor)
                .where(
                        getWhereEnabledEqTrue(),
                        getWhereProjectIdEnabledEq(searchJobSheetVO.getProjectId()),
                        getWhereTitleOrContentsLike(searchJobSheetVO.getSearchDivisionType(), searchJobSheetVO.getSearchDivisionValue()),
                        getWhereWriterOrGrantorEq(searchJobSheetVO.getSearchUserType(), searchJobSheetVO.getSearchUserValue()),
                        getWhereReportDateBetween(searchJobSheetVO.getStartDateString(), searchJobSheetVO.getEndDateString())
                )
                .orderBy(
                        getOrderBySortProp(searchJobSheetVO.getSortProp())
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

    public List<JobSheetDTO> findJobSheetListDTOs(long projectId, String startDate, String endDate) {
        return queryFactory
                .select(Projections.constructor(JobSheetDTO.class
                        , jobSheet.id
                        , jobSheet.projectId
                        , jobSheet.title
                        , jobSheet.contents
                        , jobSheet.reportDate
                        , jobSheet.writeEmbedded.writeDate
                        , jobSheet.writeEmbedded.writer
                        , jobSheetGrantor.grantor
                        , ExpressionUtils.as(
                                JPAExpressions.select(jobSheetReference.id.count())
                                        .from(jobSheetReference)
                                        .where(jobSheetReference.jobSheet.id.eq(jobSheet.id)),
                                "referenceCount")
                        , jobSheet.status))
                .from(jobSheet)
                .join(jobSheet.jobSheetGrantors, jobSheetGrantor)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereStatus()
                        , getWhereEnabledEqTrue()
                        , getWhereReportDateBetween(startDate, endDate)
                ).orderBy(
                        jobSheet.id.desc()
                ).fetch();
    }

    public List<JobSheetDTO> findJobSheetListIdsByPhasingCodes(long projectId, long processId, long userId, String[] phasingCodes) {
        return queryFactory
                .select(Projections.constructor(JobSheetDTO.class
                        , jobSheet.id
                        , jobSheet.projectId
                        , jobSheet.title
                        , jobSheet.contents
                        , jobSheet.reportDate
                        , jobSheet.writeEmbedded.writeDate
                        , jobSheet.writeEmbedded.writer
                        , jobSheet.status))
                .from(jobSheetProcessItem)
                .join(jobSheetProcessItem.jobSheet, jobSheet)
                .join(jobSheetProcessItem.processItem, processItem)
                .where(
                        getWhereProjectIdEnabledEq(projectId)
                        , getWhereStatusWriting()
                        , getWhereEnabledEqTrue()
                        , getWhereWriterOrGrantorEq("writer", userId)
                        , getWherePhasingCodeIn(phasingCodes)
                        , getWhereProcessIdEnabledEq(processId)
                ).orderBy(
                        jobSheet.id.desc()
                ).fetch();
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        //보고일자 Desc > 제목 ASC > 결재자 ASC > 상태 ASC (상태순서 : 결재중/반려/승인)
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.title.desc()};
        if ("reportDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.reportDate.asc()};
        if ("reportDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.reportDate.desc()};
        if ("writerASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.writeEmbedded.writer.userName.asc()};
        if ("writerDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.writeEmbedded.writer.userName.desc()};
        if ("grantorASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheetGrantor.grantor.userName.asc()};
        if ("grantorDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheetGrantor.grantor.userName.desc()};
        if ("statusASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.status.asc()};
        if ("statusDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{jobSheet.status.desc()};

        return new OrderSpecifier[]{
                jobSheet.reportDate.desc()
                , jobSheet.title.asc()
                , jobSheetGrantor.grantor.userName.asc()
                , jobSheet.status.asc()
        };
    }

    private BooleanExpression getWhereEnabledEqTrue() {
        return jobSheet.enabled.eq(true);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return jobSheet.projectId.eq(projectId);
    }

    private BooleanExpression getWhereTitleOrContentsLike(String searchDivisionType, String searchDivisionValue) {
        if ("title".equalsIgnoreCase(searchDivisionType)) {
            return jobSheet.title.containsIgnoreCase(searchDivisionValue);
        }
        if ("contents".equalsIgnoreCase(searchDivisionType)) {
            return jobSheet.contents.containsIgnoreCase(searchDivisionValue);
        }
        if ("all".equalsIgnoreCase(searchDivisionType)) {
            return jobSheet.title.containsIgnoreCase(searchDivisionValue).or(jobSheet.contents.containsIgnoreCase(searchDivisionValue));
        }
        return null;
    }

    private BooleanExpression getWhereWriterOrGrantorEq(String searchUserType, long userId) {
        if (userId == 0) return null;
        if ("writer".equalsIgnoreCase(searchUserType)) {
            return jobSheet.writeEmbedded.writer.id.eq(userId);
        }
        if ("grantor".equalsIgnoreCase(searchUserType)) {
            return jobSheetGrantor.grantor.id.eq(userId);
        }
        return null;
    }

    private BooleanExpression getWhereReportDateBetween(String startDateString, String endDateString) {
        if(!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return jobSheet.reportDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }
    private BooleanExpression getWhereStatus() {
        return jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getWhereStatusWriting() {
        return jobSheet.status.eq(JobSheetStatus.WRITING);
    }

    private BooleanExpression getWherePhasingCodeIn(String[] phasingCode){
        return jobSheetProcessItem.phasingCode.in(phasingCode);
    }

    private BooleanExpression getWhereProcessIdEnabledEq(long processId) {
        return processItem.processInfo.id.eq(processId);
    }


}
