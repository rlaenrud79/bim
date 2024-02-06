package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.SearchUserDTO;
import com.devo.bim.repository.dsl.SearchUserDTODslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchUserService extends AbstractService {

    private final SearchUserDTODslRepository searchUserDTODslRepository;
    private final UserInfo userInfo;

    public List<SearchUserDTO> findSearchUserDTOs(){
        return searchUserDTODslRepository.findSearchUserDTOs(userInfo.getProjectId());
    }

    public List<SearchUserDTO> findSearchUserTextDTOs(String searchText){
        return searchUserDTODslRepository.findSearchUserTextDTOs(userInfo.getProjectId(), searchText);
    }

    public List<SearchUserDTO> findCoWorkJoinerDTOs(long coWorkId){
        return searchUserDTODslRepository.findCoWorkJoinerDTOs(userInfo.getProjectId(),coWorkId);
    }
}
