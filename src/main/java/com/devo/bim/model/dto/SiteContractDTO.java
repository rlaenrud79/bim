package com.devo.bim.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SiteContractDTO {
    private long id;
    private long projectId;
    private String siteStatus;
    private long siteSum;

    public SiteContractDTO(Long id, Long projectId, String siteStatus, Long siteSum){
        this.id = id;
        this.projectId = projectId;
        this.siteStatus = siteStatus;
        this.siteSum = siteSum;
    }
}
