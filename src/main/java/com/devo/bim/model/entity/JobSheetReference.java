package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetReference {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_sheet_id")
    private JobSheet jobSheet;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="reference_id")
    Account reference;

    private int sortNo;
    private LocalDateTime viewDate;

    public void setJobSheetReferenceAtAddJobSheet(JobSheet jobSheet, Long referenceId, int sortNo) {
        this.jobSheet = jobSheet;
        this.reference = new Account(referenceId);
        this.sortNo = sortNo;
    }

    public AccountDTO getAccountDTO() {
        return new AccountDTO(this.reference);
    }
}
