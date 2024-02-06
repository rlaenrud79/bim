package com.devo.bim.service;

import com.devo.bim.model.dto.ForecastAPIResponseDTO;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.entity.WeatherForecast;
import com.devo.bim.model.enumulator.WeatherForecastType;
import com.devo.bim.model.vo.WeatherForecastVO;
import com.devo.bim.repository.dsl.WeatherForecastDslRepository;
import com.devo.bim.repository.spring.ProjectRepository;
import com.devo.bim.repository.spring.WeatherForecastRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherAPIService extends AbstractService {

    @Value("${system.weather.domain.data_go_base}")
    private String dataGoBaseDomain;
    @Value("${system.weather.service_key}")
    private String serviceKey;
    private final String weatherCategory = "1360000";
    private final String villageForecastPath = "VilageFcstInfoService_2.0";
    private final String midForecastPath = "MidFcstInfoService";
    private final String nowForecastResource = "getUltraSrtFcst";
    private final String shortForecastResource = "getVilageFcst";
    private final String longWeatherForecastResource = "getMidLandFcst";
    private final String longTemperatureForecastResource = "getMidTa";

    private final WeatherForecastRepository weatherForecastRepository;
    private final ProjectRepository projectRepository;
    private final WeatherForecastDslRepository weatherForecastDslRepository;

    private final Gson gson = new Gson();

    @Transactional
    public boolean procNowForecast(long projectId) throws Exception {
        LocalDateTime baseDateTime = getBaseDateTime(WeatherForecastType.NOWFORECAST);

        List<WeatherForecast> existForecast = weatherForecastDslRepository.findExistForecast(projectId, baseDateTime, WeatherForecastType.NOWFORECAST);

        if (existForecast.size() > 0) {
            return true;
        }

        String url = getNowForecastURL(projectId, baseDateTime);
        if (Strings.isBlank(url)) {
            return true;
        }

        Map<String, Object> result = getAPIResponse(url);
        if ("false".equals(result.get("isSuccess"))) {
            // todo
        } else {
            saveWeatherForecast(new WeatherForecastVO(
                    projectId,
                    (List<?>) result.get("data"),
                    baseDateTime,
                    WeatherForecastType.NOWFORECAST
            ));
        }
        return true;
    }

    private String getNowForecastURL(long projectId, LocalDateTime baseDateTime) {
        Map<String, Object> nxnyMap = getProjectnXnY(projectId);
        if (nxnyMap == null) {
            return null;
        }

        MultiValueMap<String, String> queryParams = getDefaultQueryParams();
        queryParams.add("base_date", baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        queryParams.add("base_time", baseDateTime.format(DateTimeFormatter.ofPattern("HHmm")));
        queryParams.add("nx", nxnyMap.get("nx").toString());
        queryParams.add("ny", nxnyMap.get("ny").toString());

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dataGoBaseDomain)
                .pathSegment(weatherCategory, villageForecastPath, nowForecastResource)
                .queryParams(queryParams)
                .build(false)
                .toUriString();
    }

    @Transactional
    public boolean procShortForecast(long projectId) throws Exception {
        LocalDateTime baseDateTime = getBaseDateTime(WeatherForecastType.SHORTFORECAST);

        List<WeatherForecast> existForecast = weatherForecastDslRepository.findExistForecast(projectId, baseDateTime, WeatherForecastType.SHORTFORECAST);
        if (existForecast.size() > 0) {
            return true;
        }

        String url = getShortForecastURL(projectId, baseDateTime);
        if (Strings.isBlank(url)) {
            return true;
        }

        Map<String, Object> result = getAPIResponse(url);
        if ("false".equals(result.get("isSuccess"))) {
            // todo
        } else {
            saveWeatherForecast(new WeatherForecastVO(
                    projectId,
                    (List<?>) result.get("data"),
                    baseDateTime,
                    WeatherForecastType.SHORTFORECAST
            ));
        }
        return true;
    }

    private String getShortForecastURL(long projectId, LocalDateTime baseDateTime) {
        Map<String, Object> nxnyMap = getProjectnXnY(projectId);
        if (nxnyMap == null) {
            return null;
        }

        MultiValueMap<String, String> queryParams = getDefaultQueryParams();
        queryParams.add("base_date", baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        queryParams.add("base_time", baseDateTime.format(DateTimeFormatter.ofPattern("HHmm")));
        queryParams.add("nx", nxnyMap.get("nx").toString());
        queryParams.add("ny", nxnyMap.get("ny").toString());

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dataGoBaseDomain)
                .pathSegment(weatherCategory, villageForecastPath, shortForecastResource)
                .queryParams(queryParams)
                .build(false)
                .toUriString();
    }

    @Transactional
    public boolean procLongWeatherForecast(long projectId) throws Exception {
        LocalDateTime baseDateTime = getBaseDateTime(WeatherForecastType.LONGWHEATHERFORECAST);

        List<WeatherForecast> existForecast = weatherForecastDslRepository.findExistForecast(projectId, baseDateTime, WeatherForecastType.LONGWHEATHERFORECAST);
        if (existForecast.size() > 0) {
            return true;
        }

        String url = getLongWeatherForecastURL(projectId, baseDateTime);
        if (Strings.isBlank(url)) {
            return true;
        }

        Map<String, Object> result = getAPIResponse(url);

        if ("false".equals(result.get("isSuccess"))) {
            // todo
        } else {
            saveWeatherForecast(new WeatherForecastVO(
                    projectId,
                    (List<?>) result.get("data"),
                    baseDateTime,
                    WeatherForecastType.LONGWHEATHERFORECAST
            ));
        }
        return true;

    }

    private String getLongWeatherForecastURL(long projectId, LocalDateTime baseDateTime) {
        MultiValueMap<String, String> queryParams = getDefaultQueryParams();
        queryParams.add("tmFc", baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

        String projectRegionId = getProjectRegionId(projectId, WeatherForecastType.LONGWHEATHERFORECAST);
        if (Strings.isBlank(projectRegionId)) {
            return null;
        }

        queryParams.add("regId", projectRegionId);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dataGoBaseDomain)
                .pathSegment(weatherCategory, midForecastPath, longWeatherForecastResource)
                .queryParams(queryParams)
                .build(false)
                .toUriString();
    }

    @Transactional
    public boolean procLongTemperatureForecast(long projectId) throws Exception {
        LocalDateTime baseDateTime = getBaseDateTime(WeatherForecastType.LONGTEMPERATUREFORECAST);

        List<WeatherForecast> existForecast = weatherForecastDslRepository.findExistForecast(projectId, baseDateTime, WeatherForecastType.LONGTEMPERATUREFORECAST);
        if (existForecast.size() > 0) {
            return true;
        }

        String url = getLongTemperatureForecastURL(projectId, baseDateTime);
        if (Strings.isBlank(url)) {
            return true;
        }

        Map<String, Object> result = getAPIResponse(url);

        if ("false".equals(result.get("isSuccess"))) {
            // todo
        } else {
            saveWeatherForecast(new WeatherForecastVO(
                    projectId,
                    (List<?>) result.get("data"),
                    baseDateTime,
                    WeatherForecastType.LONGTEMPERATUREFORECAST
            ));
        }
        return true;

    }

    private String getLongTemperatureForecastURL(long projectId, LocalDateTime baseDateTime) {
        MultiValueMap<String, String> queryParams = getDefaultQueryParams();
        queryParams.add("tmFc", baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

        String projectRegionId = getProjectRegionId(projectId, WeatherForecastType.LONGTEMPERATUREFORECAST);
        if (Strings.isBlank(projectRegionId)) {
            return null;
        }
        queryParams.add("regId", projectRegionId);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(dataGoBaseDomain)
                .pathSegment(weatherCategory, midForecastPath, longTemperatureForecastResource)
                .queryParams(queryParams)
                .build(false)
                .toUriString();
    }

    private void saveWeatherForecast(WeatherForecastVO weatherForecastVO) {
        WeatherForecast weatherForecast = new WeatherForecast();
        weatherForecast.setForecastAtAPIResponse(weatherForecastVO);
        weatherForecastRepository.save(weatherForecast);
    }

    private Map<String, Object> getAPIResponse(String spec) throws Exception {
        URL url = new URL(spec);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        Map<String, Object> result = new HashMap<>();
        try (AutoCloseable ac = conn::disconnect) {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK || !conn.getHeaderField("Content-Type").contains("json")) {
                result.put("isSuccess", "false");
            } else {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuffer sb = new StringBuffer();
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        sb.append(inputLine);
                    }

                    JsonObject jsonObject = gson.fromJson(sb.toString(), JsonObject.class);
                    ForecastAPIResponseDTO forecastAPIResponseDTO = gson.fromJson(jsonObject.get("response").getAsJsonObject(), ForecastAPIResponseDTO.class);

                    if (!"00".equals(forecastAPIResponseDTO.header.getResultCode())) {
                        result.put("isSuccess", "false");
                        result.put("resultMsg", forecastAPIResponseDTO.header.getResultMsg());
                    } else {
                        result.put("isSuccess", "true");
                        result.put("data", forecastAPIResponseDTO.body.getItems().item);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    private MultiValueMap<String, String> getDefaultQueryParams() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("serviceKey", serviceKey);
        queryParams.add("pageNo", "1");
        queryParams.add("numOfRows", "1000");
        queryParams.add("dataType", "JSON");
        return queryParams;
    }

    private LocalDateTime getBaseDateTime(WeatherForecastType type) {
        LocalDateTime dateTime = LocalDateTime.now().withSecond(0).withNano(0);

        int currentHour = dateTime.getHour();
        int currentMinute = dateTime.getMinute();

        if (type.equals(WeatherForecastType.NOWFORECAST)) {
            if (currentMinute < 45) {
                dateTime = dateTime.minusHours(1);
            }
            dateTime = dateTime.withMinute(30);
        } else if (type.equals(WeatherForecastType.SHORTFORECAST)) {
            dateTime = dateTime.withMinute(0);
            switch (currentHour % 3) {
                case 0:
                    dateTime = dateTime.minusHours(1);
                    break;
                case 1:
                    dateTime = dateTime.minusHours(2);
                    break;
            }
        } else {
            dateTime = dateTime.withMinute(0);
            if (currentHour % 6 != 0) {
                switch (currentHour / 6) {
                    case 0:
                        dateTime = dateTime.minusDays(1).withHour(18);
                        break;
                    case 1:
                    case 2:
                        dateTime = dateTime.withHour(6);
                        break;
                    case 3:
                        dateTime = dateTime.withHour(18);
                        break;
                }
            }
        }

        return dateTime;
    }

    private Map<String, Object> getProjectnXnY(long projectId) {
        Project project = projectRepository.findById(projectId).orElseGet(Project::new);
        if (project.getId() == 0 || project.getWeatherXY() == null) {
            return null;
        }

        if (Strings.isBlank(project.getWeatherXY().getWeatherX()) || Strings.isBlank(project.getWeatherXY().getWeatherX())) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("nx", project.getWeatherXY().getWeatherX());
        map.put("ny", project.getWeatherXY().getWeatherY());

        return map;
    }

    private String getProjectRegionId(long projectId, WeatherForecastType type) {
        Project project = projectRepository.findById(projectId).orElseGet(Project::new);

        if (project.getId() == 0 || project.getWeatherXY() == null) {
            return null;
        }

        if (Strings.isBlank(project.getWeatherXY().getLongForecastRegionCode()) || Strings.isBlank(project.getWeatherXY().getLongTemperatureRegionCode())) {
            return null;
        }

        return type.equals(WeatherForecastType.LONGWHEATHERFORECAST) ?
                project.getWeatherXY().getLongForecastRegionCode()
                : project.getWeatherXY().getLongTemperatureRegionCode();
    }

    @Transactional
    public void deleteOldForecast(){
        weatherForecastDslRepository.executeDeleteOldForecast();
    }
}
