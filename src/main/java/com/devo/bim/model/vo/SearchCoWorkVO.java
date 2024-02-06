package com.devo.bim.model.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SearchCoWorkVO {
    private String subject = "";

    @NotNull
    private long writerId = 0L;
    private String writerName = "";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDateEnd;

    private String sortProp = "";
}
