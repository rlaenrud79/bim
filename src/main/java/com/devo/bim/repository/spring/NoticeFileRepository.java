package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findByNoticeId(long noticeId);

    Optional<NoticeFile> findByNoticeIdAndSortNo(long noticeId, int sortNo);

    Optional<NoticeFile> findById(long id);
}
