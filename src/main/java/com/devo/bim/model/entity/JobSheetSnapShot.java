package com.devo.bim.model.entity;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.devo.bim.model.embedded.WriteEmbedded;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetSnapShot {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private int sortNo;
    private String path;    // 삭제대상

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;

    private String title;
    private String source;  // path 대체

    private String viewPointJson;
    private String viewModelJson;
    private String viewModelId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    private String searchModelId;

    @OneToMany(fetch = LAZY, mappedBy = "coWorkSnapShot")
    private List<CoWorkSnapShotFile> coWorkSnapShotFiles = new ArrayList<>();

    public void setJobSheetSnapShotAtAddJobSheet(JobSheet jobSheet, MySnapShot mySnapShot, long accountId, int sortNo) {
        this.jobSheet = jobSheet;
        this.title = mySnapShot.getTitle();
        this.source = mySnapShot.getSource();
        this.viewPointJson = mySnapShot.getViewPointJson();
        this.viewModelJson = mySnapShot.getViewModelJson();
        this.viewModelId = mySnapShot.getViewModelId();
        this.searchModelId = mySnapShot.getSearchModelId();
        this.sortNo = sortNo;
        this.writeEmbedded = new WriteEmbedded(accountId);
    }

    public void setJobSheetSnapShotAtCopyJobSheet(JobSheet jobSheet, JobSheetSnapShot jobSheetSnapShot) {
        this.jobSheet = jobSheet;
        this.sortNo = jobSheetSnapShot.getSortNo();
        this.writeEmbedded = jobSheetSnapShot.getWriteEmbedded();
        this.title = jobSheetSnapShot.getTitle();
        this.source = jobSheetSnapShot.getSource();
        this.viewPointJson = jobSheetSnapShot.getViewPointJson();
        this.viewModelJson = jobSheetSnapShot.getViewModelJson();
        this.viewModelId = jobSheetSnapShot.getViewModelId();
        this.searchModelId = jobSheetSnapShot.getSearchModelId();
    }
}
