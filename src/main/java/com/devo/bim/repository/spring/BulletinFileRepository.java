package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.BulletinFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulletinFileRepository extends JpaRepository<BulletinFile, Long> {
    List<BulletinFile> findByBulletinId(long bulletinId);
}
