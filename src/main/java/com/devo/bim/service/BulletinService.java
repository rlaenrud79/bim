package com.devo.bim.service;

import com.devo.bim.model.dto.BulletinDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Bulletin;
import com.devo.bim.model.entity.Document;
import com.devo.bim.model.entity.DocumentFile;
import com.devo.bim.model.vo.BulletinVO;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.model.vo.SearchBulletinVO;
import com.devo.bim.repository.dsl.BulletinDTODslRepository;
import com.devo.bim.repository.spring.BulletinRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BulletinService extends AbstractService {

    private final BulletinRepository bulletinRepository;
    private final BulletinDTODslRepository bulletinDTODslRepository;

    @Transactional
    public JsonObject postBulletin(BulletinVO bulletinVO) {
        try {
            Bulletin bulletin = new Bulletin();
            bulletin.setBulletinAtAddBulletin(userInfo, bulletinVO);
            bulletinRepository.save(bulletin);

            return proc.getResult(true, bulletin.getId(), "system.bulletin_service.post_bulletin");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public PageDTO<BulletinDTO> findBulletinDTOs(SearchBulletinVO searchBulletinVO, Pageable pageable) {
        return bulletinDTODslRepository.findBulletinDTOs(searchBulletinVO, pageable);
    }

    public Bulletin findByIdAndProjectId(long id, long projectId) {
        return bulletinRepository.findByIdAndProjectId(id, projectId).orElseGet(Bulletin::new);
    }

    @Transactional
    public void addViewCount(Bulletin bulletin) {
        bulletin.addViewCountAtBulletinView();
    }

    @Transactional
    public JsonObject deleteBulletin(long id) {
        Bulletin bulletin = bulletinRepository.findByIdAndProjectId(id, userInfo.getProjectId()).orElseGet(Bulletin::new);
        if (bulletin.getId() == 0) {
            return proc.getResult(false, "system.bulletin_service.not_exist_bulletin");
        }

        try {
            bulletin.setBulletinAtDeleteBulletin();
            return proc.getResult(true, "system.bulletin_service.delete_bulletin");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteSelBulletin(List<BulletinVO> bulletinVO) {
        try {
            for (int i = 0; i < bulletinVO.size(); i++) {
                Bulletin bulletin = bulletinRepository.findById(bulletinVO.get(i).getId()).orElseGet(Bulletin::new);
                if (bulletin.getId() == 0) {
                    return proc.getResult(false, "system.bulletin_service.not_exist_bulletin");
                }

                bulletin.setBulletinAtDeleteBulletin();
            }

            return proc.getResult(true, "system.bulletin_service.delete_bulletin");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putBulletin(BulletinVO bulletinVO) {
        Bulletin savedBulletin = bulletinRepository.findByIdAndProjectId(bulletinVO.getId(), userInfo.getProjectId()).orElseGet(Bulletin::new);
        if (savedBulletin.getId() == 0) {
            return proc.getResult(false, "system.bulletin_service.not_exist_bulletin");
        }

        try {
            savedBulletin.setBulletinAtUpdateBulletin(userInfo, bulletinVO);

            return proc.getResult(true, "system.bulletin_service.put_bulletin");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
