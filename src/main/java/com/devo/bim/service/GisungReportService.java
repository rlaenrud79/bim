package com.devo.bim.service;

import com.devo.bim.model.dto.GisungReportDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Document;
import com.devo.bim.model.entity.DocumentFile;
import com.devo.bim.model.entity.Gisung;
import com.devo.bim.model.entity.GisungReport;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.model.vo.GisungReportVO;
import com.devo.bim.model.vo.SearchGisungReportVO;
import com.devo.bim.repository.dsl.GisungReportDTODslRepository;
import com.devo.bim.repository.spring.GisungReportRepository;
import com.devo.bim.repository.spring.GisungRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GisungReportService extends AbstractService {

    private final GisungReportRepository gisungReportRepository;
    private final GisungReportDTODslRepository gisungReportDTODslRepository;
    private final GisungRepository gisungRepository;
    private final FileDeleteService fileDeleteService;

    public PageDTO<GisungReportDTO> findGisungReportDTOs(SearchGisungReportVO searchGisungReportVO, Pageable pageable) {
        return gisungReportDTODslRepository.findGisungReportDTOs(searchGisungReportVO, pageable);
    }

    @Transactional
    public JsonObject postGisungReport(GisungReportVO gisungReportVO) {
        try {
            Gisung gisung = gisungRepository.findById(gisungReportVO.getGisungId()).orElseGet(Gisung::new);
            GisungReport gisungReport = new GisungReport();
            gisungReport.setGisungReportAtAddGisungReport(gisungReportVO, gisung, userInfo);
            gisungReportRepository.save(gisungReport);

            return proc.getResult(true, gisungReport.getId(), "system.document_service.post_document");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public GisungReport findByIdAndProjectId(long id, long projectId) {
        return gisungReportRepository.findByIdAndProjectId(id, projectId).orElseGet(GisungReport::new);
    }

    @Transactional
    public JsonObject deleteGisungReport(long id) {
        GisungReport savedGisungReport = gisungReportRepository.findById(id).orElseGet(GisungReport::new);
        if (savedGisungReport.getId() == 0) {
            return proc.getResult(false, "system.gisung_report_service.not_exist_gisung_report");
        }

        try {
            gisungReportRepository.delete(savedGisungReport);
            fileDeleteService.deletePhysicalFile(savedGisungReport.getSurveyFilePath());
            fileDeleteService.deletePhysicalFile(savedGisungReport.getPartSurveyFilePath());
            fileDeleteService.deletePhysicalFile(savedGisungReport.getAggregateFilePath());
            fileDeleteService.deletePhysicalFile(savedGisungReport.getPartAggregateFilePath());
            fileDeleteService.deletePhysicalFile(savedGisungReport.getAccountFilePath());
            fileDeleteService.deletePhysicalFile(savedGisungReport.getEtcFilePath());

            return proc.getResult(true, "system.gisung_report_service.delete_gisung_report");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungReport(GisungReportVO gisungReportVO) {
        GisungReport savedGisungReport = gisungReportRepository.findById(gisungReportVO.getId()).orElseGet(GisungReport::new);
        if (savedGisungReport.getId() == 0) {
            return proc.getResult(false, "system.document_service.not_exist_document");
        }

        try {
            savedGisungReport.setGisungReportAtUpdateGisungReport(gisungReportVO, userInfo);
            return proc.getResult(true, "system.document_service.put_document");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
