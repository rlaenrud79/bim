package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class DocumentFile extends ObjectModelHelper<DocumentFile> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    private String originFileName;
    private String filePath;
    private int sortNo;

    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public DocumentFile(long documentId, String originFileName, String filePath, int sortNo, BigDecimal size, long writerId) {
        this.document = new Document(documentId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }
}
