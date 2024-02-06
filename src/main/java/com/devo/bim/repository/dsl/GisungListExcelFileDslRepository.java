package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QDocumentFile.documentFile;
import static com.devo.bim.model.entity.QGisungListExcelFile.gisungListExcelFile;

@Repository
@RequiredArgsConstructor
public class GisungListExcelFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungListExcelFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_EXCEL_FILE)
                        , Expressions.asString("")
                        , gisungListExcelFile.originFileName
                        , gisungListExcelFile.filePath
                        , gisungListExcelFile.size
                        , Expressions.as(Expressions.constant(0L), "writerId")
                ))
                .from(gisungListExcelFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return gisungListExcelFile.id.eq(id);
    }
}
