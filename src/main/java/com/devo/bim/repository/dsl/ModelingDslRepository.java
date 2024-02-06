package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.entity.Modeling;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.enumulator.ModelingStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QModeling.modeling;

@Repository
@RequiredArgsConstructor
public class ModelingDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<Modeling> findByFileNameAndFilePathNotExist(long projectId, long modelingId, String fileName) {
        return queryFactory
                .select(modeling)
                .from(modeling)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereFileNameEq(fileName),
                        getWhereFilePathNotExist(),
                        getWhereWorkIdEqSubQuery(modelingId)
                ).fetch();
    }



    public List<Modeling> findByFileNameAndIfcFilePathNotExist(long projectId, long modelingId) {
        return queryFactory
                .select(modeling)
                .from(modeling)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereIdEq(modelingId)
                ).fetch();
    }

    public Modeling findByNotInIdAndModelNameAndLatest(long projectId, long id, String rvtFileName){
        return queryFactory
                .select(modeling)
                .from(modeling)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereIdNotIn(id),
                        getWhereRvtFileNameEq(rvtFileName),
                        getWhereLatestEqTrue(),
                        getWhereWorkIdEqSubQuery(id)
                ).fetchOne();
    }

    public FileDownloadInfoDTO findModelFileById(long projectId, long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , modeling.id
                        , modeling.work.id
                        , Expressions.constant(FileDownloadUIType.MODELING_FILE)
                        , modeling.version
                        , modeling.fileName
                        , modeling.filePath
                        , modeling.fileSize
                        , modeling.writeEmbedded.writer.id
                ))
                .from(modeling)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereIdEq(id),
                        getWhereStatusEqUse()
                ).fetchOne();
    }


    public FileDownloadInfoDTO findModelIfcFileById(long projectId, long id){
        return queryFactory
                .select(Projections.constructor(FileDownloadInfoDTO.class
                        , modeling.id
                        , modeling.work.id
                        , Expressions.constant(FileDownloadUIType.MODELING_IFC_FILE)
                        , modeling.version
                        , modeling.ifcFileName
                        , modeling.ifcFilePath
                        , modeling.ifcFileSize
                        , modeling.writeEmbedded.writer.id
                ))
                .from(modeling)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereIdEq(id),
                        getWhereStatusEqUse()
                ).fetchOne();
    }

    private BooleanExpression getWhereWorkIdEqSubQuery(long modelingId) {
        return modeling.work.id.eq(JPAExpressions
                .select(modeling.work.id)
                .from(modeling)
                .where(modeling.id.eq(modelingId)));
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return modeling.projectId.eq(projectId);
    }

    @NotNull
    private BooleanExpression getWhereFileNameEq(String fileName) {
        return modeling.fileName.equalsIgnoreCase(fileName);
    }
    private BooleanExpression getWhereFilePathNotExist() {
        return modeling.filePath.isNotEmpty();
    }
    private BooleanExpression getWhereIfcFilePathNotExist() {
        return modeling.ifcFilePath.isNotEmpty();
    }
    private BooleanExpression getWhereIdNotIn(long id) { return modeling.id.notIn(id); }
    private BooleanExpression getWhereRvtFileNameEq(String rvtFileName) {
        return modeling.fileName.equalsIgnoreCase(rvtFileName);
    }
    private BooleanExpression getWhereLatestEqTrue() {
        return modeling.latest.isTrue();
    }
    private BooleanExpression getWhereIdEq(long id) {
        return modeling.id.eq(id);
    }
    private BooleanExpression getWhereStatusEqUse() {
        return modeling.status.eq(ModelingStatus.USED);
    }
}
