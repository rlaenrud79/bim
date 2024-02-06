package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QGisungPaymentFile.gisungPaymentFile;

@Repository
@RequiredArgsConstructor
public class GisungPaymentFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , gisungPaymentFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.GISUNG_PAYMENT_FILE)
                        , Expressions.asString("")
                        , gisungPaymentFile.originFileName
                        , gisungPaymentFile.filePath
                        , gisungPaymentFile.size
                        , gisungPaymentFile.writeEmbedded.writer.id
                ))
                .from(gisungPaymentFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return gisungPaymentFile.id.eq(id);
    }
}
