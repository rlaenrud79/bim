package com.devo.bim.repository.dsl;

import static com.devo.bim.model.entity.QIssueReport.issueReport;
import static com.devo.bim.model.entity.QIssueReportFile.issueReportFile;

import org.springframework.stereotype.Repository;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IssueReportFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , issueReportFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.ISSUE_REPORT_FILE)
                        , Expressions.asString("")
                        , issueReportFile.originFileName
                        , issueReportFile.filePath
                        , issueReportFile.size
                        , issueReport.reporter.id
                ))
                .from(issueReportFile)
                .innerJoin(issueReportFile.issueReport, issueReport)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return issueReportFile.id.eq(id);
    }

}
