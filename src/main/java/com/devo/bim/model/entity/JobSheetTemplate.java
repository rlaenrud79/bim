package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.JobSheetTemplateVO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class JobSheetTemplate {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;
    private String title;
    private String contents;
    private boolean enabled;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public JobSheetTemplate(UserInfo userInfo, JobSheetTemplateVO jobSheetTemplateVO) {
        this.projectId = userInfo.getProjectId();
        this.title = jobSheetTemplateVO.getTitle();
        this.contents = jobSheetTemplateVO.getContents();
        this.enabled = jobSheetTemplateVO.isEnable();
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setJobSheetTemplateAtUpdateJobSheetTemplate(String title, String contents, boolean enable) {
        this.title = title;
        this.contents = contents;
        this.enabled = enable;
    }
}