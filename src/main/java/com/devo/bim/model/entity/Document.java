package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Document extends ObjectModelHelper<Document> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    private String title;
    private String description;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_category_id")
    private DocumentCategory documentCategory;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<DocumentFile> documentFiles = new ArrayList<>();

    public Document(long documentId) {
        this.id = documentId;
    }

    public void setDocumentAtAddDocument(DocumentVO documentVO, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.work = new Work(documentVO.getWorkId());
        this.title = documentVO.getTitle();
        this.description = documentVO.getDescription();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
        this.documentCategory = new DocumentCategory(documentVO.getDocumentCategoryId());
    }

    public void setDocumentAtUpdateDocument(DocumentVO documentVO, UserInfo userInfo) {
        this.work = new Work(documentVO.getWorkId());
        this.title = documentVO.getTitle();
        this.description = documentVO.getDescription();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
        this.documentCategory = new DocumentCategory(documentVO.getDocumentCategoryId());
    }
}
