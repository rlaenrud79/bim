package com.devo.bim.service;

import com.devo.bim.model.dto.ConfigCustomDTO;
import com.devo.bim.model.entity.ConfigCustom;
import com.devo.bim.model.vo.ConfigCustomVO;
import com.devo.bim.model.vo.SearchConfigCustomVO;
import com.devo.bim.repository.dsl.ConfigCustomDTODslRepository;
import com.devo.bim.repository.spring.ConfigCustomRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigCustomService extends AbstractService {

    private final ConfigCustomDTODslRepository configCustomDTODslRepository;
    private final ConfigCustomRepository configCustomRepository;

    public ConfigCustomDTO findConfigCustom(SearchConfigCustomVO searchConfigCustomVO) {
        return configCustomDTODslRepository.findByConfigIdAndProjectId(searchConfigCustomVO);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projectCustomConfig", allEntries=true),
    })
    public JsonObject postConfigCustom(ConfigCustomVO configCustomVO) {
        try {
            ConfigCustom configCustom = new ConfigCustom();
            configCustom.setConfigCustomAtAddConfigCustom(userInfo, configCustomVO);
            configCustomRepository.save(configCustom);
            return proc.getResult(true, "system.config_custom_service.post_config_custom");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projectCustomConfig", allEntries=true),
    })
    public JsonObject putConfigCustom(ConfigCustomVO configCustomVO) {
        ConfigCustom savedConfigCustom = configCustomRepository.findById(configCustomVO.getId()).orElseGet(ConfigCustom::new);
        if (savedConfigCustom.getId() == 0) {
            return proc.getResult(false, "system.config_custom_service.error_no_exist_config_custom");
        }

        try {
            savedConfigCustom.setConfigCustomAtUpdateConfigCustom(configCustomVO);
            return proc.getResult(true, "system.config_custom_service.put_config_custom");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projectCustomConfig", allEntries=true),
    })
    public JsonObject deleteConfigCustom(long id) {
        ConfigCustom savedConfigCustom = configCustomRepository.findById(id).orElseGet(ConfigCustom::new);
        if (savedConfigCustom.getId() == 0) {
            return proc.getResult(false, "system.config_custom_service.error_no_exist_config_custom");
        }

        try {
            configCustomRepository.delete(savedConfigCustom);
            return proc.getResult(true, "system.config_custom_service.delete_config_custom");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
