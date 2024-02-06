package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Chatting {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_id")
    private CoWork coWork;

    private String teamName;
    private String roomId;

    @OneToMany(fetch = LAZY, mappedBy = "chatting")
    private List<ChattingJoiner> chattingJoiners = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "chatting")
    private List<ChattingLog> chattingLogs = new ArrayList<>();

    @Embedded
    private WriteEmbedded writeEmbedded;

    public void setChatting(CoWork coWork, String teamName, UserInfo userInfo, String roomId) {
        this.coWork = coWork;
        this.teamName = teamName;
        this.projectId = coWork.getProjectId();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.roomId = roomId;
    }
}
