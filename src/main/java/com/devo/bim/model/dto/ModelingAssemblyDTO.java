package com.devo.bim.model.dto;

import com.devo.bim.model.entity.Modeling;
import com.devo.bim.model.entity.ModelingAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModelingAssemblyDTO {
    private long id;
    private Modeling modeling;
    private long assemblyId;
    private String assemblyName;
    private String children;
    private boolean isPart;
    private String exchangeId;
    private List<ModelingAttribute> modelingAttributes = new ArrayList<>();

}
