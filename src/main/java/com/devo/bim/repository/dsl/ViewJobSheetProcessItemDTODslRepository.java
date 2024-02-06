package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ViewJobSheetProcessItemDTO;
import com.devo.bim.model.enumulator.JobSheetStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QJobSheet.jobSheet;
import static com.devo.bim.model.entity.QViewJobSheetProcessItem.viewJobSheetProcessItem;

@Repository
@RequiredArgsConstructor
public class ViewJobSheetProcessItemDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ViewJobSheetProcessItemDTO> findAllByProcessId(long processItemId, long jobSheetId, long userId){
        return queryFactory
                .select(Projections.constructor(ViewJobSheetProcessItemDTO.class, viewJobSheetProcessItem, viewJobSheetProcessItem.jobSheet.reportDate))
                .from(viewJobSheetProcessItem)
                .join(viewJobSheetProcessItem.jobSheet, jobSheet)
                .where(
                        getWhereProcessItemIdEnabledEq(processItemId),
                        getWhereWriterIdEnabledEq(userId),
                        getWhereJobSheetIdEnabledNotEq(jobSheetId),
                        getWhereStatus(),
                        getWhereEnabled()
                ).orderBy(
                        viewJobSheetProcessItem.jobSheet.id.desc()
                ).fetch();
    }

    private BooleanExpression getWhereProcessItemIdEnabledEq(long processItemId) {
        return viewJobSheetProcessItem.processItem.id.eq(processItemId);
    }

    private BooleanExpression getWhereWriterIdEnabledEq(long userId) {
        if (userId == 0) return null;
        return jobSheet.writeEmbedded.writer.id.eq(userId);
    }

    private BooleanExpression getWhereJobSheetIdEnabledNotEq(long jobSheetId) {
        if (jobSheetId > 0) {
            return viewJobSheetProcessItem.jobSheet.id.ne(jobSheetId);
        } else {
            return viewJobSheetProcessItem.id.isNotNull();
        }
    }

    private BooleanExpression getWhereStatus() {
        return jobSheet.status.eq(JobSheetStatus.GOING).or(jobSheet.status.eq(JobSheetStatus.COMPLETE));
    }

    private BooleanExpression getWhereEnabled() {
        return jobSheet.enabled.eq(true);
    }
}
