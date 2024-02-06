package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.enumulator.GrantStatus;
import com.devo.bim.model.vo.JobSheetGrantorVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetGrantor {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="job_sheet_id")
    JobSheet jobSheet;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="grantor_id")
    Account grantor;

    private int sortNo;
    private LocalDateTime grantDate;
    private String description;

    @Enumerated(STRING)
    private GrantStatus status;

    public void setJobSheetGrantorAtAddJobSheet(JobSheet jobSheet, long id) {
        this.jobSheet = jobSheet;
        this.grantor = new Account(id);
        this.status = GrantStatus.READY;
    }

    public AccountDTO getAccountDTO() {
        return new AccountDTO(this.grantor);
    }

    public void setJobSheetGrantorAtDenyJobSheet(JobSheetGrantorVO jobSheetGrantorVO) {
        this.grantDate = LocalDateTime.now();
        this.status = GrantStatus.REJECT;
        this.description = jobSheetGrantorVO.getDescription();
    }

    public void setJobSheetGrantorAtApproveJobSheet(JobSheetGrantorVO jobSheetGrantorVO) {
        this.grantDate = LocalDateTime.now();
        this.status = GrantStatus.APPROVE;
        this.description = jobSheetGrantorVO.getDescription();
    }

    public void setJobSheetGrantorAtDeleteJobSheetReply() {
        this.description = "";
    }
}
