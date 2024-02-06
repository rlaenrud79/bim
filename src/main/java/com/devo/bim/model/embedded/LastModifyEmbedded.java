package com.devo.bim.model.embedded;

import com.devo.bim.component.Utils;
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
public class LastModifyEmbedded {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="lastModifier_id")
    private Account lastModifier;

    private LocalDateTime lastModifyDate = null;

    public LastModifyEmbedded(long lastModifierId){
        this.lastModifier = new Account(lastModifierId);
        this.lastModifyDate = LocalDateTime.now();
    }

    public LastModifyEmbedded(LastModifyEmbedded lastModifyEmbedded){
        this.lastModifier = new Account(lastModifyEmbedded.getLastModifier().getId());
        this.lastModifyDate = lastModifyEmbedded.getLastModifyDate();
    }

    public String getLastModifyDate(DateFormatEnum dateFormatEnum){
        return Utils.getDateTimeByNationAndFormatType(this.lastModifyDate, dateFormatEnum);
    }
}
