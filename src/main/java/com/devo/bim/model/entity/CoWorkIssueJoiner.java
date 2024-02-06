package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkIssueJoiner {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_issue_id")
    private CoWorkIssue coWorkIssue;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="joiner_id")
    private Account joiner;

    public CoWorkIssueJoiner(CoWorkIssue coWorkIssue, long joinerId) {
        this.coWorkIssue = coWorkIssue;
        this.joiner = new Account(joinerId);
    }

    public AccountDTO getAccountDTO()
    {
        return new AccountDTO(joiner);
    }

    @Transient
    private boolean target = false;

    public CoWorkIssueJoiner setTarget(boolean target)
    {
        this.target = target;
        return this;
    }
}
