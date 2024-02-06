package com.devo.bim.repository.dsl;

import static com.devo.bim.model.entity.QMySnapShotFile.mySnapShotFile;

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
public class MySnapShotFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , mySnapShotFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.MODELING_SNAP_SHOT_MODEL_FILE)
                        , Expressions.asString("")
                        , mySnapShotFile.originFileName
                        , mySnapShotFile.filePath
                        , mySnapShotFile.size
                        , mySnapShotFile.writeEmbedded.writer.id
                ))
                .from(mySnapShotFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return mySnapShotFile.id.eq(id);
    }

}
