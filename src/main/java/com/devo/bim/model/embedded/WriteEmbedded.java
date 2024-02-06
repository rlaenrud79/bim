package com.devo.bim.model.embedded;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Embeddable
@Getter
@NoArgsConstructor
public class WriteEmbedded {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Account writer;

    private LocalDateTime writeDate = null;

    public WriteEmbedded(long writerId) {
        this.writer = new Account(writerId);
        this.writeDate = LocalDateTime.now();
    }

    public WriteEmbedded(WriteEmbedded writeEmbedded){
        this.writer = new Account(writeEmbedded.getWriter().getId());
        this.writeDate = writeEmbedded.getWriteDate();
    }

    public AccountDTO getAccountDTO(){
        return new AccountDTO(writer);
    }

    public String getWriteDate(DateFormatEnum dateFormatEnum) {
        return Utils.getDateTimeByNationAndFormatType(this.writeDate, dateFormatEnum);
    }
}
