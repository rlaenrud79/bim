package com.devo.bim.service;

import com.devo.bim.model.entity.CoWorkIssue;
import com.devo.bim.repository.spring.CoWorkIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoWorkIssueService extends AbstractService {

    private final CoWorkIssueRepository coWorkIssueRepository;

    public List<CoWorkIssue> findByCoWorkId(long coWorkId){
        return coWorkIssueRepository.findByCoWorkId(coWorkId);
    }

    public CoWorkIssue findByCoWorkIssueId(long coWorkIssueId) {
        return coWorkIssueRepository.findCoWorkById(coWorkIssueId).orElseGet(CoWorkIssue::new);
    }
}
