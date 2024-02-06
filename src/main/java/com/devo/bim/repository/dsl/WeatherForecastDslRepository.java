package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.WeatherForecast;
import com.devo.bim.model.enumulator.WeatherForecastType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.devo.bim.model.entity.QWeatherForecast.weatherForecast;

@Repository
@RequiredArgsConstructor
public class WeatherForecastDslRepository {
    private final JPAQueryFactory queryFactory;

    public WeatherForecast findNowForecastList(long projectId, LocalDateTime baseDateTime) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.NOWFORECAST),
                        getWhereNowForecastBaseTime(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    public WeatherForecast findShortDaysForecastList(long projectId, LocalDateTime baseDateTime, int plusDay) {

        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.SHORTFORECAST),
                        getWhereShortDaysBaseTime(baseDateTime, plusDay)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    public WeatherForecast findShortPeriodForecastList(long projectId, LocalDateTime baseDateTime) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.SHORTFORECAST),
                        getWhereShortPeriodBaseTime(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    public WeatherForecast findShortDateForecast(long projectId, LocalDateTime baseDateTime) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.SHORTFORECAST),
                        getWhereShortPeriodBaseTime2(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    public WeatherForecast findLongWeatherForecast(long projectId, LocalDateTime baseDateTime) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.LONGWHEATHERFORECAST),
                        getWhereLongForecastBaseTime(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    public WeatherForecast findLongTemperatureForecast(long projectId, LocalDateTime baseDateTime) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(WeatherForecastType.LONGTEMPERATUREFORECAST),
                        getWhereLongForecastBaseTime(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .fetchFirst();
    }

    private BooleanExpression getWhereNowForecastBaseTime(LocalDateTime dateTime) {
        BooleanExpression whereCondition = weatherForecast.baseDateTime.after(dateTime.withMinute(30).withSecond(0).withNano(0).minusHours(7));
        if (dateTime.getMinute() >= 30) {
            whereCondition = whereCondition.and(weatherForecast.baseDateTime.before(dateTime.withMinute(31).withSecond(0).withNano(0).minusHours(1)));
        }
        return whereCondition;
    }

    private BooleanExpression getWhereShortDaysBaseTime(LocalDateTime dateTime, int day) {
        return weatherForecast.baseDateTime.after(dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(day + 1));
    }

    private BooleanExpression getWhereShortPeriodBaseTime(LocalDateTime dateTime) {
        return weatherForecast.baseDateTime.after(dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(2));
    }

    private BooleanExpression getWhereShortPeriodBaseTime2(LocalDateTime dateTime) {
        return weatherForecast.baseDateTime.before(dateTime);
    }

    private BooleanExpression getWhereLongForecastBaseTime(LocalDateTime dateTime) {
        return weatherForecast.baseDateTime.after(dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).minusDays(1));
    }

    private BooleanExpression getWhereProjectIdEq(long projectId) {
        return weatherForecast.projectId.eq(projectId);
    }

    private BooleanExpression getWhereRequestTypeEq(WeatherForecastType type) {
        return weatherForecast.requestType.eq(type);
    }

    public List<WeatherForecast> findExistForecast(long projectId, LocalDateTime baseDateTime, WeatherForecastType type) {
        return queryFactory
                .select(weatherForecast)
                .from(weatherForecast)
                .where(
                        getWhereProjectIdEq(projectId),
                        getWhereRequestTypeEq(type),
                        getWhereForecastBaseTimeEq(baseDateTime)
                )
                .orderBy(weatherForecast.requestDate.desc())
                .limit(1)
                .fetch();
    }

    private BooleanExpression getWhereForecastBaseTimeEq(LocalDateTime baseDateTime) {
        return weatherForecast.baseDateTime.eq(baseDateTime);
    }

    public void executeDeleteOldForecast(){
        queryFactory
                .delete(weatherForecast)
                .where(
                        getWhereOldForecast(weatherForecast.baseDateTime)
                )
                .execute();
    }

    private BooleanExpression getWhereOldForecast(DateTimePath<LocalDateTime> dateTime){
        return dateTime.before(LocalDateTime.now().minusDays(2));
    }
}
