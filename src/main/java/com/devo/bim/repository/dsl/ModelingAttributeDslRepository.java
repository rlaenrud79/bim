package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.ModelingAttribute;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QModeling.modeling;
import static com.devo.bim.model.entity.QModelingAssembly.modelingAssembly;
import static com.devo.bim.model.entity.QModelingAttribute.modelingAttribute;

@Repository
@RequiredArgsConstructor
public class ModelingAttributeDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ModelingAttribute> findModelingAttributeByProjectId(long projectId, String codeValidateKey) {
        return queryFactory
                .select(modelingAttribute).distinct()
                .from(modeling)
                .innerJoin(modeling.modelingAssemblies, modelingAssembly)
                .innerJoin(modelingAssembly.modelingAttributes, modelingAttribute)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereLatestEq(true)
                        , getWhereAttributeNameContains(codeValidateKey)
                ).fetch();
    }

    public List<ModelingAttribute> findModelingAttributePhasingCodeCount(long projectId, String phasingCodeConfigValue, String phasingCode) {
        return queryFactory
                .select(modelingAttribute)
                .from(modeling)
                .innerJoin(modeling.modelingAssemblies, modelingAssembly)
                .innerJoin(modelingAssembly.modelingAttributes, modelingAttribute)
                .where(
                        getWhereProjectIdEq(projectId)
                        , getWhereLatestEq(true)
                        , getWhereAttributeNameEq(phasingCodeConfigValue)
                        , getWhereAttributeValueEq(phasingCode)
                ).fetch();
    }

    public List<ModelingAttribute> findModelingAttributeFormulaByProperty(long projectId, String phasingCodeConfigValue, String phasingCode, String property) {
        return queryFactory
                .selectFrom(modelingAttribute)
                .where(
                     getWhereAttributeNameEq(property)
                    , modelingAttribute.modelingAssembly.id.in(
                            JPAExpressions
                                .select(modelingAttribute.modelingAssembly.id)
                                .from(modeling)
                                .innerJoin(modeling.modelingAssemblies, modelingAssembly)
                                .innerJoin(modelingAssembly.modelingAttributes, modelingAttribute)
                                .where(getWhereProjectIdEq(projectId)
                                        , getWhereLatestEq(true)
                                        , getWhereAttributeNameEq(phasingCodeConfigValue)
                                        , getWhereAttributeValueEq(phasingCode)

                        )
                    )
                ).fetch();
    }

    private BooleanExpression getWhereAttributeNameContains(String codeValidateKey) {
        return modelingAttribute.attributeName.eq(codeValidateKey);
    }

    private BooleanExpression getWhereAttributeNameEq(String attributeName) {
        return modelingAttribute.attributeName.eq(attributeName);
    }

    private BooleanExpression getWhereAttributeValueEq(String attributeValue) {
        return modelingAttribute.attributeValue.eq(attributeValue);
    }

    private BooleanExpression getWhereLatestEq(boolean isLatest) {
        return modeling.latest.eq(isLatest);
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return modeling.projectId.eq(projectId);
    }


}
