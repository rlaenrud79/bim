package com.devo.bim.model.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchIssueVO {

    @NotNull
    private long projectId = 0L;

    private String searchType = "";
    private String searchText = "";

    @NotNull
    private long writerId = 0L;
    private String writerName = "";

    private String searchDateType = "WRITE_DATE";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateEnd;

    private String sortProp = "";
}
