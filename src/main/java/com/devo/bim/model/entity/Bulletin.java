package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.BulletinVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Bulletin extends ObjectModelHelper<Bulletin> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String title;
    private String contents;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    private boolean enabled;
    private int viewCount;

    @OneToMany(fetch = LAZY, mappedBy = "bulletin")
    private List<BulletinFile> bulletinFiles = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "bulletin")
    private List<BulletinReply> bulletinReplies = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "bulletin")
    private List<BulletinLikes> bulletinLikesList = new ArrayList<>();

    public Bulletin(long bulletinId){
        this.id = bulletinId;
    }

    public void setBulletinAtAddBulletin(UserInfo userInfo, BulletinVO bulletinVO) {
        this.projectId = userInfo.getProjectId();
        this.title = bulletinVO.getTitle();
        this.contents = bulletinVO.getContents();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.enabled = true;
        this.viewCount = 0;
    }

    public void addViewCountAtBulletinView() {
        this.viewCount += 1;
    }

    public List<BulletinReply> getBulletinReplies() {
        return this.bulletinReplies.stream()
                .filter(BulletinReply::isEnabled)
                .sorted(Comparator.comparingInt(BulletinReply::getSortNo))
                .collect(Collectors.toList());
    }

    public List<BulletinLikes> getEnabledBulletinLikesList() {
        return this.bulletinLikesList.stream()
                .filter(BulletinLikes::isEnabled)
                .collect(Collectors.toList());
    }

    public void setBulletinAtDeleteBulletin() {
        this.enabled = false;
    }

    public void setBulletinAtUpdateBulletin(UserInfo userInfo, BulletinVO bulletinVO) {
        this.title = bulletinVO.getTitle();
        this.contents = bulletinVO.getContents();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }
}
