package com.devo.bim.model.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SearchCompanyVO {

    @NotNull
    private long projectId = 0L;

    private long workId = 0L;
    private String companyName = "";

    private String sortProp = "";
}
