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
public class ShortDayForecastDTO {
    private LocalDateTime fcstDate;

    private String AMsky;
    private String AMpty;
    private String AMtmn;
    private String PMsky;
    private String PMpty;
    private String PMtmx;

    public ShortDayForecastDTO(LocalDateTime fcstDate, String AMsky, String AMpty, String AMtmn, String PMsky, String PMpty, String PMtmx) {
        this.fcstDate = fcstDate;
        this.AMsky = AMsky;
        this.AMpty = AMpty;
        this.AMtmn = AMtmn;
        this.PMsky = PMsky;
        this.PMpty = PMpty;
        this.PMtmx = PMtmx;
    }

    public String getSkyStatus(String time, String type) {

        String icon = "";
        String text = "";

        boolean isDayTime = "PM".equals(time);

        String sky;
        String pty;

        if(!isDayTime) {
            sky = this.AMsky;
            pty = this.AMpty;
        } else {
            sky = this.PMsky;
            pty = this.PMpty;
        }

        // 강수형태로 구분
        if ("0".equals(pty)) {
            // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
            switch (sky) {
                case "1":
                    icon = "wico-sunny";
                    text = "맑음";
                    break;
                case "3":
                    icon = "wico-clouds";
                    text = "구름많음";
                    break;
                case "4":
                    icon = "wico-cloudy";
                    text = "흐림";
                    break;
            }
        } else {
            // 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            switch (pty) {
                case "1":
                    icon = "wico-cloudy-rain";
                    text = "비";
                    break;
                case "2":
                    icon = "wico-cloudy-rainsnow";
                    text = "비 또는 눈";
                    break;
                case "3":
                    icon = "wico-cloudy-snow";
                    text = "눈";
                    break;
                case "4":
                    icon = "wico-cloudy-rainshower";
                    text = "소나기";
                    break;
            }
        }
        if ("icon".equals(type)) {
            return icon;
        } else {
            return text;
        }
    }

    public String getTemperature(String temp) {
        String[] split = temp.split("\\.");
        if (split.length == 0) return temp;
        return split[0];
    }

    public String getDayOfWeek(LocalDateTime date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }

    public String getSimpleDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MM.dd"));
    }

}
