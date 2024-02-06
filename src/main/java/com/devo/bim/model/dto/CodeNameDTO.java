package com.devo.bim.model.dto;

import com.devo.bim.model.enumulator.RoleCode;
import lombok.Getter;

@Getter
public class CodeNameDTO {
    private String code;
    private String name;

    public CodeNameDTO(RoleCode code, String localeName) {
        this.code = code.name();
        this.name = localeName;
    }
}
