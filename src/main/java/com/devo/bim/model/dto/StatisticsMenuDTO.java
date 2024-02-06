package com.devo.bim.model.dto;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StatisticsMenuDTO {

    private String startDate;
    private String endDate;

    List<NameCountDTO> nameCountDTOs = new ArrayList<>();

    public StatisticsMenuDTO(String startDate, String endDate, List<NameCountDTO> nameCountDTOs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.nameCountDTOs.addAll(nameCountDTOs);
    }

    public String getData()
    {
        Type listType = new TypeToken<List<DateCountDTO>>() {}.getType();
        return new Gson().toJson(nameCountDTOs, listType);
    }

    public long getMax()
    {
        return nameCountDTOs.stream().mapToLong(x -> x.getCount()).max().orElseGet(() -> 0L);
    }
}
