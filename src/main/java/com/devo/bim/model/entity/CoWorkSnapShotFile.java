package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkSnapShotFile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_snap_shot_id")
    private CoWorkSnapShot coWorkSnapShot;

    private int sortNo;
    private BigDecimal size;
    private String originFileName;
    private String filePath;

    @Embedded
    private WriteEmbedded writeEmbedded;
    
    public CoWorkSnapShotFile(CoWorkSnapShot coWorkSnapShot, MySnapShotFile mySnapShotFile) {
        this.coWorkSnapShot = coWorkSnapShot;
        this.originFileName = mySnapShotFile.getOriginFileName();
        this.filePath = mySnapShotFile.getFilePath();
        this.sortNo = mySnapShotFile.getSortNo();
        this.size = mySnapShotFile.getSize();
    }

    public CoWorkSnapShotFile(long coWorkSnapShotId, String originFileName, String filePath, int sortNo, BigDecimal size, long writerId){
        this.coWorkSnapShot = new CoWorkSnapShot(coWorkSnapShotId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    @Transient
    public String getOriginFileNameExt(){
        return Utils.getFileExtName(originFileName);
    }

    public boolean isOwner(long accountId){
        return writeEmbedded.getWriter().getId() == accountId;
    }
}
