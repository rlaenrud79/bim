package com.devo.bim.repository.dsl;

import com.devo.bim.model.dto.ConfigCustomDTO;
import com.devo.bim.model.vo.SearchConfigCustomVO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.devo.bim.model.entity.QConfig.config;
import static com.devo.bim.model.entity.QConfigCustom.configCustom;
import static com.devo.bim.model.entity.QConfigName.configName;

@Repository
@RequiredArgsConstructor
public class ConfigCustomDTODslRepository {
    private final JPAQueryFactory queryFactory;

    public ConfigCustomDTO findByConfigIdAndProjectId(SearchConfigCustomVO searchConfigCustomVO) {
        return queryFactory.select(Projections.constructor(ConfigCustomDTO.class
                        , config
                        , configCustom
                        , configName
                ))
                .from(config)
                .leftJoin(config.configCustoms, configCustom)
                .on(
                        getOnProjectIdEnabledEq(searchConfigCustomVO.getProjectId())
                )
                .leftJoin(config.configNames, configName)
                .on(
                        getOnConfigNameLanguageCodeEq(searchConfigCustomVO.getLang())
                )
                .where(
                        getWhereConfigIdEq(searchConfigCustomVO.getConfigId())
                )
                .fetchFirst();
    }

    private BooleanExpression getOnConfigNameLanguageCodeEq(String lang) {
        if (!StringUtils.isEmpty(lang)) return configName.languageCode.equalsIgnoreCase(lang);
        return null;
    }

    private BooleanExpression getWhereConfigIdEq(long configId) {
        return config.id.eq(configId);
    }

    private BooleanExpression getOnProjectIdEnabledEq(long projectId) {
        return configCustom.projectId.eq(projectId);
    }
}
