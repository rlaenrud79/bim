package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class FileDownloadInfoDTO {
    private long id;
    private long workId;
    private FileDownloadUIType fileDownloadUIType;
    private String version;
    private String fileName;
    private String filePath;
    private BigDecimal fileSize;
    private long writerId;

    public FileDownloadInfoDTO(long id, long workId, FileDownloadUIType fileDownloadUIType, String version
            , String fileName, String filePath, BigDecimal fileSize, long writerId){
        this.id = id;
        this.workId = workId;
        this.fileDownloadUIType = fileDownloadUIType;
        this.version = version;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.writerId = writerId;
    }

    public String getDownloadFileName(FileDownloadUIType fileDownloadUIType){

        if(fileDownloadUIType == FileDownloadUIType.MODELING_FILE
                || fileDownloadUIType == FileDownloadUIType.MODELING_IFC_FILE) {

            return Utils.getFileNameWithOutExt(this.fileName) + "_" + this.version + "." + Utils.getFileExtName(this.fileName);
        }

        return this.fileName;
    }

    public String getErrorFileName(FileDownloadUIType fileDownloadUIType){

        if(fileDownloadUIType == FileDownloadUIType.MODELING_FILE
                || fileDownloadUIType == FileDownloadUIType.MODELING_IFC_FILE) {
            return "error_" + Utils.getFileNameWithOutExt(this.fileName) + "_" + this.version + ".txt";
        }

        return "error.txt";
    }
}
