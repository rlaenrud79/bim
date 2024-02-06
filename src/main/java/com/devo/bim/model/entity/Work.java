package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.WorkStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Work {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String name;

    @Enumerated(EnumType.STRING)
    private WorkStatus status;
    private int sortNo;
    private long upId;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="account_work",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="company_work",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Company> companies = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "work")
    private List<WorkName> workNames = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "work")
    private List<WorkAmount> workAmounts = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "work")
    private List<Document> documents = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="notice_work",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "notice_id")
    )
    private List<Notice> notices = new ArrayList<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="schedule_work",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id")
    )
    private List<Schedule> schedules = new ArrayList<>();

    public String getLocaleName(){
        String localeName = workNames.stream()
                .filter(t -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(WorkName::new)
                .getName();
        return StringUtils.isEmpty(localeName) ? this.name : localeName;
    }

    public Work(long workId){
        this.id = workId;
    }

    public void setWorkAtAddWork(long projectId, int sortNo, String enWorkName, long accountId, long upId) {
        this.projectId = projectId;
        this.name = enWorkName;
        this.sortNo = sortNo;
        this.status = WorkStatus.USE;
        this.writeEmbedded = new WriteEmbedded(accountId);
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
        this.upId = upId;
    }

    public void setWorkAtUpdateWork(int sortNo, String enWorkName, long accountId, long upId) {
        this.name = enWorkName;
        this.sortNo = sortNo;
        this.lastModifyEmbedded = new LastModifyEmbedded(accountId);
        this.upId = upId;
    }

    public void setWorkStatusAtUpdateStatus(WorkStatus status){
        this.status = status;
    }

    public void setSortNoAtUpdateSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public String getWorkName(String languageCode) {
        return this.workNames.stream()
                .filter(t-> languageCode.equalsIgnoreCase(t.getLanguageCode()))
                .findFirst()
                .orElseGet(WorkName::new)
                .getName();
    }

    public String getWorkName(){
        return this.workNames.stream()
                .filter(t -> LocaleContextHolder.getLocale().getLanguage().equalsIgnoreCase(t.getLanguageCode()) )
                .findFirst()
                .orElseGet(WorkName::new)
                .getName();
    }
}
