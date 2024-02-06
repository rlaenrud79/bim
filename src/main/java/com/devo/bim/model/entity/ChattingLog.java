package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ChattingLog {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatting_id")
    private Chatting chatting;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="co_work_joiner_id")
    private Account coWorkJoiner;

    private String mention;
    private LocalDateTime chattingDate;
}
