package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class BulletinFile extends ObjectModelHelper<BulletinFile> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bulletin_id")
    private Bulletin bulletin;

    private String originFileName;
    private String filePath;
    private int sortNo;
    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public BulletinFile(long bulletinId, String originalFilename, String filePath, int sortNo, BigDecimal size, long writerId) {
        this.bulletin = new Bulletin(bulletinId);
        this.originFileName = originalFilename;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public boolean isImage() {
        return Utils.isImage(this.originFileName);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }

}
