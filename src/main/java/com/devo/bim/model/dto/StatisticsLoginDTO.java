package com.devo.bim.model.dto;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StatisticsLoginDTO {

    private String startDate;
    private String endDate;

    List<DateCountDTO> dateCounts = new ArrayList<>();

    public StatisticsLoginDTO(String startDate, String endDate, List<DateCountDTO> dateCountDTOs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateCounts.addAll(dateCountDTOs);
    }

    public String getData()
    {
        Type listType = new TypeToken<List<DateCountDTO>>() {}.getType();
        return new Gson().toJson(dateCounts, listType);
    }

    public long getMax()
    {
        return dateCounts.stream().mapToLong(x -> x.getCount()).max().orElseGet(() -> 0L);
    }
}
