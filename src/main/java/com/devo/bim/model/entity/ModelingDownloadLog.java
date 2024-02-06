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
public class ModelingDownloadLog {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String fileType;

    @ManyToOne(fetch = LAZY)
    private Modeling modeling;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public ModelingDownloadLog(String fileType, long modelingId, long writerId){
        this.fileType = fileType;
        this.modeling = new Modeling(modelingId);
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

}
