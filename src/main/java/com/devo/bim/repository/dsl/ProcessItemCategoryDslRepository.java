package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ProcessItemCategoryDTO;
import com.devo.bim.model.enumulator.TaskStatus;
import com.devo.bim.model.vo.SearchProcessItemCategoryVO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QProcessItem.processItem;
import static com.devo.bim.model.entity.QProcessItemCategory.processItemCategory;
import static com.devo.bim.model.entity.QViewJobSheetProcessItem.viewJobSheetProcessItem;

@Repository
@RequiredArgsConstructor
public class ProcessItemCategoryDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProcessItemCategoryDTO> findProcessCategory() {
        return queryFactory
                .select(Projections.constructor(ProcessItemCategoryDTO.class, processItemCategory))
                .from(processItemCategory)
                .where(
                        getWhereProcessItemCategoryEnabledEq())
                .orderBy(
                        processItemCategory.display.asc()
                ).fetch();
    }

    public List<ProcessItemCategoryDTO> selectCateList(SearchProcessItemCategoryVO searchProcessItemCategoryVO) {
        return queryFactory
                .select(Projections.constructor(ProcessItemCategoryDTO.class, processItemCategory
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItem.id)
                                        .from(processItem)
                                        .where(
                                                getWhereCateEq(searchProcessItemCategoryVO.getCateNo())
                                        ),
                                "processItemId")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItem.ganttTaskType)
                                        .from(processItem)
                                        .where(
                                                getWhereCateEq(searchProcessItemCategoryVO.getCateNo())
                                        ),
                                "ganttTaskType")
                        , ExpressionUtils.as(
                                JPAExpressions.select(processItem.id.count())
                                        .from(processItem)
                                        .where(
                                                getWhereCateNotEq(searchProcessItemCategoryVO.getCateNo())
                                                , processItem.taskStatus.eq(TaskStatus.REG)
                                        ),
                                "subProcessItemCount")
                        , ExpressionUtils.as(
                                JPAExpressions.select(viewJobSheetProcessItem.id.count())
                                        .from(viewJobSheetProcessItem)
                                        .join(viewJobSheetProcessItem.jobSheet, jobSheet)
                                        .where(
                                                getWhereJobSheetItemCateEq(searchProcessItemCategoryVO.getCateNo())
                                                , getWhereEnabled()
                                        ),
                                "jobSheetProcessItemCount")
                ))
                .from(processItemCategory)
                .where(
                        getWhereUpCodeEq(searchProcessItemCategoryVO.getUpCode())
                        , getWhereProcessItemCategoryEnabledEq())
                .orderBy(
                        processItemCategory.display.asc()
                ).fetch();
    }

    private BooleanExpression getWhereUpCodeEq(String upCode) {
        return processItemCategory.upCode.eq(upCode);
    }

    private BooleanExpression getWhereProcessItemCategoryEnabledEq() {
        return processItemCategory.enabled.eq(true);
    }

    private BooleanExpression getWhereEnabled() {
        return jobSheet.enabled.eq(true);
    }

    private BooleanExpression getWhereCateEq(int cateNo) {

        if (cateNo == 1) {
            return processItem.cate1.eq(processItemCategory.code)
                    .and(processItem.cate2.isNull().or(processItem.cate2.eq("")));
        } else if (cateNo == 2) {
            return processItem.cate1.eq(processItemCategory.upCode)
                    .and(processItem.cate2.eq(processItemCategory.code))
                    .and(processItem.cate3.isNull().or(processItem.cate3.eq("")));
        } else if (cateNo == 3) {
            return processItem.cate2.eq(processItemCategory.upCode)
                    .and(processItem.cate3.eq(processItemCategory.code))
                    .and(processItem.cate4.isNull().or(processItem.cate4.eq("")));
        } else if (cateNo == 4) {
            return processItem.cate3.eq(processItemCategory.upCode)
                    .and(processItem.cate4.eq(processItemCategory.code))
                    .and(processItem.cate5.isNull().or(processItem.cate5.eq("")));
        } else if (cateNo == 5) {
            return processItem.cate4.eq(processItemCategory.upCode)
                    .and(processItem.cate5.eq(processItemCategory.code))
                    .and(processItem.cate6.isNull().or(processItem.cate6.eq("")));
        } else if (cateNo == 6) {
            return processItem.cate5.eq(processItemCategory.upCode)
                    .and(processItem.cate6.eq(processItemCategory.code));
        } else {
            return null;
        }
    }

    private BooleanExpression getWhereCateNotEq(int cateNo) {

        if (cateNo == 1) {
            return processItem.cate1.eq(processItemCategory.code)
                    .and(processItem.cate2.isNotNull().and(processItem.cate2.ne("")));
        } else if (cateNo == 2) {
            return processItem.cate1.eq(processItemCategory.upCode)
                    .and(processItem.cate2.eq(processItemCategory.code))
                    .and(processItem.cate3.isNotNull().and(processItem.cate3.ne("")));
        } else if (cateNo == 3) {
            return processItem.cate2.eq(processItemCategory.upCode)
                    .and(processItem.cate3.eq(processItemCategory.code))
                    .and(processItem.cate4.isNotNull().and(processItem.cate4.ne("")));
        } else if (cateNo == 4) {
            return processItem.cate3.eq(processItemCategory.upCode)
                    .and(processItem.cate4.eq(processItemCategory.code))
                    .and(processItem.cate5.isNotNull().and(processItem.cate5.ne("")));
        } else if (cateNo == 5) {
            return processItem.cate4.eq(processItemCategory.upCode)
                    .and(processItem.cate5.eq(processItemCategory.code))
                    .and(processItem.cate6.isNotNull().and(processItem.cate6.ne("")));
        } else if (cateNo == 6) {
            return processItem.cate5.eq(processItemCategory.upCode)
                    .and(processItem.cate6.ne(processItemCategory.code));
        } else {
            return null;
        }
    }

    private BooleanExpression getWhereJobSheetItemCateEq(int cateNo) {

        if (cateNo == 1) {
            return viewJobSheetProcessItem.cate1.eq(processItemCategory.code);
        } else if (cateNo == 2) {
            return viewJobSheetProcessItem.cate1.eq(processItemCategory.upCode)
                    .and(viewJobSheetProcessItem.cate2.eq(processItemCategory.code));
        } else if (cateNo == 3) {
            return viewJobSheetProcessItem.cate2.eq(processItemCategory.upCode)
                    .and(viewJobSheetProcessItem.cate3.eq(processItemCategory.code));
        } else if (cateNo == 4) {
            return viewJobSheetProcessItem.cate3.eq(processItemCategory.upCode)
                    .and(viewJobSheetProcessItem.cate4.eq(processItemCategory.code));
        } else if (cateNo == 5) {
            return viewJobSheetProcessItem.cate4.eq(processItemCategory.upCode)
                    .and(viewJobSheetProcessItem.cate5.eq(processItemCategory.code));
        } else if (cateNo == 6) {
            return viewJobSheetProcessItem.cate5.eq(processItemCategory.upCode)
                    .and(viewJobSheetProcessItem.cate6.eq(processItemCategory.code));
        } else {
            return null;
        }
    }
}
