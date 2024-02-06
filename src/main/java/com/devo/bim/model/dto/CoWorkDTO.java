package com.devo.bim.model.dto;

import com.devo.bim.model.entity.*;
import com.devo.bim.model.enumulator.CoWorkStatus;
import com.devo.bim.model.enumulator.IssueStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CoWorkDTO {

    private long id;
    private String subject;
    private long coWorkJoinerCount;
    private long coWorkModelingCount;
    private long coWorkIssueCount;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;
    private CoWorkStatus status;
    private long coWorkIssueRequestOrGoingCount;
    private List<CoWorkIssueJoiner> coWorkIssueRequestOrGoingJoiners = new ArrayList<>();

    public CoWorkDTO(CoWork coWork, long coWorkJoinerCount, long coWorkModelingCount, long coWorkIssueCount){
        this.id = coWork.getId();
        this.subject = coWork.getSubject();
        this.coWorkJoinerCount = coWorkJoinerCount;
        this.coWorkModelingCount = coWorkModelingCount;
        this.coWorkIssueCount = coWorkIssueCount;
        this.writerDTO = coWork.getWriteEmbedded().getAccountDTO();
        this.writeDate = coWork.getWriteEmbedded().getWriteDate();
        this.status = coWork.getStatus();

        List<CoWorkIssue> coWorkIssueRequestOrGoings = coWork.getCoWorkIssues()
                .stream()
                .filter(t -> t.getStatus() == IssueStatus.REQUEST || t.getStatus() == IssueStatus.GOING)
                .collect(Collectors.toList());

        this.coWorkIssueRequestOrGoingCount = coWorkIssueRequestOrGoings.size();
        coWorkIssueRequestOrGoings.forEach( t -> {
            coWorkIssueRequestOrGoingJoiners.addAll(t.getCoWorkIssueJoiners());
        });

    }

    public boolean isWriter(long accountId) {
        if(this.writerDTO.getUserId() == accountId) return true;
        return false;
    }

    public boolean isHaveRequestOrGoingIssue(){
        if(this.coWorkIssueRequestOrGoingCount > 0) return true;
        return false;
    }

    public boolean isIncludedUser(long accountId){
        return this.coWorkIssueRequestOrGoingJoiners.stream().filter(t -> t.getJoiner().getId() == accountId).count() > 0;
    }
}
