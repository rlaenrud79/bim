package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.CoWorkStatus;
import com.devo.bim.model.vo.CoWorkVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWork {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String subject;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @Enumerated(STRING)
    private CoWorkStatus status;

    @OneToOne(fetch = LAZY, mappedBy = "coWork")
    @JoinColumn(name = "chatting_id")
    private Chatting chatting;

    @OneToMany(fetch = LAZY, mappedBy = "coWork")
    private List<CoWorkModeling> coWorkModelings = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "coWork")
    private List<CoWorkJoiner> coWorkJoiners = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "coWork")
    private List<CoWorkIssue> coWorkIssues = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "coWork")
    private List<CoWorkSnapShot> coWorkSnapShots = new ArrayList<>();

    public CoWork(UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.subject = userInfo.getUserCreateText();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
        this.status = CoWorkStatus.GOING;
    }
    
    public void setCoWorkSnapShot(CoWorkSnapShot coWorkSnapShot){
    	this.coWorkSnapShots.add(coWorkSnapShot);
    }

    public CoWork(long id) {
        this.id = id;
    }
    
    public List<CoWorkSnapShot> getSnapShotByIdAsc()
    {
    	return coWorkSnapShots.stream().filter(m->m.isEnabled()).sorted(Comparator.comparingLong(CoWorkSnapShot::getId)).collect(Collectors.toList());
    }

    public List<CoWorkSnapShot> getSnapShots()
    {
        return coWorkSnapShots.stream().filter(m->m.isEnabled()).collect(Collectors.toList());
    }

    public boolean isJoiner(long accountId)
    {
        return coWorkJoiners.stream().filter(m->m.getJoiner().getId()==accountId).count() > 0;
    }

    public void putCoWork(CoWorkVO coWorkVO, long accountId)
    {
        this.subject = coWorkVO.getSubject();
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public List<Long> joinerIds()
    {
        return coWorkJoiners.stream().map(o -> o.getJoiner().getId()).collect(Collectors.toList());
    }

    public List<Long> allJoinerIds()
    {
        List<Long> ids = new ArrayList<>();
        // 협업 생성자
        ids.add(writeEmbedded.getWriter().getId());

        // 이슈 참여자(생성자 포함)
        for(CoWorkIssue coWorkIssue : coWorkIssues) {
            ids.addAll(coWorkIssue.getCoWorkIssueJoiners().stream().map(o -> o.getJoiner().getId()).collect(Collectors.toList()));
        }

        // 채팅 참여자
        return ids.stream().distinct().collect(Collectors.toList());
    }

    public void completeStatus() {
        this.status = CoWorkStatus.COMPLETE;
    }

    public boolean isGoing()
    {
        if(this.status == null) return true;
        return this.status == CoWorkStatus.GOING;
    }

    public boolean isComplete()
    {
        if(this.status == null) return false;
        return this.status == CoWorkStatus.COMPLETE;
    }

    public String getModelIds()
    {
        return coWorkModelings.stream().map(t->t.getModeling().getId()+"").collect(Collectors.joining(","));
    }
}
