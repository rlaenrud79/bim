package com.devo.bim.model.dto;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Work;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ModelingViewerDTO {

    List<ModelingDTO> modelingDTOs = new ArrayList<>();

    public ModelingViewerDTO(List<ModelingDTO> modelingDTOs)
    {
        this.modelingDTOs = modelingDTOs;
    }

    public String getModels(){
        List<String> viewModels = modelingDTOs.stream().map(t -> t.getModelName()).collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }

    public String getModel(){
        return modelingDTOs.stream().map(t -> t.getModelName()).findFirst().orElse("");
    }

    public String getModelIds(){
        return modelingDTOs.stream().map(t -> t.getId()+"").collect(Collectors.joining(","));
    }

    public String getWorkName(){
        List<String> viewModels = modelingDTOs.stream().map(t -> t.getWork().getWorkName()).collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }
    public String getDistinctWorkName(){
        List<String> viewModels = modelingDTOs.stream().map(t -> t.getWork().getWorkName()).distinct().collect(Collectors.toList());
        return Utils.getJsonString(viewModels);
    }
}
