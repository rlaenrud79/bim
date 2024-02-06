package com.devo.bim.model.entity;

import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor
public class ProcessCategory extends ObjectModelHelper<ProcessCategory> {
    @EmbeddedId
    private ProcessCategoryId id;
    private String name;
    private long display;
    private long rowNum;

    // 생성자
    public ProcessCategory(ProcessCategoryId id, String name, long display, long rowNum) {
        this.id = id;
        this.name = name;
        this.display = display;
        this.rowNum = rowNum;
    }
}
