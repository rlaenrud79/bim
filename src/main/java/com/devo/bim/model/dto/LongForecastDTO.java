package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@NoArgsConstructor
public class LongForecastDTO {

    // 날짜 // (계산:요일
    // 최고기온
    // 최저기온
    // 하늘상태
    private LocalDateTime day3;
    private String wf3Am;
    private String wf3Pm;
    private int taMin3;
    private int taMax3;

    private LocalDateTime day4;
    private String wf4Am;
    private String wf4Pm;
    private int taMin4;
    private int taMax4;

    private LocalDateTime day5;
    private String wf5Am;
    private String wf5Pm;
    private int taMin5;
    private int taMax5;

    private LocalDateTime day6;
    private String wf6Am;
    private String wf6Pm;
    private int taMin6;
    private int taMax6;

    private LocalDateTime day7;
    private String wf7Am;
    private String wf7Pm;
    private int taMin7;
    private int taMax7;

    public LongForecastDTO(LocalDateTime day3, String wf3Am, String wf3Pm, int taMin3, int taMax3, LocalDateTime day4, String wf4Am, String wf4Pm, int taMin4, int taMax4, LocalDateTime day5, String wf5Am, String wf5Pm, int taMin5, int taMax5, LocalDateTime day6, String wf6Am, String wf6Pm, int taMin6, int taMax6, LocalDateTime day7, String wf7Am, String wf7Pm, int taMin7, int taMax7) {
        this.day3 = day3;
        this.wf3Am = wf3Am;
        this.wf3Pm = wf3Pm;
        this.taMin3 = taMin3;
        this.taMax3 = taMax3;
        this.day4 = day4;
        this.wf4Am = wf4Am;
        this.wf4Pm = wf4Pm;
        this.taMin4 = taMin4;
        this.taMax4 = taMax4;
        this.day5 = day5;
        this.wf5Am = wf5Am;
        this.wf5Pm = wf5Pm;
        this.taMin5 = taMin5;
        this.taMax5 = taMax5;
        this.day6 = day6;
        this.wf6Am = wf6Am;
        this.wf6Pm = wf6Pm;
        this.taMin6 = taMin6;
        this.taMax6 = taMax6;
        this.day7 = day7;
        this.wf7Am = wf7Am;
        this.wf7Pm = wf7Pm;
        this.taMin7 = taMin7;
        this.taMax7 = taMax7;
    }

    public String getDayOfWeek(LocalDateTime date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }

    public String getSimpleDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MM.dd"));
    }

    public String getSkyStatus(String status) {
        // 기상청 api 중기예보 목록
        // 맑음
        // 구름많음, 구름많고 비, 구름많고 눈, 구름많고 비/눈, 구름많고 소나기
        // 흐림, 흐리고 비, 흐리고 눈, 흐리고 비/눈, 흐리고 소나기
        String icon = "";
        switch (status) {
            case "맑음":
                icon = "wico-sunny";
                break;
            case "구름많음":
                icon = "wico-clouds";
                break;
            case "구름많고 비":
                icon = "wico-clouds-rain";
                break;
            case "구름많고 눈":
                icon = "wico-clouds-snow";
                break;
            case "구름많고 비/눈":
                icon = "wico-clouds-rainsnow";
                break;
            case "구름많고 소나기":
                icon = "wico-clouds-rainshower";
                break;
            case "흐림":
                icon = "wico-cloudy";
                break;
            case "흐리고 비":
                icon = "wico-cloudy-rain";
                break;
            case "흐리고 눈":
                icon = "wico-cloudy-snow";
                break;
            case "흐리고 비/눈":
                icon = "wico-cloudy-rainsnow";
                break;
            case "흐리고 소나기":
                icon = "wico-cloudy-rainshower";
                break;

        }
        return icon;
    }
}
