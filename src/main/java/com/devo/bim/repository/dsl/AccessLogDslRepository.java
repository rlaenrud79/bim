package com.devo.bim.repository.dsl;

import com.devo.bim.component.Message;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.vo.SearchStatisticsUserVO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.devo.bim.model.entity.QAccessLog.accessLog;
import static com.devo.bim.model.entity.QAccount.account;
import static com.devo.bim.model.entity.QCalendar.calendar;
import static com.devo.bim.model.entity.QCompany.company;
import static com.devo.bim.model.entity.QMenu.menu;
import static com.devo.bim.model.entity.QWork.work;

@Repository
@RequiredArgsConstructor
public class AccessLogDslRepository {
    private final JPAQueryFactory queryFactory;
    private final Message message;

    public StatisticsLoginDTO findLoginByProjectIdAndAccessDateBetween(long projectId, String startDate, String endDate) {

        List<Tuple> results = queryFactory
                .select(
                        calendar.date
                        , accessLog.count()
                )
                .from(calendar)
                .leftJoin(accessLog)
                .on(calendar.date.eq(accessLog.date), accessLog.projectId.eq(projectId), accessLog.menuCode.eq("layout.common.menu.account.login"))
                .where(
                        calendar.date.goe(startDate)
                        , calendar.date.loe(endDate)
                )
                .groupBy(calendar.date)
                .orderBy(calendar.date.asc())
                .fetch();

        return new StatisticsLoginDTO(
                startDate
                , endDate
                , results
                .stream()
                .map(t -> new DateCountDTO(t.get(calendar.date), t.get(accessLog.count())))
                .collect(Collectors.toList()));
    }

    public StatisticsMenuDTO findMenuByProjectIdAndAccessDateBetween(long projectId, String startDate, String endDate) {

        List<Tuple> results = queryFactory
                .select(
                        menu.code
                        , accessLog.count()
                )
                .from(menu)
                .leftJoin(accessLog)
                .on(menu.code.eq(accessLog.menuCode)
                        , accessLog.projectId.eq(projectId)
                        , accessLog.date.goe(startDate)
                        , accessLog.date.loe(endDate)
                )
                .groupBy(menu.code)
                .orderBy(menu.code.asc())
                .fetch();

        return new StatisticsMenuDTO(
                startDate
                , endDate
                , results.stream()
                .map(t -> new NameCountDTO(message.getMessage(t.get(menu.code)), t.get(accessLog.count())))
                .collect(Collectors.toList()));
    }

    public List<StatisticsUserDTO> findAccountDTOsBySearchCondition(SearchStatisticsUserVO searchStatisticsUserVO, long projectId) {
        List<StatisticsUserDTO> results = queryFactory
                .select(Projections.constructor(StatisticsUserDTO.class
                        , account
                        , accessLog.menuCode
                        , accessLog.accessDate))
                .distinct()
                .from(accessLog)
                .join(account).on(accessLog.accessorId.eq(account.id))
                .join(account.company, company)
                .leftJoin(account.works, work)
                .where(
                        accessLog.projectId.eq(projectId)
                        , accessLog.date.goe(searchStatisticsUserVO.getStartDate())
                        , accessLog.date.loe(searchStatisticsUserVO.getEndDate())
                        , getWhereWorkIdEq(searchStatisticsUserVO.getWorkId())
                        , getWhereAccessorIdEq(searchStatisticsUserVO.getAccessorId())
                ).orderBy(
                        accessLog.accessDate.desc()
                ).fetch();

        results.forEach(t-> t.setMenuName(message.getMessage(t.getMenuCode())));

        return results;
    }

    public PageDTO<StatisticsUserDTO> findAccountDTOsByProjectId(SearchStatisticsUserVO searchStatisticsUserVO, long projectId, Pageable pageable) {
        QueryResults<StatisticsUserDTO> results = queryFactory
                .select(Projections.constructor(StatisticsUserDTO.class
                        , account
                        , accessLog.menuCode
                        , accessLog.accessDate))
                .distinct()
                .from(accessLog)
                .join(account).on(accessLog.accessorId.eq(account.id))
                .join(account.company, company)
                .leftJoin(account.works, work)
                .where(
                        accessLog.projectId.eq(projectId)
                        , accessLog.date.goe(searchStatisticsUserVO.getStartDate())
                        , accessLog.date.loe(searchStatisticsUserVO.getEndDate())
                        , getWhereWorkIdEq(searchStatisticsUserVO.getWorkId())
                        , getWhereAccessorIdEq(searchStatisticsUserVO.getAccessorId())
                ).orderBy(
                        accessLog.accessDate.desc()
                )
                .offset(
                        pageable.getPageNumber() * pageable.getPageSize()
                ).limit(
                        pageable.getPageSize()
                ).fetchResults();

        results.getResults().forEach(t-> t.setMenuName(message.getMessage(t.getMenuCode())));

        return new PageDTO<>(results, pageable);
    }

    private BooleanExpression getWhereAccessorIdEq(long accessorId) {
        if (accessorId > 0) return accessLog.accessorId.eq(accessorId);
        return null;
    }

    private BooleanExpression getWhereWorkIdEq(long workId) {
        if (workId > 0) return work.id.eq(workId);
        return null;
    }
}
