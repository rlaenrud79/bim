package com.devo.bim.service;

import com.devo.bim.model.entity.RoleName;
import com.devo.bim.model.enumulator.RoleCode;
import com.devo.bim.repository.spring.RoleNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService extends AbstractService {
    private final RoleNameRepository roleNameRepository;
    private final ConfigService configService;
    private final String systemCode = "SYSTEM";

    public List<RoleName> findByLanguageCode()
    {
        return roleNameRepository.findByLanguageCode(getLanguage());
    }

    public List<RoleName> getProductRoleNameList(boolean isAdminSystem)
    {
        List<RoleName> roleNameList = findByLanguageCode();
        if (roleNameList.size() == 0) {
            return new ArrayList<>();
        }

        String projectMenus = configService.findConfigForCache(systemCode, "PROJECT_MENU_LIST", userInfo.getProjectId());
        if(isAdminSystem) {
            if(projectMenus.isBlank()) projectMenus = systemCode;
            else projectMenus += ", "+systemCode;
        }

        if (projectMenus.length() < 1) {
            return new ArrayList<>();
        }

        List<String> projectMenuList = Arrays.stream(projectMenus.replace(" ", "").split(",")).collect(Collectors.toList());

        return roleNameList
                .stream()
                .filter(t -> projectMenuList.stream().anyMatch(c -> c.equals(t.getRole().getCode().toString().split("_")[2])))
                .collect(Collectors.toList());
    }

    public String getValidRoleString(String selectedRoles, boolean isAdminSystem)
    {
        List<RoleName> roleNameList = getProductRoleNameList(isAdminSystem);
        List<String> roleIdList = Arrays.stream(selectedRoles.replace(" ", "").split(",")).collect(Collectors.toList());

        if(isAdminSystem) {
            RoleName roleName = roleNameList.stream().filter(t->t.getRole().getCode() == RoleCode.ROLE_ADMIN_SYSTEM).findFirst().get();
            if(roleIdList.stream().filter(s->s.equals(roleName.getId() + "")).count() == 0) roleIdList.add( roleName.getId() + "");
        }

        return roleNameList
                .stream()
                .filter(t -> roleIdList.stream().anyMatch(c -> c.equals(t.getRole().getId()+"")))
                .collect(Collectors.toList())
                .stream()
                .map(i -> i.getId()+"")
                .collect(Collectors.joining(","));
    }
}
