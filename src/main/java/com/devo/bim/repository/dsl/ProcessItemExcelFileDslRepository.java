package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QProcessItemExcelFile.processItemExcelFile;

@Repository
@RequiredArgsConstructor
public class ProcessItemExcelFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , processItemExcelFile.id
                        , Expressions.constant(FileDownloadUIType.PROCESS_ITEM_EXCEL_FILE)
                        , Expressions.asString(String.valueOf(0))
                        , Expressions.asString("")
                        , processItemExcelFile.originFileName
                        , processItemExcelFile.filePath
                        , processItemExcelFile.size
                        , Expressions.as(Expressions.constant(0L), "writerId")
                ))
                .from(processItemExcelFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return processItemExcelFile.id.eq(id);
    }
}
