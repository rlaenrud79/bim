package com.devo.bim.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConfigTreeDTO {

    private long id;
    private String code;
    private int sort;
    private List<ConfigTreeSubDTO> configs;

    @Data
    public static class ConfigTreeSubDTO {
        private long id;
        private String code;
        private int sort;
    }
}
