package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.BulletinLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BulletinLikesRepository extends JpaRepository<BulletinLikes, Long> {
    @Query(value = "select b.* from bulletin_likes b where b.bulletin_id = :bulletinId and b.writer_id = :writerId", nativeQuery = true)
    Optional<BulletinLikes> findByBulletinIdAndWriterId(long bulletinId, long writerId);
}
