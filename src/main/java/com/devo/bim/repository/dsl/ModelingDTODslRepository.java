package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ModelingDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.vo.SearchModelingVO;
import com.devo.bim.model.enumulator.ConvertStatus;
import com.devo.bim.model.enumulator.ModelingStatus;
import com.devo.bim.model.enumulator.WorkStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QModeling.modeling;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class ModelingDTODslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ModelingDTO> findModelingDTOs(SearchModelingVO searchModelingVO){
        return getQueryModelingDTOJPAQuery(searchModelingVO).fetch();
    }

    private JPAQuery<ModelingDTO> getQueryModelingDTOJPAQuery(SearchModelingVO searchModelingVO) {
        return queryFactory
                .select(Projections.constructor(ModelingDTO.class, modeling))
                .from(modeling)
                .join(modeling.work, work)
                .where(
                        getWhereModelingProjectIdEq(searchModelingVO.getProjectId()),
                        getWhereModelingStatusEqUse(),
                        getWhereModelingLatest(searchModelingVO),
                        getWhereModelingWorkIdEq(searchModelingVO),
                        getWhereModelingFileNameLike(searchModelingVO),
                        getWhereModelingWriterIdEq(searchModelingVO),
                        getWhereModelingWriteDateBetween(searchModelingVO),
                        getWhereModelingIfcConvertStatusEq(searchModelingVO),
                        getWhereModelingIdsIn(searchModelingVO)
                        //getWhereModelingIdEq(30)
                ).orderBy(
                        getOrderBySortProps(searchModelingVO.getSortProp())
                );
    }

    // 페이징
    public PageDTO<ModelingDTO> findModelingDTOs(SearchModelingVO searchModelingVO, Pageable pageable){
        QueryResults<ModelingDTO> results = getQueryModelingDTOJPAQuery(searchModelingVO)
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        List<ModelingDTO> content = results.getResults();
        long total = results.getTotal();
        return new PageDTO<>(content, pageable, total);
    }

    private BooleanExpression getWhereModelingIdsIn(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.getModelingIds().size() > 0) return modeling.id.in(searchModelingVO.getModelingIds());
        return null;
    }

    private BooleanExpression getWhereModelingProjectIdEq(long projectId) {
        return modeling.projectId.eq(projectId);
    }

    private BooleanExpression getWhereModelingStatusEqUse() {
        return modeling.status.eq(ModelingStatus.USED);
    }

    private BooleanExpression getWhereModelingIdEq(long id) {
        return modeling.id.eq(id);
    }

    private BooleanExpression getWhereModelingLatest(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.isSearchLatest()) return modeling.latest.eq(true);
        return null;
    }

    private BooleanExpression getWhereModelingWorkIdEq(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.getWorkId() > 0) return modeling.work.id.eq(searchModelingVO.getWorkId());
        return null;
    }

    private BooleanExpression getWhereModelingFileNameLike(SearchModelingVO searchModelingVO) {
        if(!StringUtils.isEmpty(searchModelingVO.getFileName())) return modeling.fileName.containsIgnoreCase(searchModelingVO.getFileName());
        return null;
    }

    private BooleanExpression getWhereModelingWriterIdEq(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.getWriterId() != 0) return modeling.writeEmbedded.writer.id.eq(searchModelingVO.getWriterId());
        return null;
    }

    private BooleanExpression getWhereModelingIfcConvertStatusEq(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.getConvertStatus() != ConvertStatus.NONE) return modeling.convertStatus.eq(searchModelingVO.getConvertStatus());
        return null;
    }

    @NotNull
    private BooleanExpression getWhereModelingWriteDateBetween(SearchModelingVO searchModelingVO) {
        if(searchModelingVO.getWriteDateEnd() != null && searchModelingVO.getWriteDateEnd() != null)
            return modeling.writeEmbedded.writeDate.between(searchModelingVO.getWriteDateFrom().atStartOfDay(), searchModelingVO.getWriteDateEnd().atTime(23, 59, 59));
        return null;
    }

    private BooleanExpression getWhereWorkStatusEqUSE(){
        return work.status.eq(WorkStatus.USE);
    }

    @NotNull
    private OrderSpecifier[] getOrderBySortProps(String sortProperties) {
        if("FILENAME".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { modeling.fileName.asc() };
        if("WORK_NAME".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { work.name.asc() };
        if("ID".equalsIgnoreCase(sortProperties)) return new OrderSpecifier[] { modeling.id.asc() };
        return new OrderSpecifier[]{
                work.name.asc()
                , modeling.fileName.asc()
                , modeling.version.desc()
                , modeling.writeEmbedded.writeDate.desc()
        };
    }
}
