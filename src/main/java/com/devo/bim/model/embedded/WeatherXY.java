package com.devo.bim.model.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class WeatherXY {
    @Column(name = "weather_x")
    private String weatherX;
    @Column(name = "weather_y")
    private String weatherY;
    private String longForecastRegionCode;
    private String longTemperatureRegionCode;

    public WeatherXY(String x, String y, String longForecastRegionCode, String longTemperatureRegionCode) {
        this.weatherX = x;
        this.weatherY = y;
        this.longForecastRegionCode = longForecastRegionCode;
        this.longTemperatureRegionCode = longTemperatureRegionCode;
    }

    public boolean isWeatherPoint() {
        return !weatherX.isBlank() && !weatherY.isBlank();
    }
}
