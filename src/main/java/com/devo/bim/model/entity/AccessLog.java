package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class AccessLog extends ObjectModelHelper<AccessLog> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private long projectId;
    private String menuCode;
    private long accessorId;
    private LocalDateTime accessDate;
    private String date;


    public AccessLog(String menuCode, long accessorId, long projectId) {
        this.projectId = projectId;
        this.menuCode = menuCode;
        this.accessorId = accessorId;
        this.accessDate = LocalDateTime.now();
        this.date = Utils.todayString();
    }
}
