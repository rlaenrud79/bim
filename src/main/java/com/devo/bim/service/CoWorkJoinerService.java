package com.devo.bim.service;

import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.repository.spring.CoWorkJoinerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoWorkJoinerService extends AbstractService {

    private final CoWorkJoinerRepository coWorkJoinerRepository;

    public List<AccountDTO> findAccountDTOByCoWorkId(long coWorkId){
        return coWorkJoinerRepository.findByCoWorkId(coWorkId).stream().map(o -> o.getAccountDTO()).collect(Collectors.toList());
    }
}
