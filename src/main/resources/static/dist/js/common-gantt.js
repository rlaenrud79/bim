let _simulationMarkerId;

let updateSimulationMarker = function(gantt, simulationDate, taskId) {
  if(typeof(_simulationMarkerId) === "undefined") _simulationMarkerId = simulationMarker(gantt, simulationDate);
  gantt.getMarker(_simulationMarkerId).start_date = simulationDate;
  gantt.updateMarker(_simulationMarkerId);
  gantt.showDate(simulationDate);

  if(typeof(taskId) === 'undefined' || taskId === '') return false;

  let tmpTask = gantt.getTask(taskId);
  if(tmpTask.start_date && tmpTask.end_date) {
    let sizes = gantt.getTaskPosition(tmpTask, tmpTask.start_date, tmpTask.end_date);
    gantt.scrollTo(null, sizes.top);
  }
}

// region today marker
let todayMarker = function(gantt) {
  return gantt.addMarker({
    start_date: new Date(),
    css: "today_marker_line",
    text: "Today",
    title: "Today"
  });
}

let simulationMarker = function(gantt, simulationDate) {
  return gantt.addMarker({
    start_date: simulationDate,
    css: "simulation_marker_line",
    text: "Simulation",
    title: "Simulation"
  });
}

// endregion today marker

// region gantt calendar setting

let _holidays = [];
let _allWorksCalendar = {};

let pushAsWorkIdAndHolidays = function(item) {
  let tmpHoliday = {};
  tmpHoliday.workId = item.workId;
  tmpHoliday.holidays = item.holidays;
  _holidays.push(tmpHoliday);
}

let addCalendarForAllWorks = function (gantt, item) {
  return gantt.addCalendar({
    id: item.workId,
    workTime: {
      days: [1, 1, 1, 1, 1, 1, 1]
    }
  });
}

let addCalendarForUnitWork = function(gantt, item) {
  return gantt.addCalendar({
    id: item.workId
  });
}

let setWorkTimeForWorks = function(gantt, item, calendarObject) {
  let tmpHolidays = item.holidays.split("||");
  for (let i = 0; i < tmpHolidays.length; i++) {
    calendarObject.setWorkTime({date: new Date(tmpHolidays[i]), hours: false});
  }
}

let initHoliday = function(gantt) {
  $.each(_holidayClassifiedWorkDTOs, function (index, item) {

    pushAsWorkIdAndHolidays(item);

    if (item.workId === "0") {

      let allWorkCalendarId = addCalendarForAllWorks(gantt, item);
      _allWorksCalendar = gantt.getCalendar(allWorkCalendarId);

      setWorkTimeForWorks(gantt, item, _allWorksCalendar);
    }
  });

  $.each(_holidays, function (index, item) {
    if (item.workId === "0") return true;

    let ganttCalendarId = addCalendarForUnitWork(gantt, item);
    let ganttCalendar = gantt.getCalendar(ganttCalendarId);
    ganttCalendar._worktime.dates = _allWorksCalendar._worktime.dates;
    setWorkTimeForWorks(gantt, item, ganttCalendar);
  });

}
// endregion gantt calendar setting

// region basic function
let showDate = function (gantt, date) {
  gantt.showDate(getDateFormat(date));
};

let getGanttYearMonthLocaleFormat = function (_locale) {
  if (_locale === "en") return "%m/%Y";
  if (_locale === "zh") return "%Y-%m";
  return "%Y-%m";
}

let getGanttMonthDayLocaleFormat = function (_locale) {
  if (_locale === "en") return "%d/%m";
  if (_locale === "zh") return "%m/%d";
  return "%m/%d";
}

let getReturnLocaleWeekFormat = function (_locale, weekNum, start, end, year) {
  if (_locale === "en") return weekNum + " week, " + start + " - " + end + ", " + year;
  if (_locale === "zh") return year + ", " + start + " - " + end + ", " + weekNum + " week";
  return year + ", " + start + " - " + end + ", " + weekNum + " week";
}

let getGanttLocale = function (_locale) {
  if (_locale === "en") return "en";
  if (_locale === "zh") return "cn";
  return "kr";
}

let getGanttConfigDateFormat = function(_locale){
  if (_locale === "en") return "%m-%d-%Y";
  if (_locale === "zh") return "%Y-%m-%d";
  return "%Y-%m-%d";
}

let showGeneralDataLoadingProc = function () {
  $('#generalDataLoadingProc').removeClass('display-none');
}

let hideGeneralDataLoadingProc = function () {
  $('#generalDataLoadingProc').addClass('display-none');
}

// endregion basic function

// region zoom config

let zoomConfig = function (gantt, _locale ) {

  return {
    levels: [
      {
        name: "day",
        scale_height: 50,
        min_column_width: 60,
        scales: [
          {
            unit: "week", step: 1, format: function (date) {
              var dateToStr = gantt.date.date_to_str(getGanttMonthDayLocaleFormat(_locale));
              var endDate = gantt.date.add(date, 6, "day");
              var weekNum = gantt.date.date_to_str("%W")(date);
              var yearNum = gantt.date.date_to_str("%Y")(date);

              return getReturnLocaleWeekFormat(_locale, weekNum, dateToStr(date), dateToStr(endDate), yearNum);
            }
          },
          {unit: "day", step: 1, format: "%j, %D"}
        ]
      },
      {
        name: "month",
        scale_height: 50,
        min_column_width: 60,
        scales: [
          {unit: "month", step: 1, format: getGanttYearMonthLocaleFormat(_locale)},
          {unit: "day", step: 1, format: "%j, %D"}
        ]
      },
      {
        name: "quarter",
        height: 50,
        min_column_width: 60,
        scales: [
          {
            unit: "quarter", step: 1, format: function (date) {

              var dateToStr = gantt.date.date_to_str(getGanttYearMonthLocaleFormat(_locale));
              var endDate = gantt.date.add(gantt.date.add(date, 3, "month"), -1, "day");

              return dateToStr(date) + " - " + dateToStr(endDate);
            }
          },
          {unit: "month", step: 1, format: "%M"}
        ]
      },
      {
        name: "year",
        scale_height: 50,
        min_column_width: 60,
        scales: [
          {unit: "year", step: 1, format: "%Y"},
          {
            unit: "quarter", step: 1, format: function (date) {
              var dateToStr = gantt.date.date_to_str("%M");
              var endDate = gantt.date.add(gantt.date.add(date, 3, "month"), -1, "day");
              return dateToStr(date) + " - " + dateToStr(endDate);
            }
          }
        ]
      }
    ]
  }
}

// endregion zoom config