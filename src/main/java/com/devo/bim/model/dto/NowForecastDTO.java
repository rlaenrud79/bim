package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@NoArgsConstructor
public class NowForecastDTO {
    private String fcstDate;
    private String fcstTime;

    private LocalDateTime displayBaseTime;

    private String t1h;
    private String sky;
    private String pty;
    private String vec;
    private String wsd;
    private String reh;

    public NowForecastDTO(String fcstDate, String fcstTime, LocalDateTime displayBaseTime, String t1h, String sky, String pty, String vec, String wsd, String reh) {
        this.fcstDate = fcstDate;
        this.fcstTime = fcstTime;
        this.displayBaseTime = displayBaseTime;
        this.t1h = t1h;
        this.sky = sky;
        this.pty = pty;
        this.vec = vec;
        this.wsd = wsd;
        this.reh = reh;
    }

    public String getSkyStatus(String type) {

        String icon = "";
        String text = "";
        boolean isDayTime = 600 <= Integer.parseInt(this.fcstTime) && Integer.parseInt(this.fcstTime) < 1800;

        if ("0".equals(this.pty)) {
            // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
            if (isDayTime) {
                switch (this.sky) {
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
                switch (this.sky) {
                    case "1":
                        icon = "wico-night";
                        text = "맑음";
                        break;
                    case "3":
                        icon = "wico-n-clouds";
                        text = "구름많음";
                        break;
                    case "4":
                        icon = "wico-n-cloudy";
                        text = "흐림";
                        break;
                }
            }

        } else {
            // 강수형태(PTY) 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
            switch (this.pty) {
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
                case "5":
                    icon = "wico-clouds-rain";
                    text = "빗방울";
                    break;
                case "6":
                    icon = "wico-clouds-rainsnow";
                    text = "가끔 비 또는 눈";
                    break;
                case "7":
                    icon = "wico-clouds-snow";
                    text = "눈날림";
                    break;
            }
        }
        if ("icon".equals(type)) {
            return icon;
        } else {
            return text;
        }
    }

    public String getWindVector() {
        // (풍향값 + 22.5 * 0.5) / 22.5) = 변환값(소수점 이하 버림)
        double vector = Double.parseDouble(this.vec);
        int calcVector = (int) ((vector + 22.5 * 0.5) / 22.5);

        // 0: 북
        // 1~3: 북동
        // 4: 동
        // 5~7: 남동
        // 8 남
        // 9~11: 남서
        // 12: 서
        // 13~15: 북서
        // 16: 북
        String text = "";
        switch (calcVector) {
            case 1:
            case 2:
            case 3:
                text = "북동";
                break;
            case 4:
                text = "동";
                break;
            case 5:
            case 6:
            case 7:
                text = "남동";
                break;
            case 8:
                text = "남";
                break;
            case 9:
            case 10:
            case 11:
                text = "남서";
                break;
            case 12:
                text = "서";
                break;
            case 13:
            case 14:
            case 15:
                text = "북서";
                break;
            default:
                text = "북";
        }
        return text;
    }

    public String getBaseTime() {
        LocalDateTime dateTime = this.displayBaseTime;
        String date = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String dayOfWeek = dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        String hour = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return  date + " " + "(" + dayOfWeek + ")" + " " + hour;
    }
}
