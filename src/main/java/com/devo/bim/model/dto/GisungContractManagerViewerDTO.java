package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GisungContractManagerViewerDTO {
    List<GisungContractManagerDTO> gisungContractManagerDTOs = new ArrayList<>();

    public GisungContractManagerViewerDTO(List<GisungContractManagerDTO> gisungContractManagerDTOs)
    {
        this.gisungContractManagerDTOs = gisungContractManagerDTOs;
    }

    public String getCompanys(){
        List<String> viewModels = gisungContractManagerDTOs.stream().map(t -> t.getCompany()).collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }

    public String getCompany(){
        return gisungContractManagerDTOs.stream().map(t -> t.getCompany()).findFirst().orElse("");
    }

    public String getIds(){
        return gisungContractManagerDTOs.stream().map(t -> t.getId()+"").collect(Collectors.joining(","));
    }

    public String getDamName(){
        List<String> viewModels = gisungContractManagerDTOs.stream().map(t -> t.getDamName()).collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }
    public String getStampPath(){
        List<String> viewModels = gisungContractManagerDTOs.stream().map(t -> t.getStampPath()).distinct().collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }
}
