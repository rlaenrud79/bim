package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.BulletinReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulletinReplyRepository extends JpaRepository<BulletinReply, Long> {
    List<BulletinReply> findByBulletinId(long bulletinId);
}
