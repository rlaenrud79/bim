package com.devo.bim.service;

import com.devo.bim.model.dto.GisungPaymentDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.GisungPayment;
import com.devo.bim.model.entity.GisungPaymentFile;
import com.devo.bim.model.vo.GisungPaymentVO;
import com.devo.bim.model.vo.SearchGisungPaymentVO;
import com.devo.bim.repository.dsl.GisungPaymentDTODslRepository;
import com.devo.bim.repository.spring.GisungPaymentFileRepository;
import com.devo.bim.repository.spring.GisungPaymentRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GisungPaymentService extends AbstractService {
    private final GisungPaymentRepository gisungPaymentRepository;
    private final GisungPaymentDTODslRepository gisungPaymentDTODslRepository;
    private final GisungPaymentFileRepository gisungPaymentFileRepository;
    private final FileDeleteService fileDeleteService;

    public PageDTO<GisungPaymentDTO> findGisungPaymentDTOs(SearchGisungPaymentVO searchGisungPaymentVO, Pageable pageable) {
        return gisungPaymentDTODslRepository.findGisungPaymentDTOs(searchGisungPaymentVO, pageable);
    }

    @Transactional
    public JsonObject postGisungPayment(GisungPaymentVO gisungPaymentVO) {
        try {
            GisungPayment gisungPayment = new GisungPayment();
            gisungPayment.setGisungPaymentAtAddGisungPayment(gisungPaymentVO, userInfo);
            gisungPaymentRepository.save(gisungPayment);

            return proc.getResult(true, gisungPayment.getId(), "system.gisung_payment_service.post_gisung_payment");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteGisungPayment(long id) {
        GisungPayment savedGisungPayment = gisungPaymentRepository.findById(id).orElseGet(GisungPayment::new);
        if (savedGisungPayment.getId() == 0) {
            return proc.getResult(false, "system.gisung_payment_service.not_exist_gisung_payment");
        }

        GisungPaymentFile savedGisungPaymentFile = savedGisungPayment.getGisungPaymentFiles().stream().findFirst().orElseGet(GisungPaymentFile::new);
        if (savedGisungPaymentFile.getId() == 0) {
            return proc.getResult(false, "system.gisung_payment_service.not_exist_gisung_payment_file");
        }

        try {
            gisungPaymentFileRepository.delete(savedGisungPaymentFile);
            gisungPaymentRepository.delete(savedGisungPayment);
            fileDeleteService.deletePhysicalFile(savedGisungPaymentFile.getFilePath());

            return proc.getResult(true, "system.gisung_payment_service.delete_gisung_payment");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public GisungPayment findByIdAndProjectId(long id, long projectId) {
        return gisungPaymentRepository.findByIdAndProjectId(id, projectId).orElseGet(GisungPayment::new);
    }

    @Transactional
    public JsonObject putGisungPayment(GisungPaymentVO gisungPaymentVO) {
        GisungPayment savedGisungPayment = gisungPaymentRepository.findById(gisungPaymentVO.getId()).orElseGet(GisungPayment::new);
        if (savedGisungPayment.getId() == 0) {
            return proc.getResult(false, "system.gisung_payment_service.not_exist_gisung_payment");
        }

        try {
            savedGisungPayment.setGisungPaymentAtUpdateGisungPayment(gisungPaymentVO, userInfo);
            return proc.getResult(true, "system.gisung_payment_service.put_gisung_payment");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
