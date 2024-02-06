package com.devo.bim.controller.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.FileDownloadInfoDTO;
import com.devo.bim.model.dto.FileUploadDTO;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.service.CoWorkService;
import com.devo.bim.service.FileDeleteService;
import com.devo.bim.service.FileDownloadService;
import com.devo.bim.service.FileUploadService;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/commonApi")
@RequiredArgsConstructor
public class CommonApiController extends AbstractController {

    private final CoWorkService coWorkService;
    private final FileUploadService fileUploadService;
    private final FileDownloadService fileDownloadService;
    private final FileDeleteService fileDeleteService;

    @PostMapping("/uploadFile")
    public JsonObject uploadFile(@RequestParam(required = true, defaultValue = "0") long id
            , @RequestParam(required = true, defaultValue = "") String fileUploadUIType
            , @RequestParam("file") MultipartFile uploadFile
            , @RequestParam(required = true, defaultValue = "false") boolean makeVersion
            , @RequestParam(required = true, defaultValue = "false") boolean executeHoopsConverter) {

        FileUploadDTO fileUploadDTO = new FileUploadDTO(id, fileUploadUIType, uploadFile, makeVersion, executeHoopsConverter, userInfo.getProjectId());

        return fileUploadService.uploadFile(fileUploadDTO);
    }

    @GetMapping(value = "/downloadFile/{fileDownloadUIType}/{id}")
    public ResponseEntity fileDownload(@PathVariable("fileDownloadUIType") String fileDownloadUIType
            , @PathVariable("id") long id) throws IOException {

        FileDownloadUIType tmpFileDownloadUIType = Utils.getFileDownloadUITypeEnum(fileDownloadUIType);

        FileDownloadInfoDTO fileDownloadInfoDTO = fileDownloadService.findByIdAndFileDownloadUIType(id, tmpFileDownloadUIType);

        Path path = Paths.get( getDownloadFilePath(fileDownloadInfoDTO));

        try {
            Resource resource = fileDownloadService.getFileResource(path);
            fileDownloadService.postFileDownloadLog(fileDownloadInfoDTO.getId(), tmpFileDownloadUIType);
            return getHeader(getApplicationOctetStream(), fileDownloadInfoDTO.getDownloadFileName(tmpFileDownloadUIType))
                    .body(resource);
        } catch (FileNotFoundException ioe) {
            log.error("error", ioe); // 2) 로그를 작성
            return getHeader(getApplicationHtmlUTF8(), fileDownloadInfoDTO.getErrorFileName(tmpFileDownloadUIType))
                    .body(getTranslateErrorNoExistFile());
        } catch (Exception e){
            log.error("error", e); // 2) 로그를 작성
            return getHeader(getApplicationHtmlUTF8(), fileDownloadInfoDTO.getErrorFileName(tmpFileDownloadUIType))
                    .body(getTranslateErrorDownloadFile());
        }
    }

    @NotNull
    private String getDownloadFilePath(FileDownloadInfoDTO fileDownloadInfoDTO) {
        return Utils.getPhysicalFilePath(winPathUpload, linuxPathUpload, macPathUpload, fileDownloadInfoDTO.getFilePath());
    }

    private String getTranslateErrorDownloadFile() {
        return proc.translate("system.file_download_service.error_download_file");
    }

    private String getTranslateErrorNoExistFile() {
        return proc.translate("system.file_download_service.error_no_exist_file");
    }

    @NotNull
    private String getApplicationOctetStream() {
        return "application/octet-stream";
    }

    @NotNull
    private String getApplicationHtmlUTF8() {
        return "application/html;charset=UTF-8";
    }

    @NotNull
    private ResponseEntity.BodyBuilder getHeader(String s, String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getUrlEncdeFileName(fileName) + "\"");
    }

    @NotNull
    private String getUrlEncdeFileName(String fileName) {
        return URLEncoder.encode(fileName, Charset.defaultCharset()).replaceAll("\\+", "%20");
    }

    @PostMapping("/shareMySnapShot")
    public JsonObject shareMySnapShot(MySnapShotShareVO mySnapShotShareVO) {

        if(mySnapShotShareVO.isCoWork()) return coWorkService.postSnapShotWithShare(mySnapShotShareVO);
        return proc.getResult(false, "system.co_work_service.fail_sharing_my_snap_shot");
    }

    @DeleteMapping("/deleteFile/{fileUploadUIType}/{fileId}")
    public JsonObject deleteFile(@PathVariable("fileUploadUIType") String fileUploadUIType
            , @PathVariable("fileId") long fileId){

        return fileDeleteService.deleteFileAndDB(FileUploadUIType.valueOf(fileUploadUIType), fileId);
    }

    @GetMapping("/translate/{msgCode}")
    public JsonObject getTranslate(@PathVariable String msgCode){
        return proc.getResult(true, msgCode);
    }
}
