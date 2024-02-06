package com.devo.bim.repository.dsl;

import static com.devo.bim.model.entity.QJobSheetFile.jobSheetFile;

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
public class JobSheetFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , jobSheetFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.JOB_SHEET_FILE)
                        , Expressions.asString("")
                        , jobSheetFile.originFileName
                        , jobSheetFile.filePath
                        , jobSheetFile.size
                        , jobSheetFile.writeEmbedded.writer.id
                ))
                .from(jobSheetFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return jobSheetFile.id.eq(id);
    }

}
