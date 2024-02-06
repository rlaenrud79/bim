package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Modeling;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.DateFormatEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ModelingViewDTO {

    private long id;
    private long projectId;
    private String version;
    private boolean latest;
    private String description;
    private String modelName;
    private String fileName;
    private String filePath;
    private BigDecimal fileSize;

    private String ifcFileName;
    private String ifcFilePath;
    private BigDecimal ifcFileSize;

    private String workName;
    private String writerName;
    private String writeDate;
    private String writerCompany;
    private String writerCompanyRoleName;

    private AccountDTO accountDTO;

    public ModelingViewDTO(Modeling modeling) {
        this.id = modeling.getId();
        this.projectId = modeling.getProjectId();
        this.version = modeling.getVersion();
        this.latest = modeling.isLatest();
        this.modelName = modeling.getModelName();
        this.description = modeling.getDescription();
        this.fileName = modeling.getFileName();
        this.filePath = modeling.getFilePath();
        this.fileSize = modeling.getFileSize();
        this.ifcFileName = modeling.getIfcFileName();
        this.ifcFilePath = modeling.getIfcFilePath();
        this.ifcFileSize = modeling.getIfcFileSize();
        this.workName = modeling.getWork().getName();
        this.writerName = modeling.getWriteEmbedded().getWriter().getUserName();
        this.writeDate = modeling.getWriteEmbedded().getWriteDate(DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN);
        this.writerCompany = modeling.getWriteEmbedded().getWriter().getCompany().getName();
        this.writerCompanyRoleName = modeling.getWriteEmbedded().getWriter().getCompany().getCompanyRole().getName();
        this.accountDTO = new AccountDTO(modeling.getWriteEmbedded().getWriter());
    }
}
