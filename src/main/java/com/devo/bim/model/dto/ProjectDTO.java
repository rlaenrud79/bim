package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDTO {
    private long id;
    private String name;
    private long maxUserCount;
    private long addedUserCount;
    private long addedCompanyCount;
    private boolean isValidLicense;
    private LocalDateTime writeDate;

    public ProjectDTO(Project project, long addedUserCount, long addedCompanyCount){
        this.id = project.getId();
        this.name = project.getName();
        this.maxUserCount = project.getMaxUserCount();
        this.addedUserCount = addedUserCount;
        this.addedCompanyCount = addedCompanyCount;
        this.isValidLicense = project.isValidLicense();
        this.writeDate = project.getWriteEmbedded().getWriteDate();
    }
}
