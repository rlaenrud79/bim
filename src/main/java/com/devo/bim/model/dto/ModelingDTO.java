package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Modeling;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.ConvertStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ModelingDTO {

    private long id;
    private long projectId;
    private String version;
    private boolean latest;
    private String description;

    private String fileName;
    private String filePath;
    private BigDecimal fileSize;

    private String ifcFileName;
    private String ifcFilePath;
    private String modelName;
    private BigDecimal ifcFileSize;
    private ConvertStatus convertStatus;

    private Work work;
    private AccountDTO writerDTO;
    private LocalDateTime writeDate;

    public ModelingDTO(long id, long projectId, String version, boolean latest, String description
            , String fileName, String filePath, BigDecimal fileSize
            , String ifcFileName, String ifcFilePath, BigDecimal ifcFileSize, ConvertStatus convertStatus
            , Work work, Account writer
            , LocalDateTime writeDate){

        this.id = id;
        this.projectId = projectId;
        this.version = version;
        this.latest = latest;
        this.description = description;

        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;

        this.ifcFileName = ifcFileName;
        this.ifcFilePath = ifcFilePath;
        this.ifcFileSize = ifcFileSize;
        this.convertStatus = convertStatus;

        this.work = work;
        this.writerDTO = new AccountDTO(writer);
        this.writeDate = writeDate;
    }

    public ModelingDTO(Modeling modeling){

        this.id = modeling.getId();
        this.projectId = modeling.getProjectId();
        this.version = modeling.getVersion();
        this.latest = modeling.isLatest();
        this.description = modeling.getDescription();

        this.fileName = modeling.getFileName();
        this.filePath = modeling.getFilePath();
        this.fileSize = modeling.getFileSize();

        this.ifcFileName = modeling.getIfcFileName();
        this.ifcFilePath = modeling.getIfcFilePath();
        this.ifcFileSize = modeling.getIfcFileSize();
        this.modelName = modeling.getModelName();
        this.convertStatus = modeling.getConvertStatus();

        this.work = modeling.getWork();
        this.writerDTO = new AccountDTO(modeling.getWriteEmbedded().getWriter());
        this.writeDate = modeling.getWriteEmbedded().getWriteDate();
    }

    public String getWriteDate(DateFormatEnum dateFormatEnum){
        return Utils.getDateTimeByNationAndFormatType(this.writeDate, dateFormatEnum);
    }

    public BigDecimal getFileSizeMegaByteUnit() {
        return Utils.getFileSizeMegaByteUnit(this.fileSize);
    }

    public boolean isIfcConvertStatusEqNone(){
        if(this.convertStatus == ConvertStatus.NONE) return true;
        return false;
    }

    public boolean isIfcConvertStatusEqReady(){
        if(this.convertStatus == ConvertStatus.READY) return true;
        return false;
    }

    public boolean isIfcConvertStatusEqRequest(){
        if(this.convertStatus == ConvertStatus.REQUEST) return true;
        return false;
    }

    public boolean isIfcConvertStatusEqConverting(){
        if(this.convertStatus == ConvertStatus.CONVERTING) return true;
        return false;
    }

    public boolean isIfcConvertStatusEqSuccess(){
        if(this.convertStatus == ConvertStatus.CONVERT_SUCCESS) return true;
        return false;
    }

    public boolean isIfcConvertStatusEqFail(){
        if(this.convertStatus == ConvertStatus.CONVERT_FAIL) return true;
        return false;
    }
}
