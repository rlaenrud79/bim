package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ChattingJoiner {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatting_id")
    private Chatting chatting;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="joinerId")
    private Account joiner;

    private LocalDateTime inviteDate;
    private boolean isOwner;

    public void setChattingJoiner(Chatting chatting, Long joinerId, boolean isOwner) {
        this.chatting = chatting;
        this.joiner = new Account(joinerId);
        this.inviteDate = LocalDateTime.now();
        this.isOwner = isOwner;
    }
}
