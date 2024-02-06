package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ProjectLicense;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProjectLicenseDTO {
    private long projectId;
    private String userMacAddress;

    public ProjectLicenseDTO(ProjectLicense projectLicense){
        this.projectId = projectLicense.getProject().getId();
        this.userMacAddress = projectLicense.getMacAddress();
    }
}
