package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkSnapShot {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_id")
    private CoWork coWork;

    private int sortNo;
    private String source;
    private String title;

    private String viewPointJson;
    private String viewModelJson;
    private String viewModelId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    private boolean enabled;

    private String searchModelId;

    @OneToMany(fetch = LAZY, mappedBy = "coWorkSnapShot")
    private List<CoWorkSnapShotFile> coWorkSnapShotFiles = new ArrayList<>();
    
    public CoWorkSnapShot(CoWork coWork, MySnapShot mySnapShot) {
        this.coWork = coWork;
        this.sortNo = mySnapShot.getSortNo();
        this.source = mySnapShot.getSource();
        this.title = mySnapShot.getTitle();
        this.writeEmbedded = new WriteEmbedded(mySnapShot.getOwner().getId());
        this.enabled = true;
        this.viewPointJson = mySnapShot.getViewPointJson();
        this.viewModelJson = mySnapShot.getViewModelJson();
        this.viewModelId = mySnapShot.getViewModelId();
    }

    public void delete() {
        this.enabled = false;
    }

    public boolean isOwner(long loginAccountId){
        return this.writeEmbedded.getWriter().getId() == loginAccountId;
    }
    
    public CoWorkSnapShot(long coWorkSnapShotId) {
        this.id = coWorkSnapShotId;
    }
}
