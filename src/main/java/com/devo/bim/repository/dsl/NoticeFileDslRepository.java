package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QNotice.notice;
import static com.devo.bim.model.entity.QNoticeFile.noticeFile;

@Repository
@RequiredArgsConstructor
public class NoticeFileDslRepository {
    private final JPAQueryFactory queryFactory;

    public FileDownloadInfoDTO findFileDownloadInfoDTOById(long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , noticeFile.id
                        , Expressions.as(Expressions.constant(0L), "workId")
                        , Expressions.constant(FileDownloadUIType.NOTIFICATION_FILE)
                        , Expressions.asString("")
                        , noticeFile.originFileName
                        , noticeFile.filePath
                        , noticeFile.size
                        , notice.writeEmbedded.writer.id
                ))
                .from(noticeFile)
                .innerJoin(noticeFile.notice, notice)
                .where(
                        getWhereIdEq(id)
                ).fetchOne();
    }

    private BooleanExpression getWhereIdEq(long id) {
        return noticeFile.id.eq(id);
    }

}
