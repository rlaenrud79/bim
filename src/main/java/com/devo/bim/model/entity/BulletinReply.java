package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.BulletinReplyVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class BulletinReply {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bulletin_id")
    private Bulletin bulletin;

    private String contents;
    private boolean enabled;
    private int sortNo;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public BulletinReply(long bulletinId, String contents, int sortNo, long userId) {
        this.bulletin = new Bulletin(bulletinId);
        this.contents = contents;
        this.enabled = true;
        this.sortNo = sortNo;
        this.writeEmbedded = new WriteEmbedded(userId);
    }

    public void setDisableAtDeleteBulletinReply(UserInfo userInfo) {
        this.enabled = false;
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }

    public boolean isCurrentUserReply(long userId){
        return this.writeEmbedded.getWriter().getId() == userId;
    }
}
