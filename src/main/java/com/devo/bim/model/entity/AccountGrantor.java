package com.devo.bim.model.entity;

import com.devo.bim.model.dto.AccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class AccountGrantor {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    private long grantorId;

    public AccountDTO getAccountDTO() {
        return new AccountDTO(this.account);
    }

    public AccountGrantor(long accountId, long grantorId){
        this.grantorId = grantorId;
        this.account = new Account(accountId);
    }
}
