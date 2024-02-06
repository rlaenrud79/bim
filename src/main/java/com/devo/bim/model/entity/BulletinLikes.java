package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.BulletinLikesVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class BulletinLikes {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bulletin_id")
    private Bulletin bulletin;

    private boolean enabled;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public void setBulletinLikesAtPostBulletinLikes(BulletinLikesVO bulletinLikesVO, UserInfo userInfo) {
        this.bulletin = new Bulletin(bulletinLikesVO.getBulletinId());
        this.enabled = bulletinLikesVO.isEnabled();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }
}
