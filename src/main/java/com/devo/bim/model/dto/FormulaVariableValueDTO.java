package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FormulaVariableValueDTO {

    private String variable;
    private BigDecimal value;
    private String result;
    private boolean isSuccess;

    public FormulaVariableValueDTO(String variable, BigDecimal value, String result,  boolean isSuccess){
        this.variable = variable;
        this.value = value;
        this.result = result;
        this.isSuccess = isSuccess;
    }
}
