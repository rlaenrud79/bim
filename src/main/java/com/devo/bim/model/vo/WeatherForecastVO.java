package com.devo.bim.model.vo;

import com.devo.bim.model.enumulator.WeatherForecastType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class WeatherForecastVO {
    private long projectId;
    private List<?> forecastItems;
    private LocalDateTime baseDateTime;
    private WeatherForecastType requestType;

    public WeatherForecastVO(long projectId, List<?> forecastItems, LocalDateTime baseDateTime, WeatherForecastType requestType) {
        this.projectId = projectId;
        this.forecastItems = forecastItems;
        this.baseDateTime = baseDateTime;
        this.requestType = requestType;
    }
}
