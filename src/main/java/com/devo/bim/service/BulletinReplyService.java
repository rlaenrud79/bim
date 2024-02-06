package com.devo.bim.service;

import com.devo.bim.model.entity.BulletinReply;
import com.devo.bim.model.vo.BulletinReplyVO;
import com.devo.bim.repository.spring.BulletinReplyRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BulletinReplyService extends AbstractService {

    private final BulletinReplyRepository bulletinReplyRepository;

    @Transactional
    public JsonObject postBulletinReply(BulletinReplyVO bulletinReplyVO) {
        try {
            List<BulletinReply> savedBulletinReplies = bulletinReplyRepository.findByBulletinId(bulletinReplyVO.getBulletinId());

            savedBulletinReplies.add(new BulletinReply(
                    bulletinReplyVO.getBulletinId()
                    , bulletinReplyVO.getContents()
                    , savedBulletinReplies.size() + 1
                    , userInfo.getId()
            ));

            savedBulletinReplies.forEach(t -> {
                bulletinReplyRepository.save(t);
            });
            return proc.getResult(true, "system.bulletin_reply_service.post_bulletin_reply");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<BulletinReply> findByBulletinId(long bulletinId) {
        List<BulletinReply> savedBulletinReplies = bulletinReplyRepository.findByBulletinId(bulletinId);
        return savedBulletinReplies.stream()
                .filter(BulletinReply::isEnabled)
                .sorted(Comparator.comparingInt(BulletinReply::getSortNo))
                .collect(Collectors.toList());
    }

    @Transactional
    public JsonObject deleteBulletinReply(long id) {
        BulletinReply savedBulletinReply = bulletinReplyRepository.findById(id).orElseGet(BulletinReply::new);
        if (savedBulletinReply.getId() == 0) {
            return proc.getResult(false, "system.bulletin_reply_service.not_exist_bulletin_reply");
        }
        try {
            savedBulletinReply.setDisableAtDeleteBulletinReply(userInfo);
            return proc.getResult(true, "system.bulletin_reply_service.delete_bulletin_reply");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());

        }
    }
}
