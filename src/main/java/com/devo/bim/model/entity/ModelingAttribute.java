package com.devo.bim.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ModelingAttribute {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="modeling_assembly_id")
    private ModelingAssembly modelingAssembly;

    private String attributeName;
    private String attributeType;
    private String attributeValue;
    private Boolean userAttr;

    @Builder
    public  ModelingAttribute(long modelAssemblyId, long id, String attributeName, String attributeType, String attributeValue, boolean userAttr){
        this.modelingAssembly = new ModelingAssembly(modelAssemblyId);
        this.id = id;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
        this.userAttr = userAttr;
    }
}
