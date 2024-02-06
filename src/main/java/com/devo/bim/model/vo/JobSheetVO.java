package com.devo.bim.model.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSheetVO {
    private long id;
    private String location;
    private String reportDate;
    private String title;
    private String contents;
    private String temperatureMax;
    private String temperatureMin;
    private String rainfallAmount;
    private String snowfallAmount;
    private String status;
    private long grantorId;
    private List<Long> referencesIds;
    private List<Long> mySnapShotIds;
    private List<Long> fileIds;
    private List<Long> processItemIds;
    private List<String> phasingCodes;
    private List<String> taskFullPaths;
    private List<BigDecimal> beforeProgressRates;
    private List<BigDecimal> afterProgressRates;
    private List<BigDecimal> todayProgressRates;
    private List<Long> beforeProgressAmounts;
    private List<Long> afterProgressAmounts;
    private List<Long> todayProgressAmounts;
    private List<List<Object>> processItemWorkers;
    private List<List<Object>> processItemDevices;
    private String todayContents;
    private List<String> exchangeIds;
    //private List<Integer> mySnapShotIds;
    //private List<String> mySnapShotSource;
}
