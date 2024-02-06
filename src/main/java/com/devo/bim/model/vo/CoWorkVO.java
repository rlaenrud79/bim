package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CoWorkVO {
    private long id;
    private String subject;
    private String joinerIds;
}
