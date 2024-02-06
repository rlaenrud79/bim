package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChattingAlertDTO {
    private long coWorkId;
    private String subject;
    private String roomId;
    private String teamName;
    private int unread;

    public ChattingAlertDTO(long coWorkId, String subject, String roomId, String teamName) {
        this.coWorkId = coWorkId;
        this.subject = subject;
        this.roomId = roomId;
        this.teamName = teamName;
    }
}
