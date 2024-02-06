package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortPeriodForecastDTO {
    private String fcstDate;
    private String fcstTime;

    private String tmp;
    private String sky;
    private String pty;
    private String tmn;
    private String tmx;
    private String pcp;
    private String sno;

    public ShortPeriodForecastDTO(String fcstDate, String fcstTime, String tmp, String sky, String pty, String tmn, String tmx, String pcp, String sno) {
        this.fcstDate = fcstDate;
        this.fcstTime = fcstTime;
        this.tmp = tmp;
        this.sky = sky;
        this.pty = pty;
        this.tmn = tmn;
        this.tmx = tmx;
        this.pcp = pcp;
        this.sno = sno;
    }

    public String getSkyStatus() {

        String icon = "";

        boolean isDayTime = 600 <= Integer.parseInt(this.fcstTime) && Integer.parseInt(this.fcstTime) < 1800;

        // 강수형태로 구분
        if ("0".equals(this.pty)) {
            // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
            if (isDayTime) {
                // 낮
                switch (this.sky) {
                    case "1":
                        icon = "wico-sunny";
                        break;
                    case "3":
                        icon = "wico-clouds";
                        break;
                    case "4":
                        icon = "wico-cloudy";
                        break;
                }
            } else {
                // 밤
                switch (this.sky) {
                    case "1":
                        icon = "wico-night";
                        break;
                    case "3":
                        icon = "wico-n-clouds";
                        break;
                    case "4":
                        icon = "wico-n-cloudy";
                        break;
                }
            }

        } else {
            // 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            switch (this.pty) {
                case "1":
                    icon = "wico-cloudy-rain";
                    break;
                case "2":
                    icon = "wico-cloudy-rainsnow";
                    break;
                case "3":
                    icon = "wico-cloudy-snow";
                    break;
                case "4":
                    icon = "wico-cloudy-rainshower";
                    break;
            }
        }

        return icon;
    }

    public String getTimeString() {
        int calc = Integer.parseInt(this.fcstTime.substring(0,2)) % 24;
        return Integer.toString(calc);
    }
}
