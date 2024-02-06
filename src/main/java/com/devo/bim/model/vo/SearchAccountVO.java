package com.devo.bim.model.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchAccountVO {
    private long roleId;
    private long workId;
    private long companyId;

    private String searchType;
    private String searchText;

    private String sortProp = "";
}