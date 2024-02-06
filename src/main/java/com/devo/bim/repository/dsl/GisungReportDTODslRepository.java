package com.devo.bim.repository.dsl;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.dto.GisungReportDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.vo.SearchGisungReportVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QGisungReport.gisungReport;

@Repository
@RequiredArgsConstructor
public class GisungReportDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public PageDTO<GisungReportDTO> findGisungReportDTOs(SearchGisungReportVO searchGisungReportVO, Pageable pageable) {
        QueryResults<GisungReportDTO> results = queryFactory.select(Projections.constructor(GisungReportDTO.class
                        , gisungReport.id
                        , gisungReport.projectId
                        , gisungReport.title
                        , gisungReport.gisung.id
                        , gisungReport.surveyFileName
                        , gisungReport.surveyFileSize
                        , gisungReport.writeEmbedded.writeDate
                        , gisungReport.writeEmbedded.writer
                ))
                .from(gisungReport)
                .where(
                        getWhereProjectIdEnabledEq(searchGisungReportVO.getProjectId()),
                        getWhereWriter(searchGisungReportVO.getSearchUserId()),
                        getWhereSearchValue(searchGisungReportVO.getSearchType(), searchGisungReportVO.getSearchValue()),
                        getWhereWriteDateBetween(searchGisungReportVO.getStartDateString(), searchGisungReportVO.getEndDateString()),
                        getWhereGisungId(searchGisungReportVO.getGisungId())
                )
                .orderBy(
                        getOrderBySortProp(searchGisungReportVO.getSortProp())
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

    public FileDownloadInfoDTO findFileSurveyDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.surveyFileName
                        , gisungReport.surveyFilePath
                        , gisungReport.surveyFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findFilePartSurveyDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.partSurveyFileName
                        , gisungReport.partSurveyFilePath
                        , gisungReport.partSurveyFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findFileAggregateDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.aggregateFileName
                        , gisungReport.aggregateFilePath
                        , gisungReport.aggregateFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findFilePartAggregateDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.partAggregateFileName
                        , gisungReport.partAggregateFilePath
                        , gisungReport.partAggregateFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findFileAccountDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.accountFileName
                        , gisungReport.accountFilePath
                        , gisungReport.accountFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findFileEtcDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungReport.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_REPORT_SURVEY_FILE)
                        , Expressions.asString("")
                        , gisungReport.etcFileName
                        , gisungReport.etcFilePath
                        , gisungReport.etcFileSize
                        , gisungReport.writeEmbedded.writer.id
                ))
                .from(gisungReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return gisungReport.id.eq(id);
    }

    private BooleanExpression getWhereSearchValue(String searchType, String searchValue) {
        if ("title".equalsIgnoreCase(searchType)) {
            return gisungReport.title.containsIgnoreCase(searchValue);
        }
        if ("fileName".equalsIgnoreCase(searchType)) {
            return gisungReport.surveyFileName.containsIgnoreCase(searchValue);
        }
        if ("titleWithDescription".equalsIgnoreCase(searchType)) {
            return gisungReport.title.containsIgnoreCase(searchValue);
        }
        return null;
    }

    private BooleanExpression getWhereWriteDateBetween(String startDateString, String endDateString) {
        if (!Strings.isBlank(startDateString) && !Strings.isBlank(endDateString)) {
            return gisungReport.writeEmbedded.writeDate.between(
                    Utils.parseLocalDateTimeStart(startDateString),
                    Utils.parseLocalDateTimeEnd(endDateString)
            );
        }
        return null;
    }

    private BooleanExpression getWhereWriter(long userId) {
        if (userId == 0) return null;
        return gisungReport.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereProjectIdEnabledEq(long projectId) {
        return gisungReport.projectId.eq(projectId);
    }

    private OrderSpecifier[] getOrderBySortProp(String sortProp) {
        if ("titleASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.title.asc()};
        if ("titleDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.title.desc()};
        if ("writeDateASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.writeEmbedded.writeDate.asc()};
        if ("writeDateDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.writeEmbedded.writeDate.desc()};
        if ("fileNameASC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.surveyFileName.asc()};
        if ("fileNameDESC".equalsIgnoreCase(sortProp)) return new OrderSpecifier[]{gisungReport.surveyFileName.desc()};

        return new OrderSpecifier[]{
                gisungReport.writeEmbedded.writeDate.desc()
        };
    }

    private BooleanExpression getWhereGisungId(long gisungId) {
        return gisungReport.gisung.id.eq(gisungId);
    }

}
