package com.devo.bim.model.dto;

import lombok.Data;

@Data
public class NowForecastItem {
    private String baseDate;
    private String baseTime;
    private String fcstDate;
    private String fcstTime;
    private String category;
    private String fcstValue;
    private int nx;
    private int ny;
}
