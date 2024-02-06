package com.devo.bim.model.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SearchNoticeVO {

    @NotNull
    private long projectId = 0L;

    private String searchType = "";
    private String searchText = "";

    @NotNull
    private long writerId = 0L;
    private String writerName = "";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateEnd;

    private String sortProp = "";
}
