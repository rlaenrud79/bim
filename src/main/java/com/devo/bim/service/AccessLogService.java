package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.AccessLog;
import com.devo.bim.model.entity.Menu;
import com.devo.bim.model.vo.SearchStatisticsUserVO;
import com.devo.bim.repository.dsl.AccessLogDslRepository;
import com.devo.bim.repository.spring.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessLogService  extends AbstractService {

    private final AccessLogRepository accessLogRepository;
    private final AccessLogDslRepository accessLogDslRepository;
    private final MenuService menuService;

    @Transactional
    public void setAccessLog(String uri) {
        if (userInfo.getId() == 0) return;

        String menuCode = "layout.common.menu" + uri.replace("/",".");

        List<Menu> menus = menuService.menuAll();

        for(Menu menu : menus )
        {
            if(menuCode.equals(menu.getCode())) {
                AccessLog newAccessLog = new AccessLog(menu.getCode(), userInfo.getId(), userInfo.getProjectId());
                accessLogRepository.save(newAccessLog);
            }
        }
    }

    public StatisticsLoginDTO getStatisticsLogin(String startDate, String endDate) {
         return accessLogDslRepository.findLoginByProjectIdAndAccessDateBetween(userInfo.getProjectId(),startDate,endDate);
    }

    public StatisticsMenuDTO getStatisticsMenu(String startDate, String endDate) {
        return accessLogDslRepository.findMenuByProjectIdAndAccessDateBetween(userInfo.getProjectId(),startDate,endDate);
    }

    public PageDTO<StatisticsUserDTO> getStatisticsUser(SearchStatisticsUserVO searchStatisticsUserVO, Pageable pageable) {
        return accessLogDslRepository.findAccountDTOsByProjectId(searchStatisticsUserVO, userInfo.getProjectId(), pageable);
    }

    public List<StatisticsUserDTO> getStatisticsUserBySearchCondition(SearchStatisticsUserVO searchStatisticsUserVO) {
        return accessLogDslRepository.findAccountDTOsBySearchCondition(searchStatisticsUserVO, userInfo.getProjectId());
    }
}
