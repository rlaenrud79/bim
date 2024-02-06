package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Modeling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelingRepository extends JpaRepository<Modeling, Long> {
    List<Modeling> findByProjectIdAndIdInOrderByIdAsc(long projectId, List<Long> longs);
    Optional<Modeling> findByProjectIdAndId(long projectId, Long id);
    List<Modeling> findByModelName(String modelingName);

    @Query(value= getModelingByWorkNameEn, nativeQuery=true)
    List<Modeling> findByLatestModelAndWorkNameEn(@Param("work_name_en") String workNameEn);

    String getModelingByWorkNameEn = "" +
            " select m.* from modeling m inner join work w on m.work_id = w.id " +
            " inner join work_name wn on w.id = wn.work_id" +
            " where wn.language_code = 'EN' and wn.name = :work_name_en and m.latest = true ";

}
