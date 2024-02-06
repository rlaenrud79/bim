package com.devo.bim.service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.repository.spring.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.ModelingStatus;
import com.devo.bim.model.vo.SearchModelingVO;
import com.devo.bim.repository.dsl.ModelingDTODslRepository;
import com.devo.bim.repository.dsl.MySnapShotDslRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelingService extends AbstractService {

    private final ModelingDTODslRepository modelingDTODslRepository;
    private final ModelingRepository modelingRepository;
    private final CoWorkModelingRepository coWorkModelingRepository;
    private final MySnapShotDslRepository mySnapShotDslRepository;
    private final FileDeleteService fileDeleteService;
    private final ModelingAttributeRepository modelingAttributeRepository;
    private final ModelingAssemblyRepository modelingAssemblyRepository;
    private final AccountService accountService;
    private final PhasingCodeFileRepository phasingCodeFileRepository;
    private final FileUploadService fileUploadService;

    public List<ModelingDTO> findModelingDTOsBySearchCondition(SearchModelingVO searchModelingVO) {
        return modelingDTODslRepository.findModelingDTOs(searchModelingVO);
    }

    public JsonObject findLatestConvertedModelingDTOs() {
        List<ModelingDTO> modelingDTOs = modelingDTODslRepository.findModelingDTOs(new SearchModelingVO(userInfo.getProjectId()));
        ModelingViewerDTO modelingViewerDTO = new ModelingViewerDTO(modelingDTOs);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("model",modelingViewerDTO.getModel());
        jsonObject.addProperty("models",modelingViewerDTO.getModels());
        jsonObject.addProperty("modelIds",modelingViewerDTO.getModelIds());
        jsonObject.addProperty("workName",modelingViewerDTO.getWorkName());
        jsonObject.addProperty("distinctWorkName", modelingViewerDTO.getDistinctWorkName());

        try {
        	jsonObject.addProperty("myWorkIds",accountService.getUserWorksIds(userInfo.getId()).toString());
        } catch (Exception e) {
			log.warn("{} workId is null", userInfo.getEmail() );
		}
        return jsonObject;
    }

    public JsonObject findConvertedModelingDTOs(String modelIds) {
        List<ModelingDTO> modelingDTOs = modelingDTODslRepository.findModelingDTOs(new SearchModelingVO(userInfo.getProjectId(),modelIds));
        ModelingViewerDTO modelingViewerDTO = new ModelingViewerDTO(modelingDTOs);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("model",modelingViewerDTO.getModel());
        jsonObject.addProperty("models",modelingViewerDTO.getModels());
        jsonObject.addProperty("modelIds",modelingViewerDTO.getModelIds());
        jsonObject.addProperty("workName",modelingViewerDTO.getWorkName());
        jsonObject.addProperty("distinctWorkName", modelingViewerDTO.getDistinctWorkName());

        try {
        	jsonObject.addProperty("myWorkIds",accountService.getUserWorksIds(userInfo.getId()).toString());
        } catch (Exception e) {
			log.warn("{} workId is null", userInfo.getEmail() );
		}

        return jsonObject;
    }

    public List<Modeling> findByModelIds(String modelIds) {
        return modelingRepository
                .findByProjectIdAndIdInOrderByIdAsc(userInfo.getProjectId(), Utils.getLongList(modelIds, ","))
                .stream()
                .sorted(Comparator.comparingLong(Modeling::getId))
                .collect(Collectors.toList());
    }

    public ModelingViewDTO findById(Long id) {
        return modelingRepository
                .findByProjectIdAndId(userInfo.getProjectId(), id)
                .map(m -> new ModelingViewDTO(m))
                .orElseGet(ModelingViewDTO::new);
    }

    @Transactional
    public JsonObject postModelingInfo(long workId, String description) {
        try {

            Modeling modeling = new Modeling();

            modeling.setModelingAtAddModeling(userInfo.getProjectId(), workId, description, userInfo.getId());

            long modelId = modelingRepository.save(modeling).getId();

            return proc.getResult(true, modelId, "");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putModelingInfoDelete(long id) {

        // 1. 모델링 조회
        Modeling savedModeling = modelingRepository.findByProjectIdAndId(userInfo.getProjectId(), id).orElseGet(Modeling::new);
        if (savedModeling.getId() == 0) return proc.getResult(false, "system.modeling_service.error_no_exist_modeling");

        // 2. co_work_modeling에 modelingId 있는지 조회
        int coWorkModelingCnt = coWorkModelingRepository.findByModelingId(id).size();

        // 3. my_snapShot에 modelingId 있는지 조회
        String searchModelId = "(" + id + ")";
        int mySnapShotCnt = mySnapShotDslRepository.findByProjectIdAndSearchModelIdIn(userInfo.getProjectId(), searchModelId).size();

        try {
            // 4. 삭제 대상이 최신 버전인 경우 그 전 버전으로 최신버전으로 수정
            if (savedModeling.isLatest()) setPreVersionModelingLatestTrue(savedModeling);

            // 5. co_work_modeling과 my_snapShot에 존재하면 논리적 삭제(UNUSED), 존재하지 않으면 물리적 삭제(DEL),
            if (coWorkModelingCnt == 0 && mySnapShotCnt == 0) deleteModelingInfo(savedModeling);
            else updateModelingStatus(savedModeling);

            return proc.getResult(true, "system.modeling_service.put_modeling_info_delete");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void setPreVersionModelingLatestTrue(Modeling savedModeling) {
        Modeling preVersionModeling = modelingRepository.findByModelName(savedModeling.getModelName())
                .stream()
                .filter(t -> ModelingStatus.USED == t.getStatus())
                .sorted(Comparator.comparing(Modeling::getModelName).reversed())
                .collect(Collectors.toList())
                .stream()
                .findFirst()
                .orElseGet(Modeling::new);
        if (preVersionModeling.getId() > 0) {
            preVersionModeling.setLatestAtPutModelingInfoDelete(true);
        }
    }

    // 파일 삭제 후 상태 DEL로 변경
    private void deleteModelingInfo(Modeling savedModeling) {
        fileDeleteService.deletePhysicalFile(savedModeling.getFilePath());
        fileDeleteService.deletePhysicalFile(savedModeling.getIfcFilePath());

        savedModeling.setStatusAndLatestAtPutModelingInfoDelete(ModelingStatus.DEL, false);
    }

    // 상태만 UNUSED로 변경
    private void updateModelingStatus(Modeling savedModeling) {
        savedModeling.setStatusAndLatestAtPutModelingInfoDelete(ModelingStatus.UNUSED, false);
    }

    private void deleteFile(String deleteFilePath) {
        File file = new File(deleteFilePath);
        if (file.exists()) file.delete();
    }

    @Transactional
    public JsonObject postUserProperty(ModelingAttributeDTO dto){
        JsonObject jsonObject = proc.getMessageResult(
                true,
                "success",
                modelingAttributeRepository.save(dto.toEntity()));
        return jsonObject;
    }

    @Transactional
    public JsonObject putUserProperty(ModelingAttributeDTO dto){
        JsonObject jsonObject = new JsonObject();
        try {
            modelingAttributeRepository.save(dto.toEntity());
            jsonObject.addProperty("result", true);
            jsonObject.addProperty("message", "success");
        }catch (Exception e){
            jsonObject.addProperty("result", false);
            jsonObject.addProperty("message", "faied to update");
        }
        return jsonObject;
    }

    public ModelingAssemblyDTO getMoelingAssemblyFindByExchangeId(String exchangeId) {
        ModelingAssembly m = modelingAssemblyRepository.findByExchangeId(exchangeId);
        ModelingAssemblyDTO dto = new ModelingAssemblyDTO();
        if( m != null ) {
            dto.setId(m.getId());
            dto.setAssemblyId(m.getAssemblyId());
            dto.setAssemblyName(m.getAssemblyName());
            dto.setExchangeId(m.getExchangeId());
        }
        return dto;
    }

    public List<ModelingAttributeDTO> getModelingAttributes(long modelingAssemblyId, boolean userAtt){
        ModelingAssembly modelingAssembly = new ModelingAssembly(modelingAssemblyId);
        List<ModelingAttribute> modelingAttributeList = modelingAttributeRepository.findAllByModelingAssemblyAndUserAttrOrderByIdDesc(modelingAssembly, userAtt);
        List<ModelingAttributeDTO> dtos = getModelingAttributeDTOs(modelingAssemblyId, modelingAttributeList);
        return dtos;
    }

    private List<ModelingAttributeDTO> getModelingAttributeDTOs(long modelingAssemblyId, List<ModelingAttribute> modelingAttributeList) {
        List<ModelingAttributeDTO> attributeDTOList = new ArrayList<>();
        for (ModelingAttribute attr: modelingAttributeList) {
            ModelingAttributeDTO attributeDTO = new ModelingAttributeDTO();
            attributeDTO.setId(attr.getId());
            attributeDTO.setAttributeName(attr.getAttributeName());
            attributeDTO.setAttributeValue(attr.getAttributeValue());
            attributeDTO.setAttributeType(attr.getAttributeType());
            attributeDTO.setUserAttr(attr.getUserAttr());
            attributeDTO.setModelingAssembly(new ModelingAssembly(modelingAssemblyId)); //OK
            //attributeDTO.setModelingAssembly(attr.getModelingAssembly()); // DO NOT USE LIKE THIS: Lazy Fetch에 의한 에러??? org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer
            attributeDTOList.add(attributeDTO);
        }
        return attributeDTOList;
    }

    @Transactional
    public JsonObject deleteUserProperty(long id){
        ModelingAttribute savedAttribute = modelingAttributeRepository.findById(id).orElseGet(ModelingAttribute::new);
        if(savedAttribute.getId() == 0 ){
            return proc.getResult(false, "system.modeling_service.error_no_exist_property");
        }
        try{
            modelingAttributeRepository.delete(savedAttribute);
            return proc.getMessageResult(true, "success to delete");
        }catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject getCntUserProperty(long modelingAssemblyId, String property){
        ModelingAssembly modelingAssembly = new ModelingAssembly(modelingAssemblyId);
        int cnt = modelingAttributeRepository.countByModelingAssemblyAndAttributeName(modelingAssembly, property);
        if( cnt > 0 ){
            return proc.getResult(false, "system.modeling_service.error_same_property_exists");
        }
        return proc.getMessageResult(true, "success");
    }
    
    public ModelingViewerDTO findModelingDTOs() {
        List<ModelingDTO> modelingDTOs = modelingDTODslRepository.findModelingDTOs(new SearchModelingVO(userInfo.getProjectId()));
        ModelingViewerDTO modelingViewerDTO = new ModelingViewerDTO(modelingDTOs);
        return modelingViewerDTO;
    }

    public PhasingCodeFile getPhasingCodeFile(long id){
        return phasingCodeFileRepository.findById(id).orElseGet(PhasingCodeFile::new);
    }

    /***
     * 메인 페이지에서 노드선택 > 객체 정보 팝업 표시 > 해당 객체에 매핑되어 있는 파일 정보 추출
     * @param projectId
     * @param phasingCode
     * @return 객체에 매핑된 파일 정보 리스트
     */
    public List<PhasingCodeFile> getPhasingCodeFiles(long projectId, String phasingCode){
        List<PhasingCodeFile> phasingCodeFiles = phasingCodeFileRepository.findAllByProjectIdAndPhasingCodeOrderByOriginFileName(projectId, phasingCode);
        return phasingCodeFiles;
    }

    /***
     * 메인 페이지에서 노드선택 > 객체 정보 팝업 표시 > 해당 객체에서 파일 설명 및 파일 선택 후 저장 > 해당 내용 데이터베이스 저장, 이후 별도의 Ajax 를 통해 파일을 저장한다.
     * @param phasingCodeFile
     * @return 저장된 PhasingCodeFile 의 PK
     */
    public PhasingCodeFile savePhasingCodeFile(PhasingCodeFile phasingCodeFile){
        return phasingCodeFileRepository.save(phasingCodeFile);
    }

    /***
     * 파일 삭제 및 데이터 삭제
     * @param id
     * @return
     */
    public JsonObject deletePhasingCodeFile(long id){
        PhasingCodeFile phasingCodeFile = phasingCodeFileRepository.findById(id).orElseGet(PhasingCodeFile::new);
        if (phasingCodeFile.getId() == 0) {
            return proc.getResult(false, "system.bulletin_file_service.not_exist_bulletin_file");
        }
        try {
            fileDeleteService.deletePhysicalFile(phasingCodeFile.getFilePath());
            phasingCodeFileRepository.delete(phasingCodeFile);
            return proc.getResult(true, "system.bulletin_file_service.delete_bulletin_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public ArrayList<String> findByLatestModelAndWorkNameEn(String workNameEn){
        ArrayList<String> modelNames = new ArrayList<>();
        List<Modeling> lst = modelingRepository.findByLatestModelAndWorkNameEn(workNameEn);
        for(Modeling m: lst){
            modelNames.add(m.getModelName());
        }
        return modelNames;
    }
}
