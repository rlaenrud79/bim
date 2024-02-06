package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProjectImage {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private int sortNo;
    private String path;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    public ProjectImage(long projectId, String filePath, int sortNo, long loginAccountId) {
        this.project = new Project(projectId);
        this.path = filePath;
        this.sortNo = sortNo;
        this.writeEmbedded = new WriteEmbedded(loginAccountId);
    }

    public String getFileName()
    {
        return Utils.getFileName(this.path);
    }
}
