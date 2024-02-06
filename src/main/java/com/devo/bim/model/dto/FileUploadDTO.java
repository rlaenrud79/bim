package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.FileUploadUIType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.Locale;

@Data
@RequiredArgsConstructor
public class FileUploadDTO {
    private long id;
    private FileUploadUIType fileUploadUIType;
    private MultipartFile file;
    private boolean makeVersion;
    private boolean executeHoopsConverter = false;
    private long projectId;

    public FileUploadDTO(long id, String fileUploadUIType, MultipartFile file, boolean makeVersion, boolean executeHoopsConverter, long projectId){
        this.id = id;
        this.fileUploadUIType = FileUploadUIType.valueOf(fileUploadUIType);
        this.file = file;
        this.makeVersion = makeVersion;
        this.executeHoopsConverter = executeHoopsConverter;
        this.projectId = projectId;
    }

    private String versionString;
    private String serverBasePath;
    private String clientBasePath = "/file_upload/";
    private String savedFileName;

    public String getOriginalFilename()
    {
        return this.file.getOriginalFilename();
    }

    public String getFileNameWithOutExt()
    {
        return Utils.getFileNameWithOutExt(getOriginalFilename());
    }

    public String getFileExtName()
    {
        return Utils.getFileExtName(getOriginalFilename());
    }

    public String getNowDate()
    {
        return Utils.getSaveFileNameDate();
    }

    public String getSaveFileName()
    {
        if (StringUtils.isEmpty(versionString)) return getFileNameWithOutExt() + "_" + getNowDate() + "." + getFileExtName();
        if (executeHoopsConverter) return getModelName() + "." + getFileExtName();
        else return getModelName() + "_" + getNowDate()  + "." + getFileExtName();
    }

    public void setSaveFileName()
    {
        this.savedFileName = getSaveFileName();
    }

    public void setSaveFileName(long workId)
    {
        if(workId > 0 ) this.savedFileName = this.projectId + "_" + workId + "_" + getSaveFileName();
    }

    public String getServerFolderPath() {
        return this.serverBasePath + "/" + this.projectId + "/" +  getUploadUITypeString();
    }

    public String getServerFileFullPath() {
        return  getServerFolderPath() + "/" +this.savedFileName;
    }

    public String getClientFileFullPath()
    {
        return this.clientBasePath + this.projectId + "/" + getUploadUITypeString() + "/" + this.savedFileName;
    }

    public String getModelName()
    {
        return getFileNameWithOutExt() + "_" + versionString;
    }

    public String getUploadUITypeString()
    {
        if (fileUploadUIType == FileUploadUIType.MODELING_FILE || fileUploadUIType == FileUploadUIType.MODELING_IFC_FILE) return "model";
        return fileUploadUIType.name().toLowerCase(Locale.ROOT);
    }
}
