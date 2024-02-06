package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkModeling {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "modeling_id")
    private Modeling modeling;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_id")
    private CoWork coWork;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public CoWorkModeling(long modelingId, long coWorkId, long writerId)
    {
        this.modeling = new Modeling(modelingId);
        this.coWork = new CoWork(coWorkId);
        this.writeEmbedded = new WriteEmbedded(writerId);
    }
}
