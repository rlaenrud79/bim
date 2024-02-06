package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class NoticeFile{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private String originFileName;
    private String filePath;
    private int sortNo;
    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public boolean isImage(){
        return Utils.isImage(this.originFileName);
    }

    public NoticeFile(long noticeId, String originalFilename, String filePath, int sortNo, BigDecimal size, long writerId) {
        this.notice = new Notice(noticeId);
        this.originFileName  = originalFilename;
        this.filePath = filePath;
        this.writeEmbedded = new WriteEmbedded(writerId);
        this.sortNo = sortNo;
        this.size = size;
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }
}
