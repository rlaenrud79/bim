package com.devo.bim.controller.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.entity.PhasingCodeFile;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.service.FileUploadService;
import com.devo.bim.service.SiteContractService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devo.bim.component.Coordinate;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.ModelingAssemblyDTO;
import com.devo.bim.model.dto.ModelingAttributeDTO;
import com.devo.bim.model.dto.SimulationDTO;
import com.devo.bim.service.ModelingService;
import com.devo.bim.service.ProcessService;
import com.devo.bim.service.GisungService;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/modelingViewerApi")
@RequiredArgsConstructor
public class ModelingViewerApiController extends AbstractController {
    private final ModelingService modelingService;
    private final ProcessService processService;
    private final SiteContractService siteContractService;
    private final FileUploadService fileUploadService;
    private final GisungService GisungService;

    @GetMapping("/latestModels")
    JsonObject latestModels() {
        return modelingService.findLatestConvertedModelingDTOs();
    }

    @GetMapping("/models/{modelIds}")
    JsonObject getModels(@PathVariable String modelIds) {
        if ("LATEST".equals(modelIds)) return modelingService.findLatestConvertedModelingDTOs();
        return modelingService.findConvertedModelingDTOs(modelIds);
    }

    @GetMapping("/processItems")
    List<SimulationDTO> getProcessItems() {
    	return processService.findTaskExchangeIds();
    }

    @GetMapping("/baseCoordinate")
    public ResponseEntity<Coordinate> getBaseCoordinate(){
        String coordinate = configService.findConfigForCache("MODEL", "MODEL_COORDINATE", userInfo.getProjectId());
        coordinate = coordinate.replaceAll("\\s", "");
        String pattern = "[x|X]:([-0-9]+(?:\\.[0-9]*)*),[y|Y]:([-0-9]+(?:\\.[0-9]*)*),[z|Z]:([-0-9]+(?:\\.[0-9]*)*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(coordinate);

        try {
            if (m.find()) {
                Coordinate p = new Coordinate(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)));
                return new ResponseEntity<>(p, HttpStatus.OK);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(new Coordinate(0, 0, 0), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 유저가 모델에 추가한 프로퍼티를 데이터베이스에 저장
     * @return JsonObject and HttpStatus
     */
    @PostMapping("/userProperty")
    public ResponseEntity<JsonObject> postUserProperty(@RequestBody ModelingAttributeDTO dto){
        JsonObject jsonObject = modelingService.postUserProperty(dto);
        return  new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

    @PutMapping("/userProperty")
    public JsonObject putUserProperty(@RequestBody ModelingAttributeDTO dto){
        JsonObject jsonObject = modelingService.putUserProperty(dto);
        return jsonObject;
    }

    @DeleteMapping("/userProperty/{id}")
    public JsonObject delUserProperty(@PathVariable long id){
        return modelingService.deleteUserProperty(id);
    }

    @GetMapping("/assemblyId/{exchangeId}")
    public ModelingAssemblyDTO getModelingAssembly(@PathVariable String exchangeId){
        return modelingService.getMoelingAssemblyFindByExchangeId(exchangeId);
    }

    @GetMapping("/userProperty/{assemblyId}")
    public List<ModelingAttributeDTO> getUserProperty(@PathVariable long assemblyId){
        return modelingService.getModelingAttributes(assemblyId, true);
    }

    @GetMapping("/userProperty/{assemblyId}/{property}")
    public JsonObject getCntProperty(@PathVariable long assemblyId, @PathVariable String property){
        return modelingService.getCntUserProperty(assemblyId, property);
    }

    @GetMapping("/nodeProperty/siteContract")
    public JsonObject getSiteContract(){
        return siteContractService.findAllByProjectId();
    }

    @GetMapping("/phasingCodeFile/{phasingCode}")
    public List<PhasingCodeFile> getPhasingCodeFile(@PathVariable String phasingCode){
        List<PhasingCodeFile>  lst = modelingService.getPhasingCodeFiles(userInfo.getProjectId(), phasingCode);
        return lst;
    }

    @PostMapping("/phasingCodeFile")
    public PhasingCodeFile fileUpload(@RequestParam("file") MultipartFile uploadFile,
                                             @RequestParam String phasingCode, @RequestParam String fileTitle){
        FileUploadDTO fileUploadDTO = new FileUploadDTO(0, FileUploadUIType.PHASING_CODE_FILE.toString(), uploadFile, false, false, userInfo.getProjectId());
        fileUploadService.uploadFile(fileUploadDTO);
        String clientFileFullPath = fileUploadDTO.getClientFileFullPath();
        WriteEmbedded writeEmbedded = new WriteEmbedded(userInfo.getId());
        PhasingCodeFile phasingCodeFile = PhasingCodeFile.builder()
                .projectId(userInfo.getProjectId())
                .phasingCode(phasingCode)
                .fileTitle(fileTitle)
                .originFileName(uploadFile.getOriginalFilename())
                .filePath(clientFileFullPath)
                .fileSize(new BigDecimal(uploadFile.getSize()))
                .writerId(userInfo.getId())
                .writeDate(LocalDateTime.now())
                .build();
        return modelingService.savePhasingCodeFile(phasingCodeFile);
    }

    @GetMapping("/phasingCodeFileDownload/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long id) throws IOException {
        PhasingCodeFile phasingCodeFile = modelingService.getPhasingCodeFile(id);
        String physicalPath = Utils.getPhysicalFilePath(winPathUpload, linuxPathUpload, macPathUpload, phasingCodeFile.getFilePath());
        Path path = Paths.get(physicalPath);
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + phasingCodeFile.getOriginFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/deletePhasingCodeFile")
    public JsonObject deleteBulletinFile(@RequestParam long id) {
        return modelingService.deletePhasingCodeFile(id);
    }

    @GetMapping("/modelNames/{workNameEn}")
    public ArrayList<String> modelNameByWorkNameEn(@PathVariable String workNameEn){
        return modelingService.findByLatestModelAndWorkNameEn(workNameEn);
    }

    @GetMapping("/getJobSheetModelExchangeIds")
    public List<JobSheetProcessItemDTO> getModelExchangeIdsByJobSheetProcessItem(@RequestParam String startDate, @RequestParam String endDate){
        return GisungService.getModelExchangeIdsByJobSheetProcessItem(startDate, endDate);
    }
}

