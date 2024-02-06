package com.devo.bim.service;

import com.devo.bim.model.dto.GisungContractManagerDTO;
import com.devo.bim.model.dto.GisungContractManagerViewerDTO;
import com.devo.bim.model.dto.ModelingViewerDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.GisungContractManagerVO;
import com.devo.bim.model.vo.GisungTableVO;
import com.devo.bim.model.vo.SearchGisungContractManagerVO;
import com.devo.bim.model.vo.SearchModelingVO;
import com.devo.bim.repository.dsl.GisungContractManagerDTODslRepository;
import com.devo.bim.repository.spring.GisungContractManagerRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GisungContractManagerService extends AbstractService {
    private final GisungContractManagerRepository gisungContractManagerRepository;
    private final GisungContractManagerDTODslRepository gisungContractManagerDTODslRepository;
    private final FileDeleteService fileDeleteService;

    public PageDTO<GisungContractManagerDTO> findGisungContractManagerDTOs(SearchGisungContractManagerVO searchGisungContractManagerVO, Pageable pageable) {
        return gisungContractManagerDTODslRepository.findGisungContractManagerDTOs(searchGisungContractManagerVO, pageable);
    }

    public GisungContractManager findByIdAndProjectId(long id, long projectId) {
        return gisungContractManagerRepository.findByIdAndProjectId(id, projectId).orElseGet(GisungContractManager::new);
    }

    public List<GisungContractManager> findGisungContractManagerByProjectId(long projectId) {
        return gisungContractManagerRepository.findGisungContractManagerByProjectId(projectId);
    }

    @Transactional
    public JsonObject procGisungContractManager(GisungContractManagerVO gisungContractManagerVO) {
        try {
            GisungContractManager savedGisungContractManager = new GisungContractManager();
            savedGisungContractManager.setGisungContractManager(userInfo, gisungContractManagerVO);
            gisungContractManagerRepository.save(savedGisungContractManager);

            return proc.getResult(true, savedGisungContractManager.getId(), "system.gisung_service.post_gisung_contract_manager");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungContractManager(GisungContractManagerVO gisungContractManagerVO) {
        try {
            long id = gisungContractManagerVO.getId();

            GisungContractManager savedGisungContractManager = gisungContractManagerRepository.findById(gisungContractManagerVO.getId()).orElseGet(GisungContractManager::new);
            if (savedGisungContractManager.getId() == 0) {
                return proc.getResult(false, "system.gisung_service.not_exist_gisung_contract_manager");
            }

            savedGisungContractManager.setGisungContractManagerAtUpdateGisungContractManager(userInfo, gisungContractManagerVO);

            return proc.getResult(true, id, "system.gisung_service.put_gisung_contract_manager");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public JsonObject deleteGisungContractManager(long id) {
        GisungContractManager savedGisungContractManager = gisungContractManagerRepository.findById(id).orElseGet(GisungContractManager::new);
        if (savedGisungContractManager.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung_contract_manager");
        }

        try {
            gisungContractManagerRepository.delete(savedGisungContractManager);
            fileDeleteService.deletePhysicalFile(savedGisungContractManager.getStampPath());

            return proc.getResult(true, "system.gisung_service.delete_gisung_contract_manager");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject findGisungContractManagerDTOs() {
        List<GisungContractManagerDTO> gisungContractManagers =  gisungContractManagerDTODslRepository.findGisungContractManagerDTOs(new SearchGisungContractManagerVO(userInfo.getProjectId()));
        GisungContractManagerViewerDTO gisungContractManagerViewerDTO = new GisungContractManagerViewerDTO(gisungContractManagers);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("company",gisungContractManagerViewerDTO.getCompany());
        jsonObject.addProperty("companys",gisungContractManagerViewerDTO.getCompanys());
        jsonObject.addProperty("companyIds",gisungContractManagerViewerDTO.getIds());
        jsonObject.addProperty("damName",gisungContractManagerViewerDTO.getDamName());
        jsonObject.addProperty("stampPath", gisungContractManagerViewerDTO.getStampPath());

        return proc.getResult(gisungContractManagers);
        //return jsonObject;
    }
}
