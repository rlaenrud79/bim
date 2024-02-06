package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.entity.QDocumentFile;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QBulletinFile.bulletinFile;
import static com.devo.bim.model.entity.QDocumentFile.documentFile;

@Repository
@RequiredArgsConstructor
public class DocumentFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id) {
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , documentFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.DOCUMENT_FILE)
                        , Expressions.asString("")
                        , documentFile.originFileName
                        , documentFile.filePath
                        , documentFile.size
                        , documentFile.writeEmbedded.writer.id
                ))
                .from(documentFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return documentFile.id.eq(id);
    }
}
