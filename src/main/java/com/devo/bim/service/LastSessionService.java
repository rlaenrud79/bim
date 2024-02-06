package com.devo.bim.service;

import com.devo.bim.model.entity.LastSession;
import com.devo.bim.repository.spring.LastSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LastSessionService extends AbstractService {

    private final LastSessionRepository lastSessionRepository;

    public boolean isSameSession(long accountId, long projectId, String sessionId){
        LastSession savedLastSession = lastSessionRepository.findByAccountIdAndProjectId(userInfo.getId(), userInfo.getProjectId()).orElseGet(LastSession::new);
        if(sessionId.equalsIgnoreCase(savedLastSession.getLastSessionId())) return true;
        return false;
    }

    @Transactional
    public void setNowSessionId(String sessionId) {
        LastSession savedLastSession = lastSessionRepository.findByAccountIdAndProjectId(userInfo.getId(), userInfo.getProjectId()).orElseGet(LastSession::new);

        if (savedLastSession.getId() == 0) savedLastSession.setLsatSession(userInfo.getId(), userInfo.getProjectId(), sessionId);
        else savedLastSession.setLastSessionId(sessionId);

        lastSessionRepository.save(savedLastSession);
    }
}
