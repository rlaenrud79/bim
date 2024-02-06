package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QCoWorkSnapShotFile.coWorkSnapShotFile;

@Repository
@RequiredArgsConstructor
public class CoWorkSnapShotFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , coWorkSnapShotFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.CO_WORK_SNAP_SHOT_MODEL_FILE)
                        , Expressions.asString("")
                        , coWorkSnapShotFile.originFileName
                        , coWorkSnapShotFile.filePath
                        , coWorkSnapShotFile.size
                        , coWorkSnapShotFile.writeEmbedded.writer.id
                ))
                .from(coWorkSnapShotFile)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return coWorkSnapShotFile.id.eq(id);
    }

}
