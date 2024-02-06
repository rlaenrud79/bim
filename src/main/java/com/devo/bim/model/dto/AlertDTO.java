package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Alert;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlertDTO {

    private boolean isSuccess;
    private Alert alert;
    private String returnMessage;

    public AlertDTO(boolean isSuccess, Alert alert){
        this.isSuccess = isSuccess;
        this.alert = alert;
    }

    public AlertDTO(boolean isSuccess, String returnMessage){
        this.isSuccess = isSuccess;
        this.returnMessage = returnMessage;
    }

    public AlertDTO(boolean isSuccess, Alert alert, String returnMessage){
        this.isSuccess = isSuccess;
        this.alert = alert;
        this.returnMessage = returnMessage;
    }
}
