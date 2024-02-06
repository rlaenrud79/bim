package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.entity.SiteContract;
import com.devo.bim.repository.spring.SiteContractRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteContractService extends AbstractService {
    private final SiteContractRepository siteContractRepository;
    private final UserInfo userInfo;

    public JsonObject findAllByProjectId(){
        return proc.getResult(true, siteContractRepository.findAllBy(userInfo.getProjectId()));
    }

    public List<SiteContract> findAllByProjectId(Long projectId){
        return siteContractRepository.findAllBy(projectId);
    }
}
