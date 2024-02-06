package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.ConvertStatus;
import com.devo.bim.model.enumulator.ModelingStatus;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Modeling extends ObjectModelHelper<Modeling> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="work_id")
    private Work work;

    private long projectId;
    private String modelName;
    private String fileName;
    private String filePath;
    private BigDecimal fileSize;
    private String version;
    private String ifcFileName;
    private String ifcFilePath;
    private BigDecimal ifcFileSize;
    private String description;

    @Enumerated(STRING)
    private ConvertStatus convertStatus;

    @Embedded
    private WriteEmbedded writeEmbedded;
    private boolean latest;

    @Enumerated(STRING)
    private ModelingStatus status;

    @OneToMany(fetch = LAZY, mappedBy = "modeling")
    private List<CoWorkModeling> coWorkModelings = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "modeling")
    private List<ModelingDownloadLog> modelingDownloadLogs = new ArrayList<>();

    private String convertLog;

    public Modeling(long id){
        this.id = id;
    }

    @OneToMany(fetch = LAZY, mappedBy = "modeling")
    private List<ModelingAssembly> modelingAssemblies = new ArrayList<>();

    public void setModelingAtAddModeling(long projectId, long workId, String description, long writerId){
        this.projectId = projectId;
        this.work = new Work(workId);
        this.description = description;
        this.status = ModelingStatus.USED;
        this.latest = false;
        this.writeEmbedded = new WriteEmbedded(writerId);
        this.convertStatus = ConvertStatus.NONE;
        this.fileSize = new BigDecimal("0");
    }

    public void setModelingFileInfoAtFileUpload(String originalFileName, String filePath, BigDecimal fileSize, String versionString){
        this.latest = true;
        this.fileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.version = versionString;
        this.convertStatus = ConvertStatus.READY;
        this.modelName = this.projectId + "_" + this.work.getId() + "_" + Utils.getFileNameWithOutExt(originalFileName) + "_" + versionString;
    }

    public void setModelingIfcFileInfoAtFileUpload(String ifcFileName, String ifcFilePath, BigDecimal ifcFileSize){
        this.latest = true;
        this.ifcFileName = ifcFileName;
        this.ifcFilePath = ifcFilePath;
        this.ifcFileSize = ifcFileSize;
    }

    public void setNoLatestAtAddModeling(){
        this.latest = false;
    }

    public void setStatusAndLatestAtPutModelingInfoDelete(ModelingStatus modelingStatus, boolean latest){
        this.status = modelingStatus;
        this.latest = latest;
    }

    public void setLatestAtPutModelingInfoDelete(boolean latest){
        this.latest = latest;
    }

    public void setFileConvertStatusAtSaveModelingFile(){
        this.convertStatus = ConvertStatus.REQUEST;
    }

    public void setIfcConvertLog(String log){
        this.convertLog = log;
    }
}
