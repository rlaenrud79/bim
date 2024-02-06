package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ProcessItemLink;
import lombok.Data;

@Data
public class LinkDTO {
    private long id;
    private Long source;
    private Long target;
    private String type;
    private int lag;        // 다음 작업의 FS SS 설정 duration
    private int sortNo;

    public LinkDTO(ProcessItemLink processItemLink){
        this.id = processItemLink.getId();
        this.source = processItemLink.getPredecessorId() == null ? 0L : processItemLink.getPredecessorId() ;
        this.target = processItemLink.getProcessItem().getId();
        this.type = processItemLink.getPredecessorType().getValue();
        this.lag = processItemLink.getPredecessorDuration();
        this.sortNo = processItemLink.getSortNo();
    }
}
