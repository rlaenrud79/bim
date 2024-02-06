package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import com.devo.bim.model.enumulator.ConvertStatus;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchModelingVO {

    @NotNull
    private long projectId = 0L;

    private boolean searchLatest = true;
    @NotNull
    private long workId = -1L;
    private String fileName = "";
    @NotNull
    private long writerId = 0L;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDateFrom = null;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDateEnd = null;
    private ConvertStatus convertStatus = ConvertStatus.NONE;
    private String sortProp;

    private String modelingIds = "";

    public SearchModelingVO(Long projectId, Boolean searchLatest, Long workId, String fileName
                                        , Long writerId, LocalDate writeDateFrom, LocalDate writeDateEnd){
        this.projectId = projectId;
        this.searchLatest = searchLatest;
        this.workId = workId;
        this.fileName = fileName;
        this.writerId = writerId;
        this.writeDateFrom = writeDateFrom;
        this.writeDateEnd = writeDateEnd;
        this.convertStatus= ConvertStatus.NONE;
    }

    public SearchModelingVO(Long projectId){
        this.projectId = projectId;
        this.searchLatest = true;
        this.convertStatus = ConvertStatus.CONVERT_SUCCESS;
        this.sortProp = "ID";
    }

    public SearchModelingVO(Long projectId, String modelingIds){
        this.projectId = projectId;
        this.searchLatest = false; // BIM모델관리 > 구 버전 모델 > View 버튼
        this.sortProp = "ID";
        this.convertStatus = ConvertStatus.CONVERT_SUCCESS;
        this.modelingIds = modelingIds;
    }

    public List<Long> getModelingIds(){
        return Utils.getLongList(this.modelingIds,",");
    }
}
