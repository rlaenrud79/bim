package com.devo.bim.model.dto;

import com.devo.bim.model.entity.ModelingAssembly;
import com.devo.bim.model.entity.ModelingAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelingAttributeDTO {
    private long id;
    private ModelingAssembly modelingAssembly;
    private String attributeName;
    private String attributeType;
    private String attributeValue;
    private boolean userAttr;

    public ModelingAttribute toEntity(){
        ModelingAttribute build = ModelingAttribute.builder()
                .modelAssemblyId(modelingAssembly.getId()) // hoops assembly id
                .id(id) // attribute table id (pk)
                .attributeName(attributeName)
                .attributeType(attributeType)
                .attributeValue(attributeValue)
                .userAttr(userAttr)
                .build();
        return build;
    }
}
