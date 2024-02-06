package com.devo.bim.model.enumulator;

public enum WeatherForecastType {
    NOWFORECAST("초단기예보"),
    SHORTFORECAST("단기예보"),
    LONGWHEATHERFORECAST("중기날씨예보"),
    LONGTEMPERATUREFORECAST("중기기온예보");

    private final String value;

    WeatherForecastType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public enum NowForecastCategory {
        T1H("기온"),
        SKY("하늘상태"),
        PTY("강수형태"),
        VEC("풍향"),
        WSD("풍속"),
        REH("습도");

        private final String value;

        NowForecastCategory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum ShortDaysForecastCategory {
        SKY("하늘상태"),
        PTY("강수형태"),
        TMN("일최저기온"),
        TMX("일최고기온");

        private final String value;

        ShortDaysForecastCategory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ShortPeriodForecastCategory {
        SKY("하늘상태"),
        PTY("강수형태"),
        TMP("1시간기온"),
        TMN("일최저기온"),
        TMX("일최고기온"),
        PCP("1시간강수량"),
        SNO("1시간신적설");

        private final String value;

        ShortPeriodForecastCategory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
