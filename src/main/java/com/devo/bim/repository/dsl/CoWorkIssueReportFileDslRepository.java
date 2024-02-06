package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QCoWorkIssueReportFile.coWorkIssueReportFile;

@Repository
@RequiredArgsConstructor
public class CoWorkIssueReportFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , coWorkIssueReportFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.CO_WORK_ISSUE_REPORT_FILE)
                        , Expressions.asString("")
                        , coWorkIssueReportFile.originFileName
                        , coWorkIssueReportFile.filePath
                        , coWorkIssueReportFile.size
                        , Expressions.as(Expressions.constant(0L), "writerId")
                ))
                .from(coWorkIssueReportFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return coWorkIssueReportFile.id.eq(id);
    }

}
