package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PhasingCodeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long projectId;
    private String phasingCode;
    private String fileTitle;
    private String originFileName;
    private String filePath;
    private BigDecimal fileSize;
    private long writerId;
    private LocalDateTime writeDate;
    @Builder
    public PhasingCodeFile(long id, long projectId, String fileTitle, String phasingCode, String originFileName, String filePath, BigDecimal fileSize, long writerId, LocalDateTime writeDate){
        this.id = id;
        this.projectId = projectId;
        this.fileTitle = fileTitle;
        this.phasingCode =phasingCode;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.writerId = writerId;
        this.writeDate = writeDate;
    }
}
