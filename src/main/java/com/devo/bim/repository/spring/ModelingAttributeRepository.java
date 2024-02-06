package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.ModelingAssembly;
import com.devo.bim.model.entity.ModelingAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelingAttributeRepository extends JpaRepository<ModelingAttribute, Long> {
    List<ModelingAttribute> findAllByModelingAssemblyAndUserAttrOrderByIdDesc(ModelingAssembly modelingAssembly, boolean userAttr );
    int countByModelingAssemblyAndAttributeName(ModelingAssembly modelingAssembly, String attrName);
}
