package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkJoiner {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coWorkId")
    private CoWork coWork;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="joinerId")
    private Account joiner;

    private LocalDateTime assignDate;

    public CoWorkJoiner(long coWorkId, long writerId){
        this.coWork = new CoWork(coWorkId);
        this.joiner = new Account(writerId);
        this.assignDate = LocalDateTime.now();
    }

    public CoWorkJoiner(CoWork coWork, long writerId) {
        this.coWork = coWork;
        this.joiner = new Account(writerId);
        this.assignDate = LocalDateTime.now();
    }

    public AccountDTO getAccountDTO()
    {
        return new AccountDTO(joiner);
    }

    @Transient
    private boolean target = false;

    public CoWorkJoiner setTarget(boolean target)
    {
        this.target = target;
        return this;
    }
}
