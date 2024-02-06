package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QGisungExcelFile.gisungExcelFile;

@Repository
@RequiredArgsConstructor
public class GisungExcelFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById() {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungExcelFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_EXCEL_FILE)
                        , Expressions.asString("")
                        , gisungExcelFile.originFileName
                        , gisungExcelFile.filePath
                        , gisungExcelFile.size
                        , Expressions.as(Expressions.constant(0L), "writerId")
                ))
                .from(gisungExcelFile)
                .orderBy(gisungExcelFile.id.desc())
                .limit(1)
                .fetchOne();
    }
}
