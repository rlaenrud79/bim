package com.devo.bim.service;

import com.devo.bim.model.entity.BulletinLikes;
import com.devo.bim.model.vo.BulletinLikesVO;
import com.devo.bim.repository.spring.BulletinLikesRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BulletinLikesService extends AbstractService {
    private final BulletinLikesRepository bulletinLikesRepository;

    @Transactional
    public JsonObject postBulletinLikes(BulletinLikesVO bulletinLikesVO) {
        try {
            BulletinLikes bulletinLikes = bulletinLikesRepository.findByBulletinIdAndWriterId(bulletinLikesVO.getBulletinId(), userInfo.getId()).orElseGet(BulletinLikes::new);
            if (bulletinLikes.getId() == 0) {
                bulletinLikes.setBulletinLikesAtPostBulletinLikes(bulletinLikesVO, userInfo);
                bulletinLikesRepository.save(bulletinLikes);
            } else {
                bulletinLikes.setBulletinLikesAtPostBulletinLikes(bulletinLikesVO, userInfo);
            }
            return proc.getResult(true);

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public BulletinLikes findByBulletinIdAndWriterId(long bulletinId, long writerId) {
        return bulletinLikesRepository.findByBulletinIdAndWriterId(bulletinId, writerId).orElseGet(BulletinLikes::new);
    }
}
