package com.devo.bim.model.dto;

import com.devo.bim.component.Message;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
public class NameCountDTO {

    private String name;
    private long count;

    public NameCountDTO(String name, Long count) {
        this.name = name;
        this.count = count;
    }
}
