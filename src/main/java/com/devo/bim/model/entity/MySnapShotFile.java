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
public class MySnapShotFile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "my_snap_shot_id")
    private MySnapShot mySnapShot;

    private int sortNo;
    private BigDecimal size;
    private String originFileName;
    private String filePath;

    @Embedded
    private WriteEmbedded writeEmbedded;
    
    public MySnapShotFile(long mySnapShotId, String originFileName, String filePath, int sortNo, BigDecimal size, long writerId){
        this.mySnapShot = new MySnapShot(mySnapShotId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }
}
