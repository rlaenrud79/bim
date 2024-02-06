package com.devo.bim.component;

import com.devo.bim.model.dto.VmGisungProcessItemDTO;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.enumulator.RoleCode;
import com.devo.bim.model.vo.CalendarDayVO;
import com.devo.bim.service.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@Component
@RequiredArgsConstructor
public class Utils {

    private static String _DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN_SEC;
    private static String _DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN;
    private static String _DATE_FORMAT_KR_YEAR_MONTH_DAY;
    private static String _DATE_FORMAT_KR_GANTT;

    private static String _DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN_SEC;
    private static String _DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN;
    private static String _DATE_FORMAT_US_YEAR_MONTH_DAY;
    private static String _DATE_FORMAT_US_GANTT;

    private static String _DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN_SEC;
    private static String _DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN;
    private static String _DATE_FORMAT_CN_YEAR_MONTH_DAY;
    private static String _DATE_FORMAT_CN_GANTT;

    private static BigDecimal defaultDecimal = new BigDecimal("0.00");
    private static BigDecimal defaultDecimalError = new BigDecimal("-1.00");
    private static BigDecimal persentageMultiple = new BigDecimal("100");

    public static  BigDecimal getDefaultDecimal(){
        return defaultDecimal;
    }
    public static  BigDecimal getDefaultDecimalError(){
        return defaultDecimalError;
    }
    public static  BigDecimal getPersentageMultiple(){
        return persentageMultiple;
    }

