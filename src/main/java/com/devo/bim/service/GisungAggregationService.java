package com.devo.bim.service;

import com.devo.bim.model.entity.GisungAggregation;
import com.devo.bim.model.vo.GisungAggregationVO;
import com.devo.bim.repository.dsl.GisungAggregationDTODslRepository;
import com.devo.bim.repository.spring.GisungAggregationRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GisungAggregationService extends AbstractService {
    private final GisungAggregationRepository gisungAggregationRepository;

    private final GisungAggregationDTODslRepository gisungAggregationDTODslRepository;

    public List<GisungAggregation> findByYearAndDocumentNo(String year, Integer documentNo) {
        return gisungAggregationRepository.findByYearAndDocumentNo(year, documentNo);
    }

    public List<GisungAggregation> findByYearAndGtype(String year, String gtype, Integer documentNo) {
        return gisungAggregationRepository.findByYearAndGtype(year, gtype, documentNo);
    }
    public List<Map<String, Object>> findGisungAggregationByYear(long projectId) {
        return gisungAggregationRepository.findGisungAggregationByYear(projectId);
    }

    @Transactional
    public JsonObject postGisungAggregation(GisungAggregationVO gisungAggregationVO) {
        try {
            long id = 0;
            GisungAggregationVO savedGisungAggregationVO = new GisungAggregationVO();
            for (GisungAggregation gisungAggregation : gisungAggregationVO.getData()) {
                GisungAggregation savedGisungAggregation = new GisungAggregation();
                savedGisungAggregationVO.setYear(gisungAggregationVO.getYear());
                savedGisungAggregationVO.setNetCheck(gisungAggregation.getNetCheck());
                savedGisungAggregationVO.setTitle(gisungAggregation.getTitle());
                savedGisungAggregationVO.setCost(gisungAggregation.getCost());
                savedGisungAggregationVO.setGtype(gisungAggregation.getGtype());
                savedGisungAggregationVO.setPercent(gisungAggregation.getPercent());
                savedGisungAggregationVO.setDocumentNo(gisungAggregationVO.getDocumentNo());
                savedGisungAggregation.setGisungAggregationAtAddGisungAggregation(userInfo, savedGisungAggregationVO);
                gisungAggregationRepository.save(savedGisungAggregation);
                id = savedGisungAggregation.getId();
            }

            return proc.getResult(true, id, "system.gisung_aggregation_service.post_gisung_aggregation");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungAggregation(GisungAggregationVO gisungAggregationVO) {
        try {
            long id = 0;

            //GisungAggregation
            List<GisungAggregation> gisungAggregations = gisungAggregationRepository.findByYearAndDocumentNo(gisungAggregationVO.getYear(), gisungAggregationVO.getDocumentNo());
            if (gisungAggregations.size() > 0) {
                gisungAggregationRepository.deleteAllInBatch(gisungAggregations);
            }

            GisungAggregationVO savedGisungAggregationVO = new GisungAggregationVO();
            for (GisungAggregation gisungAggregation : gisungAggregationVO.getData()) {
                GisungAggregation savedGisungAggregation = new GisungAggregation();
                savedGisungAggregationVO.setYear(gisungAggregationVO.getYear());
                savedGisungAggregationVO.setNetCheck(gisungAggregation.getNetCheck());
                savedGisungAggregationVO.setTitle(gisungAggregation.getTitle());
                savedGisungAggregationVO.setCost(gisungAggregation.getCost());
                savedGisungAggregationVO.setGtype(gisungAggregation.getGtype());
                savedGisungAggregationVO.setPercent(gisungAggregation.getPercent());
                savedGisungAggregationVO.setDocumentNo(gisungAggregationVO.getDocumentNo());
                savedGisungAggregation.setGisungAggregationAtAddGisungAggregation(userInfo, savedGisungAggregationVO);
                gisungAggregationRepository.save(savedGisungAggregation);
                id = savedGisungAggregation.getId();
            }

            return proc.getResult(true, id, "system.gisung_aggregation_service.put_gisung_aggregation");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
