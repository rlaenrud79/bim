package com.devo.bim.model.enumulator;

public enum CalendarDayWeek {
    SUN("일"),
    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토");

    private String value;

    CalendarDayWeek(String value) {
        this.value = value;
    }
}

