package com.devo.bim.model.entity;

import com.devo.bim.model.enumulator.PredecessorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItemLink {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="process_item_id")
    private ProcessItem processItem;

    private String predecessor;

    @Enumerated(STRING)
    private PredecessorType predecessorType;
    private int predecessorValue;
    private int predecessorDuration;

    private int sortNo;

    private Long predecessorId = 0L;

    public ProcessItemLink(long id){
        this.id = id;
    }

    public void setProcessItemLinkIdAndSortNo(long id, int sortNo){
        this.processItem = new ProcessItem(id);
        this.sortNo = sortNo;
    }

    public void setProcessItemAtSaveProcessItemLink(long processItemId, int sortNo){
        this.processItem = new ProcessItem(processItemId);
        this.sortNo = sortNo;
    }

    private PredecessorType getSelectedPredecessorType(String fsType){
        if("FS".equalsIgnoreCase(fsType)) return PredecessorType.FS;
        if("SS".equalsIgnoreCase(fsType)) return PredecessorType.SS;
        if("FF".equalsIgnoreCase(fsType)) return PredecessorType.FF;
        if("SF".equalsIgnoreCase(fsType)) return PredecessorType.SF;
        return PredecessorType.NONE;
    }

    private int getSelectedPredecessorValue(String fsType){
        if("FS".equalsIgnoreCase(fsType)) return Integer.parseInt(PredecessorType.FS.getValue());
        if("SS".equalsIgnoreCase(fsType)) return Integer.parseInt(PredecessorType.SS.getValue());
        if("FF".equalsIgnoreCase(fsType)) return Integer.parseInt(PredecessorType.FF.getValue());
        if("SF".equalsIgnoreCase(fsType)) return Integer.parseInt(PredecessorType.SF.getValue());
        return -1;
    }

    public void setProcessItemLinkAtPutGanttData(ProcessItemLink processItemLink, String predecessor){
        if(processItemLink.getId() > 0) this.id = processItemLink.getId();
        if(processItemLink.getProcessItem().getId() != this.processItem.getId()) this.processItem = new ProcessItem(processItemLink.getProcessItem().getId());
        if(!predecessor.equalsIgnoreCase(this.predecessor)) this.predecessor = predecessor;
        this.predecessorType = processItemLink.getPredecessorType();
        this.predecessorValue = processItemLink.getPredecessorValue();
        this.predecessorDuration = processItemLink.getPredecessorDuration();
        this.predecessorId = processItemLink.getPredecessorId();
    }
}
