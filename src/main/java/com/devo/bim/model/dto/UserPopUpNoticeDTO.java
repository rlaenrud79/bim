package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Alert;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPopUpNoticeDTO {

    private long alertId;
    private String title;
    private String contents;
    private String path;
    private long refId;
    private String writeDate;

    public UserPopUpNoticeDTO(Alert alert, String contents){
        this.alertId = alert.getId();
        this.title = alert.getTitle();
        this.contents = contents;
        this.path = alert.getPath();
        this.refId = alert.getRefId();
        this.writeDate = alert.getWriteDate().toString();
    }
}
