package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.entity.NoticeFile;
import com.devo.bim.model.entity.Work;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NoticeDTO {

    private long id;

    private long projectId;
    private String title;
    private String contents;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isPopup;

    private int viewCount;
    private boolean enabled;

    private AccountDTO writerDTO;
    private AccountDTO lastModifierDTO;

    private LocalDateTime writeDate;
    private LocalDateTime lastModifyDate;

    private boolean isTotalWorks;

    private List<Work> works = new ArrayList<>();
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    public NoticeDTO(Notice notice, Long totalWorksCnt) {
        this.id = notice.getId();
        this.projectId = notice.getProjectId();
        this.title = notice.getTitle();
        this.contents = notice.getContents();

        this.isPopup = notice.isPopup();
        this.startDate = notice.getStartDate();
        this.endDate = notice.getEndDate();

        this.viewCount = notice.getViewCount();
        this.enabled = notice.isEnabled();

        this.writerDTO = notice.getWriterDTO();
        this.lastModifierDTO = notice.getLastModifierDTO();

        this.writeDate = notice.getWriteEmbedded().getWriteDate();
        this.lastModifyDate = notice.getLastModifyEmbedded().getLastModifyDate();

        this.isTotalWorks = false;
        if(totalWorksCnt == notice.getWorks().size()) this.isTotalWorks = true;

        for (Work work : notice.getWorks()) {
            this.works.add(work);
        }
    }

    public void addWorks(List<Work> works, long totalWorksCnt) {
        this.isTotalWorks = false;
        if(totalWorksCnt == works.size()) this.isTotalWorks = true;

        for (Work work : works) {
            this.works.add(work);
        }
    }

    public void addNoticeFiles(List<NoticeFile> noticeFiles) {
        for (NoticeFile noticeFile : noticeFiles) {
            this.noticeFiles.add(noticeFile);
        }
    }

    public boolean isWriter(long userId){
        return this.writerDTO.getUserId() == userId;
    }

    public boolean isTargetUser(List<Work> userWorks){
        for (Work item : this.works) {
            if(userWorks.stream()
                    .filter(t -> t.getId() == item.getId() && t.getProjectId() == item.getProjectId())
                    .count() > 0) return true;
        }
        return false;
    }

    public String getWorksNames(){
        String worksNames = "";
        for (Work work : this.works) {
            worksNames += StringUtils.isEmpty(work.getWorkName(LocaleContextHolder.getLocale().getLanguage())) ? work.getName() + "/" : work.getWorkName(LocaleContextHolder.getLocale().getLanguage()) + "/";
        }
        return StringUtils.removeEnd(worksNames, "/");
    }
}
