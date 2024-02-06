package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findByIdAndProjectId(long noticeId, long projectId);

    List<Notice> findByIdIn(List<Long> ids);
}