    @Autowired
    private Utils(ConfigService configService){
        this._DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN_SEC = configService.getDateFormatter(Locale.KOREA, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
        this._DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN = configService.getDateFormatter(Locale.KOREA, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN);
        this._DATE_FORMAT_KR_YEAR_MONTH_DAY = configService.getDateFormatter(Locale.KOREA, DateFormatEnum.YEAR_MONTH_DAY);
        this._DATE_FORMAT_KR_GANTT = configService.getDateFormatter(Locale.KOREA, DateFormatEnum.GANTT);

        this._DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN_SEC = configService.getDateFormatter(Locale.ENGLISH, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
        this._DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN = configService.getDateFormatter(Locale.ENGLISH, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN);
        this._DATE_FORMAT_US_YEAR_MONTH_DAY = configService.getDateFormatter(Locale.ENGLISH, DateFormatEnum.YEAR_MONTH_DAY);
        this._DATE_FORMAT_US_GANTT = configService.getDateFormatter(Locale.ENGLISH, DateFormatEnum.GANTT);

        this._DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN_SEC = configService.getDateFormatter(Locale.CHINESE, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC);
        this._DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN = configService.getDateFormatter(Locale.CHINESE, DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN);
        this._DATE_FORMAT_CN_YEAR_MONTH_DAY = configService.getDateFormatter(Locale.CHINESE, DateFormatEnum.YEAR_MONTH_DAY);
        this._DATE_FORMAT_CN_GANTT = configService.getDateFormatter(Locale.CHINESE, DateFormatEnum.GANTT);
    }

    public static boolean isImage(String fileName) {
        List<String> imageExtensions = new ArrayList<>();
        imageExtensions.add("png");
        imageExtensions.add("jpg");
        imageExtensions.add("gif");
        imageExtensions.add("bmp");
        imageExtensions.add("jpeg");
        imageExtensions.add("svg");

        return imageExtensions.contains(getFileExtName(fileName).toLowerCase());
    }

    public static String getIssuePriorityCode(int priority) {
        if (priority == 10) return "TOP";
        if (priority == 20) return "HIGH";
        if (priority == 30) return "MIDDLE";
        return "LOW";
    }

    public static String getRandomString(int len) {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        int idx = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            idx = (int) (charSet.length * Math.random());
            sb.append(charSet[idx]);
        }

        return sb.toString();
    }

    public static String getDateTimeByNationAndFormatType(LocalDateTime dateTime, DateFormatEnum dateFormatEnum) {
        if (dateTime == null) return "-";

        if (dateFormatEnum == DateFormatEnum.YEAR) return dateTime.format(DateTimeFormatter.ofPattern("YYYY"));
        if (dateFormatEnum == DateFormatEnum.MONTH) return dateTime.format(DateTimeFormatter.ofPattern("MM"));
        if (dateFormatEnum == DateFormatEnum.DAY) return dateTime.format(DateTimeFormatter.ofPattern("dd"));
        if (dateFormatEnum == DateFormatEnum.HOUR) return (dateTime.getHour() < 10 ? "0" : "") + dateTime.getHour();
        if (dateFormatEnum == DateFormatEnum.MIN) return (dateTime.getMinute() < 10 ? "0" : "") + dateTime.getMinute();
        if (dateFormatEnum == DateFormatEnum.SEC) return (dateTime.getSecond() < 10 ? "0" : "") + dateTime.getSecond();

        if (dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC){
            if (LocaleContextHolder.getLocale().equals(Locale.KOREAN)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN_SEC));
            if (LocaleContextHolder.getLocale().equals(Locale.ENGLISH)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN_SEC));
            if (LocaleContextHolder.getLocale().equals(Locale.CHINESE)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN_SEC));
        }

        if (dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN){
            if (LocaleContextHolder.getLocale().equals(Locale.KOREAN)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN));
            if (LocaleContextHolder.getLocale().equals(Locale.ENGLISH)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN));
            if (LocaleContextHolder.getLocale().equals(Locale.CHINESE)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN));
        }

        if (dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY){
            if (LocaleContextHolder.getLocale().equals(Locale.KOREAN)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_KR_YEAR_MONTH_DAY));
            if (LocaleContextHolder.getLocale().equals(Locale.ENGLISH)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_US_YEAR_MONTH_DAY));
            if (LocaleContextHolder.getLocale().equals(Locale.CHINESE)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_CN_YEAR_MONTH_DAY));
        }

        if (dateFormatEnum == DateFormatEnum.GANTT){
            if (LocaleContextHolder.getLocale().equals(Locale.KOREAN)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_KR_GANTT));
            if (LocaleContextHolder.getLocale().equals(Locale.ENGLISH)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_US_GANTT));
            if (LocaleContextHolder.getLocale().equals(Locale.CHINESE)) return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_CN_GANTT));
        }
        return dateTime.format(DateTimeFormatter.ofPattern(_DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN_SEC));
    }

    public static List<String> getStringList(String separatedString, String separator) {
        return new ArrayList(Arrays.asList(separatedString.split(separator)));
    }

    public static List<Long> getLongList(String separatedString, String separator) {

        List<Long> longs = new ArrayList<>();
        if(separatedString.isBlank()) return longs;
        for (String string : getStringList(separatedString, separator)) {
            longs.add(Long.parseLong(string));
        }
        return longs;
    }

    public static String getJsonString(List<String> strings) {
        int idx = 0;
        String jsonString = "";
        for (String value : strings) {
            if (idx == 0) jsonString += getNameValueString(idx,value);
            else jsonString += "," + getNameValueString(idx,value);
            ++idx;
        }

        return "{" + jsonString + "}";
    }

    public static String getNameValueString(int name, String value)
    {
        return "\""+name+"\":\""+value+"\"";
    }

    public static FileDownloadUIType getFileDownloadUITypeEnum(String fileDownloadUIType) {
        try {
            return FileDownloadUIType.valueOf(fileDownloadUIType);
        } catch (Exception ex) {
            return FileDownloadUIType.NONE;
        }
    }

    public static RoleCode getRoleCodeEnum(String roleCode) {
        try {
            return RoleCode.valueOf(roleCode);
        } catch (Exception ex) {
            return RoleCode.ROLE_USER_NORMAL;
        }
    }

    public static String getBaseFolderName(String basePathName) {
        return basePathName.substring(basePathName.lastIndexOf("/") + 1);
    }

    public static String getFileNameWithOutExt(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".")).replace(" ", "_");
    }

    public static String getFileExtName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    public static String getRequestMappingValue(String requestURI) {
        return requestURI.substring(requestURI.lastIndexOf("/") + 1);
    }

    @NotNull
    public static String getSaveFileNameDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(new Date());
    }

    public static String getSaveBasePath(String winPathUpload, String linuxPathUpload, String macPathUpload) {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("win")) return winPathUpload;
        if (osName.toLowerCase().contains("mac")) return macPathUpload;
        return linuxPathUpload;
    }

    public static String getDownloadBasePath(String winPathUpload, String linuxPathUpload, String macPathUpload) {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("win")) return winPathUpload.substring(0, winPathUpload.lastIndexOf("/"));
        if (osName.toLowerCase().contains("mac")) return macPathUpload.substring(0, macPathUpload.lastIndexOf("/"));
        return linuxPathUpload.substring(0, linuxPathUpload.lastIndexOf("/"));
    }

    public static String getPhysicalFilePath(String winPathUpload, String linuxPathUpload, String macPathUpload, String filePathAtDB){
        return getSaveBasePath(winPathUpload, linuxPathUpload, macPathUpload) + filePathAtDB.replace("/file_upload", "");
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static LocalDateTime parseLocalDateTimeEnd(String date) {
        if (date.isBlank()) return null;
        return LocalDateTime.parse(date.replace(".","-") + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime parseLocalDateTimeStart(String date) {
        if (date.isBlank()) return null;
        return LocalDateTime.parse(date.replace(".","-") + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static BigDecimal getFileSizeMegaByteUnit(BigDecimal fileSize){
        BigDecimal denominator = new BigDecimal("1000000");
        return fileSize.divide(denominator, 2, RoundingMode.HALF_EVEN);
    }

    public static boolean isTotalWorksSelected(List<Work> works, int itemWorksSize) {
        return works.size() == itemWorksSize;
    }

    public static String todayString()
    {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String todayPlusDayString(long day)
    {
        return LocalDate.now().plusDays(day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String todayMinusDayString(long day)
    {
        return LocalDate.now().minusDays(day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String todayPlusMonthString(long month)
    {
        return LocalDate.now().plusMonths(month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String todayMinusMonthString(long month)
    {
        return LocalDate.now().minusMonths(month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String isEmpty(String value, String defaultValue)
    {
        if(StringUtils.isEmpty(value)) return defaultValue;
        return value;
    }

    public static BigDecimal isEmpty(BigDecimal value)
    {
        if(value == null) return defaultDecimal;
        return value;
    }

    public static int toPercentage(BigDecimal value)
    {
        if(value == null) return 0;
        return value.multiply(persentageMultiple).intValue();
    }

    public static String convertFirstCharacterToUpperCase(String text)
    {
        if(text.isBlank()) return text;
        return (text.charAt(0)+"").toUpperCase(Locale.ROOT) + text.substring(1);
    }

    /**
     * 금액 형식으로 변환
     *
     * @param value
     * @return 변환결과
     */
    public static String num2String(int value){
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("#,##0");
        return df.format(value);
    }

    /**
     * 금액 형식으로 변환
     *
     * @param value
     * @return 변환결과
     */
    public static String num2String(long value){
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("#,##0");
        return df.format(value);
    }

    /**
     * 금액 형식으로 변환
     *
     * @param value
     * @return 변환결과
     */
    public static String num2String(String value){
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("#,##0");
        return df.format(value);
    }

    /**
     * 날짜 형식 변환
     * @param dateStr
     * @return
     */
    public static String convertDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() < 10) return "";
            LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
            return date.format(formatter);
        } catch (DateTimeException e) {
            if (dateStr == null || dateStr.length() < 10) return "";
            return dateStr.substring(0, 4) + "년 " + dateStr.substring(5, 7) + "월 " + dateStr.substring(8, 10) + "일";
        }
    }

    public static String convertDateWithDayOfWeek(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() < 10) return "";

            LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN);
            return date.format(formatter);
        } catch (Exception e) {
            if (dateStr == null || dateStr.length() < 10) return "";
            return dateStr.substring(0, 4) + "년 " + dateStr.substring(5, 7) + "월 " + dateStr.substring(8, 10) + "일";
        }
    }

    public static String convertDate2(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() < 10) return "";
            LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
            return date.format(formatter);
        } catch (DateTimeException e) {
            if (dateStr == null || dateStr.length() < 10) return "";
            return dateStr.substring(0, 4) + ". " + dateStr.substring(5, 7) + ". " + dateStr.substring(8, 10) + ".";
        }
    }

    public static String getJsonListString(List<WorkDTO> workDTOs) {
        List<WorkDTO> jsonWorkDTOs = new ArrayList<>();
        for (WorkDTO workDTO : workDTOs) {
            WorkDTO jsonWorkDTO = new WorkDTO(workDTO.getId(), workDTO.getProjectId(), workDTO.getWorkName(), workDTO.getWorkNameLocale(), workDTO.getSortNo(), workDTO.getStatus(), workDTO.getUpId());
            jsonWorkDTOs.add(jsonWorkDTO);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(jsonWorkDTOs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static String numberToLomaNumber(int bun){
        String[] num = {"Ⅰ","Ⅱ","Ⅲ","Ⅳ","Ⅴ","Ⅵ","Ⅶ","Ⅷ","Ⅸ","Ⅹ"};
        return num[bun];
    }

    // 문자열 반복하는 함수
    public static String strZeroRepeat(String str, int repeatCount) {
        return str.repeat(repeatCount);
    }

    public static String getChartDataJson(String title, String code, List<String> series, List<List<Long>> data, List<String> categories) {
        // Create JSON structure
        JsonObject js = new JsonObject();
        js.addProperty("title", title);
        js.addProperty("id", code);

        // Convert data list to JSON array
        JsonArray dataArray = new JsonArray();
        JsonArray transformedData = new JsonArray();
        for (List<Long> dataList : data) {
            JsonObject seriesObject = new JsonObject();
            seriesObject.addProperty("name", series.get(data.indexOf(dataList)));

            JsonArray innerArray = new JsonArray();
            for (Long value : dataList) {
                BigDecimal valueInBigDecimal = new BigDecimal(value);
                // 소수점 이하를 0자리로 설정하고, 반올림
                BigDecimal roundedAmount = valueInBigDecimal.divide(new BigDecimal("10000"), RoundingMode.HALF_UP);
                innerArray.add(roundedAmount);
            }
            seriesObject.add("data", innerArray);
            transformedData.add(seriesObject);
        }

        JsonArray categoriesArray = new JsonArray();
        for (String category : categories) {
            categoriesArray.add(category);
        }

        js.add("data", transformedData);
        js.add("categories", categoriesArray);

        JsonArray chartArray = new JsonArray();
        chartArray.add(js);

        return chartArray.toString();
    }

    public static List<CalendarDayVO> generateDays(int year, int month) {
        List<CalendarDayVO> days = new ArrayList<>();
        CalendarDayVO calendarDayVO = new CalendarDayVO();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= lastDay; day++) {
            calendarDayVO = new CalendarDayVO();
            calendarDayVO.setDay(day);

            // 줄 바꾸기
            if ((firstDayOfWeek + day - 1) % 7 == 0) {
                calendarDayVO.setNewline("Y");
            } else {
                calendarDayVO.setNewline("N");
            }
            days.add(calendarDayVO);
        }

        // 나머지 빈 셀 채우기
        while ((firstDayOfWeek + lastDay - 1) % 7 != 0) {
            calendarDayVO = new CalendarDayVO();
            calendarDayVO.setDay(0);
            calendarDayVO.setNewline("N");
            days.add(calendarDayVO);
            lastDay++;
        }

        return days;
    }

    public static LocalDateTime getPreviousDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate previousDate = date.minusDays(1);
        return previousDate.atStartOfDay();
    }

    public static LocalDateTime getNextDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate nextDate = date.plusDays(1);
        return nextDate.atStartOfDay();
    }
}
