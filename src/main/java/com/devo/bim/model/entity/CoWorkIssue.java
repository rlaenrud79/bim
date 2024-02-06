package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.CoWorkIssuePriority;
import com.devo.bim.model.enumulator.IssueStatus;
import com.devo.bim.model.vo.CoWorkIssueVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class CoWorkIssue {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "co_work_id")
    private CoWork coWork;

    private String title;
    private String contents;

    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private LocalDateTime requestDate;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @Enumerated(STRING)
    private IssueStatus status;
    private LocalDateTime statusUpdateDate;

    @Enumerated(STRING)
    private CoWorkIssuePriority priority;

    @OneToMany(fetch = LAZY, mappedBy = "coWorkIssue", cascade = CascadeType.REMOVE)
    private List<CoWorkIssueReport> coWorkIssueReports = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "coWorkIssue", cascade = CascadeType.REMOVE)
    private List<CoWorkIssueJoiner> coWorkIssueJoiners = new ArrayList<>();

    public CoWorkIssue(Long coWorkId, long accountId) {
        this.coWork = new CoWork(coWorkId);
        this.title = "";
        this.contents = "";
        this.writeEmbedded = new WriteEmbedded(accountId);
        this.priority = CoWorkIssuePriority.NONE;
        this.status = IssueStatus.WRITE;
    }

    public CoWorkIssue setId(long id) {
        this.id = id;
        return this;
    }

    public CoWorkIssue(CoWorkIssueVO coWorkIssueVO, long accountId) {
        this.coWork = new CoWork(coWorkIssueVO.getCoWorkId());
        this.title = coWorkIssueVO.getTitle();
        this.requestDate = Utils.parseLocalDateTimeEnd(coWorkIssueVO.getRequestDate());
        this.contents = coWorkIssueVO.getContents();
        this.priority = CoWorkIssuePriority.valueOf(coWorkIssueVO.getPriority());
        this.status = IssueStatus.valueOf(coWorkIssueVO.getStatus());
        this.statusUpdateDate = LocalDateTime.now();
        this.writeEmbedded = new WriteEmbedded(accountId);
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
    }

    public CoWorkIssue updateData(CoWorkIssueVO coWorkIssueVO, long accountId) {
        this.coWork = new CoWork(coWorkIssueVO.getCoWorkId());
        this.title = coWorkIssueVO.getTitle();
        this.requestDate = Utils.parseLocalDateTimeEnd(coWorkIssueVO.getRequestDate());
        this.contents = coWorkIssueVO.getContents();
        this.priority = CoWorkIssuePriority.valueOf(coWorkIssueVO.getPriority());

        IssueStatus newIssueStatus = IssueStatus.valueOf(coWorkIssueVO.getStatus());

        if (this.status != newIssueStatus) {
            this.status = IssueStatus.valueOf(coWorkIssueVO.getStatus());
            this.statusUpdateDate = LocalDateTime.now();
        }

        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
        return this;
    }

    public boolean isStatus(String status) {
        return this.status == IssueStatus.valueOf(status);
    }

    public boolean isOwner(long loginAccountId) {
        if(this.writeEmbedded == null) return false;
        return this.writeEmbedded.getWriter().getId() == loginAccountId;
    }

    public boolean isJoiner(long accountId) {
        return coWorkIssueJoiners.stream().filter(t -> t.getJoiner().getId() == accountId).count() > 0;
    }

    public List<Long> joinerIds() {
        List<Long> ids = coWorkIssueJoiners.stream().map(o -> o.getJoiner().getId()).collect(Collectors.toList());
        if (ids.size() == 0) ids.add(writeEmbedded.getWriter().getId());
        return ids;
    }

    public boolean isIncludedUser(long accountId) {
        return this.writeEmbedded.getWriter().getId() == accountId || isJoiner(accountId);
    }

    public boolean isRequestOrGoing() {
        if(this.status == IssueStatus.REQUEST || this.status == IssueStatus.GOING) return true;
        return false;
    }

    public AccountDTO getAccountDTO() {
        return new AccountDTO(this.writeEmbedded.getWriter());
    }
}
