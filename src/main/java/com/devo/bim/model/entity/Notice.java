package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.NoticeVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends ObjectModelHelper<Notice> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isPopup;
    private String contents;
    private int viewCount;
    private boolean enabled;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "notice")
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="notice_work",
            joinColumns = @JoinColumn(name = "notice_id"),
            inverseJoinColumns = @JoinColumn(name = "work_id")
    )
    private List<Work> works = new ArrayList<>();

    public Notice(long noticeId){
        this.id = noticeId;
    }

    public Notice(NoticeVO noticeVO) {
        if(noticeVO.getNoticeId() != 0) this.id = noticeVO.getNoticeId();
        this.projectId = noticeVO.getProjectId();
        this.title = noticeVO.getTitle();
        this.contents = noticeVO.getContents();

        this.isPopup = noticeVO.isPopup();

        if(noticeVO.isPopup()) {
            this.startDate = LocalDateTime.parse(noticeVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.endDate = LocalDateTime.parse(noticeVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        else{
            this.startDate = null;
            this.endDate = null;
        }

        this.viewCount = 0;
        this.enabled = true;

        this.writeEmbedded = new WriteEmbedded(noticeVO.getUserId());
        this.lastModifyEmbedded = new LastModifyEmbedded(noticeVO.getUserId());
    }


    public void setNoticeAtPutNotice(Notice notice) {
        if(notice.getId() != 0) this.id = notice.getId();
        if(notice.getProjectId() != 0) this.projectId = notice.getProjectId();
        if(!StringUtils.isEmpty(notice.getTitle()) ) this.title = notice.getTitle();
        if(!StringUtils.isEmpty(notice.getContents()) ) this.contents = notice.getContents();

        this.isPopup = notice.isPopup();

        if(notice.isPopup()) {
            this.startDate = notice.getStartDate();
            this.endDate = notice.getEndDate();
        }
        else{
            this.startDate = null;
            this.endDate = null;
        }

        this.lastModifyEmbedded = new LastModifyEmbedded(notice.lastModifyEmbedded.getLastModifier().getId());

        this.works = new ArrayList<>();

        for (Work work : notice.getWorks()) {
            this.works.add(work);
        }
    }

    public void setWork(Work work){
        this.works.add(work);
        work.getNotices().add(this);
    }

    public AccountDTO getWriterDTO(){
        return new AccountDTO(this.writeEmbedded.getWriter());
    }

    public AccountDTO getLastModifierDTO(){
        return new AccountDTO(this.lastModifyEmbedded.getLastModifier());
    }

    public String getWorksNames(){
        String worksNames = "";
        for (Work work : this.works) {
            worksNames += StringUtils.isEmpty(work.getWorkName(LocaleContextHolder.getLocale().getLanguage())) ? work.getName() + "/" : work.getWorkName(LocaleContextHolder.getLocale().getLanguage()) + "/";
        }
        return StringUtils.removeEnd(worksNames, "/").replace("/", " / ");
    }

    public void setViewCountPlusOne(){
        this.viewCount ++;
    }

    public void setDisEnabledAtDeleteNotice(){
        this.enabled = false;
    }

    public List<Long> getNoticeWorksIds(){
        return this.works.stream().map(o -> o.getId()).collect(Collectors.toList());
    }
}
