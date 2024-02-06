package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ModelingAssembly {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="modeling_id")
    private Modeling modeling;

    private long assemblyId; // hoops에서 사용하는 assembly id
    private String assemblyName;
    private String children;
    private boolean isPart;
    private String exchangeId;

    @OneToMany(fetch = LAZY, mappedBy = "modelingAssembly")
    private List<ModelingAttribute> modelingAttributes = new ArrayList<>();

    public ModelingAssembly(long id) {
        this.id = id;
    }

    /**
     * Parent의 modelId와 hoops의 exchangeId를 통해서 생성
     * @param modelId Parent인 Modeling의 id
     * @param exchangeId Hoops의 exchange id
     */
    public ModelingAssembly(long modelId, String exchangeId){
        this.modeling = new Modeling(modelId);
        this.exchangeId = exchangeId;
    }
}
