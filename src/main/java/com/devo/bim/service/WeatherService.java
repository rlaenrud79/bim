package com.devo.bim.service;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.WeatherForecast;
import com.devo.bim.model.enumulator.WeatherForecastType;
import com.devo.bim.repository.dsl.WeatherForecastDslRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService extends AbstractService {

    private final WeatherForecastDslRepository weatherForecastDslRepository;

    private final Gson gson = new Gson();

    public NowForecastDTO getNowForecast(LocalDateTime dateTime) {
        WeatherForecast weatherForecast = weatherForecastDslRepository.findNowForecastList(userInfo.getProjectId(), dateTime);

        if (weatherForecast == null) {
            return null;
        }

        String targetDate = dateTime.withMinute(0).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetHour = dateTime.withMinute(0).format(DateTimeFormatter.ofPattern("HHmm"));

        List<NowForecastItem> parseNowForecastItems = parseNowForecastItems(weatherForecast);

        Map<String, String> nowForecastMap = parseNowForecastItems.stream()
                .filter(t -> targetDate.equals(t.getFcstDate()) && targetHour.equals(t.getFcstTime()))
                .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.NowForecastCategory.class, t.getCategory()))
                .collect(Collectors.toMap(NowForecastItem::getCategory, NowForecastItem::getFcstValue));

        return new NowForecastDTO(
                targetDate,
                targetHour,
                LocalDateTime.parse(parseNowForecastItems.get(0).getBaseDate() + parseNowForecastItems.get(0).getBaseTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.T1H.toString()),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.SKY.toString()),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.PTY.toString()),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.VEC.toString()),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.WSD.toString()),
                nowForecastMap.get(WeatherForecastType.NowForecastCategory.REH.toString())
        );
    }

    public HashMap<String, ShortDayForecastDTO> getShortDaysForecast(LocalDateTime dateTime) {
        HashMap<String, ShortDayForecastDTO> shortForecastDTOs = new HashMap<>();

        shortForecastDTOs.put("day1", getShortDayForecast(dateTime, 1));
        shortForecastDTOs.put("day2", getShortDayForecast(dateTime, 2));

        return shortForecastDTOs;
    }

    private ShortDayForecastDTO getShortDayForecast(LocalDateTime dateTime, int plusDay) {
        WeatherForecast weatherForecast = weatherForecastDslRepository.findShortDaysForecastList(userInfo.getProjectId(), dateTime, plusDay);

        if (weatherForecast == null) {
            return null;
        }

        LocalDateTime targetDateTime = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(plusDay);
        String targetDate = targetDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<ShortForecastItem> parseShortForecastItems = parseShortForecastItems(weatherForecast);

        Map<String, String> AM = parseShortForecastItems.stream()
                .filter(t -> targetDate.equals(t.getFcstDate()) && targetDateTime.withHour(6).format(DateTimeFormatter.ofPattern("HHmm")).equals(t.getFcstTime()))
                .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortDaysForecastCategory.class, t.getCategory()))
                .collect(Collectors.toMap(ShortForecastItem::getCategory, ShortForecastItem::getFcstValue));

        Map<String, String> PM = parseShortForecastItems.stream()
                .filter(t -> targetDate.equals(t.getFcstDate()) && targetDateTime.withHour(15).format(DateTimeFormatter.ofPattern("HHmm")).equals(t.getFcstTime()))
                .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortDaysForecastCategory.class, t.getCategory()))
                .collect(Collectors.toMap(ShortForecastItem::getCategory, ShortForecastItem::getFcstValue));

        return new ShortDayForecastDTO(
                targetDateTime,
                AM.get(WeatherForecastType.ShortDaysForecastCategory.SKY.toString()),
                AM.get(WeatherForecastType.ShortDaysForecastCategory.PTY.toString()),
                AM.get(WeatherForecastType.ShortDaysForecastCategory.TMN.toString()),
                PM.get(WeatherForecastType.ShortDaysForecastCategory.SKY.toString()),
                PM.get(WeatherForecastType.ShortDaysForecastCategory.PTY.toString()),
                PM.get(WeatherForecastType.ShortDaysForecastCategory.TMX.toString())
        );
    }

    public List<ShortPeriodForecastDTO> getShortPeriodsForecast(LocalDateTime dateTime) {
        WeatherForecast weatherForecast = weatherForecastDslRepository.findShortPeriodForecastList(userInfo.getProjectId(), dateTime);

        if (weatherForecast == null) {
            return null;
        }

        List<ShortPeriodForecastDTO> shortPeriodForecastDTOs = new ArrayList<>();
        shortPeriodForecastDTOs.addAll(getShortPeriodForecast(weatherForecast, dateTime, 0));
        shortPeriodForecastDTOs.addAll(getShortPeriodForecast(weatherForecast, dateTime, 1));

        return shortPeriodForecastDTOs;
    }

    private List<ShortPeriodForecastDTO> getShortPeriodForecast(WeatherForecast weatherForecast, LocalDateTime dateTime, int plusDays) {

        LocalDateTime baseDateTime = dateTime.withMinute(0).withNano(0).plusDays(plusDays);
        String targetDate = baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetHour = baseDateTime.format(DateTimeFormatter.ofPattern("HHmm"));

        List<ShortForecastItem> parseShortForecastItems = parseShortForecastItems(weatherForecast);

        List<ShortPeriodForecastDTO> dayForecastList = new ArrayList<>();
        Map<String, List<ShortForecastItem>> dayForecastMap;
        if (plusDays == 0) {
            dayForecastMap = parseShortForecastItems.stream()
                    .filter(t -> (targetDate.equals(t.getFcstDate()) && targetHour.compareTo(t.getFcstTime()) < 0))
                    .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortPeriodForecastCategory.class, t.getCategory()))
                    .collect(Collectors.groupingBy(ShortForecastItem::getFcstTime));
        } else {
            dayForecastMap = parseShortForecastItems.stream()
                    .filter(t -> (targetDate.equals(t.getFcstDate()) && targetHour.compareTo(t.getFcstTime()) >= 0))
                    .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortPeriodForecastCategory.class, t.getCategory()))
                    .collect(Collectors.groupingBy(ShortForecastItem::getFcstTime));
        }

        for (String time : dayForecastMap.keySet()) {

            Map<String, String> collect = dayForecastMap.get(time).stream()
                    .collect(Collectors.toMap(ShortForecastItem::getCategory, ShortForecastItem::getFcstValue));

            dayForecastList.add(new ShortPeriodForecastDTO(
                    targetDate,
                    time,
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMP.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.SKY.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.PTY.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMN.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMX.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.PCP.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.SNO.toString())
            ));
        }

        dayForecastList.sort(Comparator.comparing(ShortPeriodForecastDTO::getFcstTime));

        return dayForecastList;
    }

    public List<ShortPeriodForecastDTO> getShortDateForecast(LocalDateTime dateTime) {
        // 6시 이전 데이터를 검색 (최저기온은 6시, 최고기온은 15시에만 값을 주므로..)
        LocalDateTime targetDateTime = dateTime.withHour(5).withMinute(0).withSecond(0).withNano(0);
        String targetDate = targetDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetHour = targetDateTime.format(DateTimeFormatter.ofPattern("HHmm"));

        WeatherForecast weatherForecast = weatherForecastDslRepository.findShortDateForecast(userInfo.getProjectId(), targetDateTime);
        if (weatherForecast == null) {
            return null;
        }

        List<ShortForecastItem> parseShortForecastItems = parseShortForecastItems(weatherForecast);
        Map<String, List<ShortForecastItem>> dayForecastMap;
        dayForecastMap = parseShortForecastItems.stream()
                .filter(t -> (targetDate.equals(t.getFcstDate())))
                .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortPeriodForecastCategory.class, t.getCategory()))
                .collect(Collectors.groupingBy(ShortForecastItem::getFcstTime));

        List<ShortPeriodForecastDTO> dayForecastList = new ArrayList<>();

        for (String time : dayForecastMap.keySet()) {
            Map<String, String> collect = dayForecastMap.get(time).stream()
                    .collect(Collectors.toMap(ShortForecastItem::getCategory, ShortForecastItem::getFcstValue));
            dayForecastList.add(new ShortPeriodForecastDTO(
                    targetDate,
                    time,
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMP.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.SKY.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.PTY.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMN.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMX.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.PCP.toString()),
                    collect.get(WeatherForecastType.ShortPeriodForecastCategory.SNO.toString())
            ));
        }
        return dayForecastList;
    }

    public List<ShortPeriodForecastDTO> getNowShortDateForecast(LocalDateTime dateTime) {

        WeatherForecast weatherForecast = weatherForecastDslRepository.findShortDateForecast(userInfo.getProjectId(), dateTime);
        if (weatherForecast == null) {
            return null;
        }

        String targetDate = dateTime.withMinute(0).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetHour = dateTime.withMinute(0).format(DateTimeFormatter.ofPattern("HHmm"));

        List<ShortForecastItem> parseShortForecastItems = parseShortForecastItems(weatherForecast);
        Map<String, List<ShortForecastItem>> dayForecastMap;
        List<ShortPeriodForecastDTO> dayForecastList = new ArrayList<>();
        if (parseShortForecastItems != null) {
            dayForecastMap = parseShortForecastItems.stream()
                    .filter(t -> (targetDate.equals(t.getFcstDate())))
                    .filter(t -> EnumUtils.isValidEnumIgnoreCase(WeatherForecastType.ShortPeriodForecastCategory.class, t.getCategory()))
                    .collect(Collectors.groupingBy(ShortForecastItem::getFcstTime));

            for (String time : dayForecastMap.keySet()) {
                Map<String, String> collect = dayForecastMap.get(time).stream()
                        .collect(Collectors.toMap(ShortForecastItem::getCategory, ShortForecastItem::getFcstValue));
                dayForecastList.add(new ShortPeriodForecastDTO(
                        targetDate,
                        time,
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMP.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.SKY.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.PTY.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMN.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.TMX.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.PCP.toString()),
                        collect.get(WeatherForecastType.ShortPeriodForecastCategory.SNO.toString())
                ));
            }
        }
        return dayForecastList;
    }

    public LongForecastDTO getLongForecast(LocalDateTime dateTime) {
        LongWeatherForecastItem weatherForecastItem = getLongWeatherForecast(dateTime);
        if (weatherForecastItem == null) {
            return null;
        }

        LongTemperatureForecastItem temperatureForecastItem = getLongTemperatureForecast(dateTime);
        if (temperatureForecastItem == null) {
            return null;
        }

        return new LongForecastDTO(
                dateTime.plusDays(3),
                weatherForecastItem.getWf3Am(),
                weatherForecastItem.getWf3Pm(),
                temperatureForecastItem.getTaMin3(),
                temperatureForecastItem.getTaMax3(),
                dateTime.plusDays(4),
                weatherForecastItem.getWf4Am(),
                weatherForecastItem.getWf4Pm(),
                temperatureForecastItem.getTaMin4(),
                temperatureForecastItem.getTaMax4(),
                dateTime.plusDays(5),
                weatherForecastItem.getWf5Am(),
                weatherForecastItem.getWf5Pm(),
                temperatureForecastItem.getTaMin5(),
                temperatureForecastItem.getTaMax5(),
                dateTime.plusDays(6),
                weatherForecastItem.getWf6Am(),
                weatherForecastItem.getWf6Pm(),
                temperatureForecastItem.getTaMin6(),
                temperatureForecastItem.getTaMax6(),
                dateTime.plusDays(7),
                weatherForecastItem.getWf7Am(),
                weatherForecastItem.getWf7Pm(),
                temperatureForecastItem.getTaMin7(),
                temperatureForecastItem.getTaMax7()
        );
    }

    private LongWeatherForecastItem getLongWeatherForecast(LocalDateTime dateTime) {
        WeatherForecast weatherForecast = weatherForecastDslRepository.findLongWeatherForecast(userInfo.getProjectId(), dateTime);

        if (weatherForecast == null) {
            return null;
        }

        return parseLongWeatherForecastItems(weatherForecast).get(0);
    }

    private LongTemperatureForecastItem getLongTemperatureForecast(LocalDateTime dateTime) {
        WeatherForecast weatherForecast = weatherForecastDslRepository.findLongTemperatureForecast(userInfo.getProjectId(), dateTime);

        if (weatherForecast == null) {
            return null;
        }

        return parseLongTemperatureForecastItems(weatherForecast).get(0);
    }

    private List<NowForecastItem> parseNowForecastItems(WeatherForecast weatherForecast) {
        Type type = new TypeToken<List<NowForecastItem>>() {
        }.getType();
        String jsonString = gson.toJson(weatherForecast.getForecastItems());
        return gson.fromJson(jsonString, type);
    }

    private List<ShortForecastItem> parseShortForecastItems(WeatherForecast weatherForecast) {
        Type type = new TypeToken<List<ShortForecastItem>>() {
        }.getType();
        String jsonString = gson.toJson(weatherForecast.getForecastItems());
        return gson.fromJson(jsonString, type);
    }

    private List<LongWeatherForecastItem> parseLongWeatherForecastItems(WeatherForecast weatherForecast) {
        Type type = new TypeToken<List<LongWeatherForecastItem>>() {
        }.getType();
        String jsonString = gson.toJson(weatherForecast.getForecastItems());
        return gson.fromJson(jsonString, type);
    }

    private List<LongTemperatureForecastItem> parseLongTemperatureForecastItems(WeatherForecast weatherForecast) {
        Type type = new TypeToken<List<LongTemperatureForecastItem>>() {
        }.getType();
        String jsonString = gson.toJson(weatherForecast.getForecastItems());
        return gson.fromJson(jsonString, type);
    }
}
