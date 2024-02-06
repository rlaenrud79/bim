package com.devo.bim.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ForecastAPIResponseDTO {
    public Header header;
    public Body body;

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private String dataType;
        private Item items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;

        public static class Item {
            public List<?> item;
        }
    }
}

