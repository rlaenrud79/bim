package com.devo.bim.service;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.ConfigTreeDTO;
import com.devo.bim.model.entity.Config;
import com.devo.bim.model.entity.ConfigCustom;
import com.devo.bim.model.entity.ConfigGroup;
import com.devo.bim.model.entity.WeatherLocation;
import com.devo.bim.model.enumulator.DateFormatEnum;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.model.enumulator.PageSizeUseType;
import com.devo.bim.repository.spring.ConfigGroupRepository;
import com.devo.bim.repository.spring.ConfigRepository;
import com.devo.bim.repository.spring.WeatherLocationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService extends AbstractService {
    private final ConfigGroupRepository configGroupRepository;
    private final ConfigRepository configRepository;
    private final WeatherLocationRepository weatherLocationRepository;

    @Cacheable(value = "config")
    public List<Config> configAll() {
        return configRepository.findAll();
    }

    @Cacheable(value = "configGroup")
    public List<ConfigGroup> configGroupAll() {
        return configGroupRepository.findAll();
    }

    public ConfigCustom findConfig(String configGroupCode, String configCode) throws Exception {
        return findConfig (configGroupCode, configCode, userInfo.getProjectId());
    }

    private ConfigCustom findConfig(String configGroupCode, String configCode, long projectId) throws Exception {
        ConfigGroup configGroup = configGroupAll()
                .stream()
                .filter(t -> t.getCode().equals(configGroupCode))
                .findFirst()
                .orElseGet(ConfigGroup::new);

        if (configGroup.getId() == 0) throw new Exception(configGroupCode + ":" + message.getMessage("system.config_service.group_is_not"));

        Config config = configGroup.getConfigs()
                .stream()
                .filter(t -> t.getCode().equals(configCode))
                .findFirst()
                .orElseGet(Config::new);

        if (config.getId() == 0) throw new Exception(configCode + ":" + message.getMessage("system.config_service.config_is_not"));

        return config.getConfigCustoms()
                .stream()
                .filter(t -> t.getProjectId() == projectId)
                .findFirst()
                .orElseGet(()->new ConfigCustom(config));
    }

    @Cacheable(value = "weatherLocation")
    public List<WeatherLocation> findWeatherLocation() {
        return weatherLocationRepository.findAll();
    }

    @Cacheable(value = "projectCustomConfig")
    public String findConfigForCache(String configGroupCode, String configCode, long projectId){
        try {
            return  StringUtils.trim(findConfig(configGroupCode, configCode, projectId).getCustomValue());
        }
        catch (Exception e) {}
        return "";
    }

    public String getTelOrMobileNoPattern(String codeValue, long projectId){
        try {
            return  StringUtils.trim(findConfig("LOCALE", codeValue, projectId).getCustomValue());
        }
        catch (Exception e){}
        return "";
    }

    public String[] findFileExtension(String codeValue, boolean isDeleteAsterisk, long projectId){
        try {
            String fileExtension =  findConfig("FILE_EXT", codeValue, projectId).getCustomValue();
            if(isDeleteAsterisk) return fileExtension.replace(" ", "").replace("*.", "").split(",");
            return fileExtension.replace(" ", "").replace("*", "").split(",");
        }
        catch (Exception e){}
        return null;
    }

    public String getDateFormatter(Locale locale, DateFormatEnum dateFormatEnum){
        if(locale.getLanguage().equals(Locale.KOREAN) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC) return getConfigByCodeValue("DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN_SEC").getDefaultValue();
        if(locale.getLanguage().equals(Locale.KOREAN) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN) return getConfigByCodeValue("DATE_FORMAT_KR_YEAR_MONTH_DAY_HOUR_MIN").getDefaultValue();
        if(locale.getLanguage().equals(Locale.KOREAN) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY) return getConfigByCodeValue("DATE_FORMAT_KR_YEAR_MONTH_DAY").getDefaultValue();
        if(locale.getLanguage().equals(Locale.KOREAN) && dateFormatEnum == DateFormatEnum.GANTT) return getConfigByCodeValue("DATE_FORMAT_KR_YEAR_MONTH_DAY").getDefaultValue();

        if(locale.getLanguage().equals(Locale.ENGLISH) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC) return getConfigByCodeValue("DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN_SEC").getDefaultValue();
        if(locale.getLanguage().equals(Locale.ENGLISH) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN) return getConfigByCodeValue("DATE_FORMAT_US_YEAR_MONTH_DAY_HOUR_MIN").getDefaultValue();
        if(locale.getLanguage().equals(Locale.ENGLISH) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY) return getConfigByCodeValue("DATE_FORMAT_US_YEAR_MONTH_DAY").getDefaultValue();
        if(locale.getLanguage().equals(Locale.ENGLISH) && dateFormatEnum == DateFormatEnum.GANTT) return getConfigByCodeValue("DATE_FORMAT_US_YEAR_MONTH_DAY").getDefaultValue();

        if(locale.getLanguage().equals(Locale.CHINESE) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN_SEC) return getConfigByCodeValue("DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN_SEC").getDefaultValue();
        if(locale.getLanguage().equals(Locale.CHINESE) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY_HOUR_MIN) return getConfigByCodeValue("DATE_FORMAT_CN_YEAR_MONTH_DAY_HOUR_MIN").getDefaultValue();
        if(locale.getLanguage().equals(Locale.CHINESE) && dateFormatEnum == DateFormatEnum.YEAR_MONTH_DAY) return getConfigByCodeValue("DATE_FORMAT_CN_YEAR_MONTH_DAY").getDefaultValue();
        if(locale.getLanguage().equals(Locale.CHINESE) && dateFormatEnum == DateFormatEnum.GANTT) return getConfigByCodeValue("DATE_FORMAT_CN_YEAR_MONTH_DAY").getDefaultValue();

        return getConfigByCodeValue("DATE_FORMAT_KR_YEAR_MONTH_DAY").getDefaultValue();
    }

    // region list 리턴형
    public List<Config> findScheduleConfigs(){
        List<Config> configs = getConfigsByConfigGroupCode("SCHEDULE");
        return getConfigsByCompareCustomConfigBoolean(configs);
    }

    public List<String> findPagingSizeConfigs(){
        List<Config> configs = getConfigsByConfigGroupCode("PAGING_SIZE");

        return getConfigsByCompareCustomConfigBoolean(configs)
                .stream().map( o -> o.getLocaleName())
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Config> getConfigsByConfigGroupCode(String groupCode) {
        return configAll().stream()
                .filter(t -> t.getConfigGroup().getCode().equalsIgnoreCase(groupCode))
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Config> getConfigsByCompareCustomConfigBoolean(List<Config> configs) {
        List<Config> newConfigs = new ArrayList<>();

        for (Config config : configs) {
            ConfigCustom customConfig = getCustomConfig(config, userInfo.getProjectId());
            if (customConfig.getId() != 0 && "true".equalsIgnoreCase(customConfig.getCustomValue())) newConfigs.add(config);
            if (customConfig.getId() == 0 && "true".equalsIgnoreCase(config.getDefaultValue())) newConfigs.add(config);
        }

        return newConfigs.stream()
                .sorted(Comparator.comparingInt(Config::getSortNo))
                .collect(Collectors.toList());
    }

    @NotNull
    private ConfigCustom getCustomConfig(Config config, long projectId) {
        return config.getConfigCustoms()
                .stream()
                .filter(t -> t.getProjectId() == projectId)
                .findFirst()
                .orElseGet(ConfigCustom::new);
    }

    // endregion list 리턴형

    // region config 검색
    public String defaultSelectedLanguageConfigs(){
        return getConfigByCodeValue("LANGUAGE").getDefaultValue().replace(" ", "");
    }

    @NotNull
    private Config getConfigByCodeValue(String codeValue) {
        return configAll().stream()
                .filter(t -> t.getCode().equalsIgnoreCase(codeValue))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(Config::getSortNo).reversed())
                .collect(Collectors.toList())
                .stream()
                .findFirst()
                .orElseGet(Config::new);
    }
    // endregion config 검색

    private List<WeatherLocation> findWeatherLocationRegion() {
        return findWeatherLocation()
                .stream()
                .filter(Utils.distinctByKey(WeatherLocation::getRegionFirst))
                .sorted(Comparator.comparing(WeatherLocation::getRegionFirst))
                .collect(Collectors.toList());
    }

    private List<WeatherLocation> findWeatherLocationRegion(String region1Name) {

        return findWeatherLocation()
                .stream()
                .filter(t -> t.getRegionSecond() != null && region1Name.equals(t.getRegionFirst()))
                .filter(Utils.distinctByKey(WeatherLocation::getRegionSecond))
                .sorted(Comparator.comparing(WeatherLocation::getRegionSecond))
                .collect(Collectors.toList());
    }

    private List<WeatherLocation> findWeatherLocationRegion(String region1Name, String region2Name) {
        return findWeatherLocation()
                .stream()
                .filter(t -> t.getRegionThird() != null && region1Name.equals(t.getRegionFirst()) && region2Name.equals(t.getRegionSecond()))
                .sorted(Comparator.comparing(WeatherLocation::getRegionThird))
                .collect(Collectors.toList());
    }

    public List<WeatherLocation> findWeatherLocationRegion(String step, String region1Name, String region2Name) {
        if ("1".equals(step)) return findWeatherLocationRegion();
        if (region1Name.isBlank()) return new ArrayList<>();
        if ("2".equals(step)) return findWeatherLocationRegion(region1Name);
        if (region2Name.isBlank()) return new ArrayList<>();
        if ("3".equals(step)) return findWeatherLocationRegion(region1Name, region2Name);

        return new ArrayList<>();
    }

    public List<ConfigTreeDTO> findConfigTree() {
        List<Config> configs = configAll();
        List<ConfigGroup> configGroups = configGroupAll();

        List<ConfigTreeDTO> configTreeDTOs = new ArrayList<>();

        for (ConfigGroup configGroup : configGroups) {
            ConfigTreeDTO configTreeDTO = new ConfigTreeDTO();
            configTreeDTO.setId(configGroup.getId());
            configTreeDTO.setCode(configGroup.getCode());
            configTreeDTO.setSort(configGroup.getSortNo());

            List<Config> targetConfigs = configs.stream()
                    .filter(Config::isEnabled)
                    .filter(t -> t.getConfigGroup().getId() == configGroup.getId())
                    .collect(Collectors.toList());

            List<ConfigTreeDTO.ConfigTreeSubDTO> configSubDTOs = new ArrayList<>();
            for (Config config : targetConfigs) {
                ConfigTreeDTO.ConfigTreeSubDTO configSubDTO = new ConfigTreeDTO.ConfigTreeSubDTO();
                configSubDTO.setId(config.getId());
                configSubDTO.setCode(config.getCode());
                configSubDTO.setSort(config.getSortNo());

                configSubDTOs.add(configSubDTO);
            }

            configTreeDTO.setConfigs(configSubDTOs.stream().sorted(Comparator.comparing(ConfigTreeDTO.ConfigTreeSubDTO::getSort)).collect(Collectors.toList()));
            configTreeDTOs.add(configTreeDTO);
        }
        return configTreeDTOs;
    }

    @Transactional
    public String findWeatherSourceConfig(long projectId) {
        Config config = getConfigByCodeValue("WEATHER_SOURCE");
        ConfigCustom configCustom = getCustomConfig(config, projectId);

        if (configCustom.getId() != 0 && !StringUtils.isEmpty(configCustom.getCustomValue())) return configCustom.getCustomValue();
        return config.getDefaultValue();
    }

    public String findWeatherDisplayConfig(long projectId) {
        Config config = getConfigByCodeValue("WEATHER_DISPLAY");
        ConfigCustom configCustom = getCustomConfig(config, projectId);

        if (configCustom.getId() != 0 && !StringUtils.isEmpty(configCustom.getCustomValue())) return configCustom.getCustomValue();
        return config.getDefaultValue();
    }

    public boolean isProductLicense(String process) {
        try {
            String productLicenses = findConfig("SYSTEM", "PROJECT_MENU_LIST").getCustomValue();
            return productLicenses.contains(process);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
