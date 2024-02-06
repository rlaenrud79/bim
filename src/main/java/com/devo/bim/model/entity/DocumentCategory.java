package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.DocumentCategoryStatus;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.DocumentCategoryVO;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class DocumentCategory extends ObjectModelHelper<DocumentCategory> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    private String name;
    @Enumerated(EnumType.STRING)
    private DocumentCategoryStatus status;
    private int sortNo;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    public DocumentCategory(long documentCategoryId){
        this.id = documentCategoryId;
    }

    public void setDocumentCategoryAtAddDocumentCategory(DocumentCategoryVO documentCategoryVO, int sortNo, UserInfo userInfo) {
        this.projectId = userInfo.getProjectId();
        this.name = documentCategoryVO.getName();
        this.sortNo = sortNo;
        this.status = DocumentCategoryStatus.USE;
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setDocumentCategoryAtUpdateDocumentCategory(DocumentCategoryVO documentCategoryVO, UserInfo userInfo) {
        this.name = documentCategoryVO.getName();
        this.sortNo = documentCategoryVO.getSortNo();
        this.lastModifyEmbedded = new LastModifyEmbedded(userInfo.getId());
    }

    public void setSortNoAtUpdateSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
