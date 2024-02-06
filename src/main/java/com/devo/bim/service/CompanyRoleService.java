package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.CompanyRoleDTO;
import com.devo.bim.model.vo.SearchCompanyRoleVO;
import com.devo.bim.model.entity.CompanyRole;
import com.devo.bim.model.entity.CompanyRoleName;
import com.devo.bim.model.vo.CompanyRoleVO;
import com.devo.bim.repository.dsl.CompanyRoleDTODslRepository;
import com.devo.bim.repository.dsl.CompanyRoleDslRepository;
import com.devo.bim.repository.dsl.CompanyRoleNameDslRepository;
import com.devo.bim.repository.spring.CompanyRoleNameRepository;
import com.devo.bim.repository.spring.CompanyRoleRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyRoleService  extends AbstractService {

    private final CompanyRoleDTODslRepository companyRoleDtoDslRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final CompanyRoleDslRepository companyRoleDslRepository;
    private final CompanyRoleNameRepository companyRoleNameRepository;
    private final CompanyRoleNameDslRepository companyRoleNameDslRepository;

    public List<CompanyRoleDTO> findCompanyRoleDTOs(SearchCompanyRoleVO searchCompanyRoleVO){
        return companyRoleDtoDslRepository.findCompanyRoleDTOs(searchCompanyRoleVO);
    }

    public List<CompanyRole> findUseCompanyRoleAll(){
        return companyRoleDslRepository.findAllByProjectId(userInfo.getProjectId());
    }

    public CompanyRole findById(long companyRoleId){
        return companyRoleRepository.findByIdAndProjectId(companyRoleId, userInfo.getProjectId()).orElseGet(CompanyRole::new);
    }

    public CompanyRole findBySortNo(int sortNo){
        return companyRoleRepository.findByProjectIdAndSortNo(userInfo.getProjectId(), sortNo).orElseGet(CompanyRole::new);
    }

    @Transactional
    public JsonObject postCompanyRoleAndCompanyRoleName(CompanyRoleVO companyRoleVO){
        // 등록권한 체크
        if(!haveRightForAddUpdateCompanyRole()) return proc.getResult(false, "system.admin_service.post_company_role.no_have_right_add");

        try {
            String enRoleName = companyRoleVO.getCompanyRoleNames()
                    .stream()
                    .filter(t-> "EN".equalsIgnoreCase(t.getLanguageCode())).findFirst()
                    .get()
                    .getName();

            // 기존 sortNo 사이로 추가되는 경우 신규 sortNo 보다 같거나 큰 CompanyRole 순서 조정
            companyRoleRepository.addOneGOESortNo(userInfo.getProjectId(), companyRoleVO.getSortNo());

            long companyRoleId = addCompanyRole(companyRoleVO.getSortNo(), enRoleName, userInfo);
            addCompanyRoleNames(companyRoleVO.getCompanyRoleNames(), companyRoleId);
            return proc.getResult(true, "system.admin_service.post_company_role");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private long addCompanyRole(int sortNo, String engRoleName, UserInfo userInfo) {
        CompanyRole companyRole = new CompanyRole();
        companyRole.setCompanyRoleAtAddCompanyRole(userInfo.getProjectId(), sortNo, engRoleName, userInfo.getId());
        return companyRoleRepository.save(companyRole).getId();
    }

    private void addCompanyRoleNames(List<CompanyRoleName> companyRoleNames, long companyRoleId) {
        for (CompanyRoleName companyRoleName : companyRoleNames) {
            companyRoleName.setCompanyRoleId(companyRoleId);
            companyRoleNameRepository.save(companyRoleName);
        }
    }

    private boolean haveRightForAddUpdateCompanyRole(){
        if(userInfo.isRoleAdminProject()) return true;
        return false;
    }

    @Transactional
    public JsonObject putCompanyRoleAndCompanyRoleName(CompanyRoleVO companyRoleVO){

        long projectId = userInfo.getProjectId();

        // 1. CompanyRole 조회
        CompanyRole savedCompanyRole = companyRoleRepository.findByIdAndProjectId(companyRoleVO.getId(), projectId).orElseGet(CompanyRole::new);
        if(savedCompanyRole.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_company_role");

        if(!haveRightForAddUpdateCompanyRole()) return proc.getResult(false, "system.admin_service.put_company_role.no_have_right_update");

        try {

            // 2.1. SortNo 변화에 따른 조정 실시, sortNo 1, 2, 3, 4, 5 중 2를 4의 자리로 이동하는 경우 minusOneGTSavedSortNoAndLOESortNo(2, 4)
            if(savedCompanyRole.getSortNo() < companyRoleVO.getSortNo()) companyRoleRepository.minusOneGTSavedSortNoAndLOESortNo(projectId, savedCompanyRole.getSortNo(), companyRoleVO.getSortNo());
            // 2.2. SortNo 변화에 따른 조정 실시, sortNo 1, 2, 3, 4, 5 중 4를 2의 자리로 이동하는 경우 addOneGOESortNoAndLTSavedSortNo(4, 2)
            if(savedCompanyRole.getSortNo() > companyRoleVO.getSortNo()) companyRoleRepository.addOneLTSavedSortNoAndGOESortNo(projectId, savedCompanyRole.getSortNo(), companyRoleVO.getSortNo());

            updateCompanyRole(companyRoleVO.getSortNo(), companyRoleVO.getCompanyRoleName("EN"), userInfo, savedCompanyRole);
            updateCompanyRoleName(companyRoleVO.getCompanyRoleNames(), savedCompanyRole);

            return proc.getResult(true, "system.admin_service.put_company_role");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private int getNewSortNo(String newSortNo){
        return Integer.parseInt(newSortNo);
    }

    private void updateCompanyRoleName(List<CompanyRoleName> companyRoleNames, CompanyRole savedCompanyRole) {
        for (CompanyRoleName companyRoleName : companyRoleNames) {
            CompanyRoleName savedCompanyRoleName = companyRoleNameDslRepository.findCompanyRoleNameByCompanyRoleIdAndLanguageCode(savedCompanyRole.getId(), companyRoleName.getLanguageCode());
            if(savedCompanyRoleName == null) savedCompanyRoleName = new CompanyRoleName();
            savedCompanyRoleName.setNameAtUpdateCompanyRole(companyRoleName.getLanguageCode(), companyRoleName.getName());
            companyRoleNameRepository.save(savedCompanyRoleName);
        }
    }

    private void updateCompanyRole(int newSortNo, String engRoleName, UserInfo userInfo, CompanyRole savedCompanyRole) {
        savedCompanyRole.setCompanyRoleAtUpdateCompanyRole(newSortNo, engRoleName, userInfo.getId());
        companyRoleRepository.save(savedCompanyRole);
    }

    @Transactional
    public JsonObject putEnabledCompanyRole(long companyRoleId, String setType){

        // 1. CompanyRole 조회
        CompanyRole savedCompanyRole = companyRoleRepository.findById(companyRoleId).orElseGet(CompanyRole::new);
        if(savedCompanyRole.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_company_role");

        try {
            String propertiesCode = "";
            // 2-1. Enabled 처리
            if("ON".equals(setType.toUpperCase())) {
                savedCompanyRole.setEnabledCompanyRole(true);
                propertiesCode = "system.admin_service.put_enabled_company_role_true";
            }
            // 2-2. Disabled 처리
            if("OFF".equals(setType.toUpperCase())) {
                savedCompanyRole.setEnabledCompanyRole(false);
                propertiesCode = "system.admin_service.put_enabled_company_role_false";
            }

            return proc.getResult(true, propertiesCode);
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putCompanyRoleSortNoDESC(long companyRoleId){
        long projectId = userInfo.getProjectId();

        // 1. CompanyRole 조회
        CompanyRole savedCompanyRole = companyRoleRepository.findByIdAndProjectId(companyRoleId, projectId).orElseGet(CompanyRole::new);
        if(savedCompanyRole.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_company_role");

        // 2. sortNo 뒷자리 CompanyRole 조회
        CompanyRole savedCompanyRoleNext = companyRoleDslRepository.findNextCompanyRoleBySortNo(projectId, savedCompanyRole.getSortNo());

        try {
            // 3. sortNo Swap
            int tmpSortNo = savedCompanyRole.getSortNo();
            savedCompanyRole.setSortNoAtAdminService(savedCompanyRoleNext.getSortNo());
            savedCompanyRoleNext.setSortNoAtAdminService(tmpSortNo);
            return proc.getResult(true, "system.admin_service.put_company_role_sort_no_asc");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }

    }

    @Transactional
    public JsonObject putCompanyRoleSortNoASC(long companyRoleId){

        long projectId = userInfo.getProjectId();

        // 1. CompanyRole 조회
        CompanyRole savedCompanyRole = companyRoleRepository.findByIdAndProjectId(companyRoleId, projectId).orElseGet(CompanyRole::new);
        if(savedCompanyRole.getId() == 0) return proc.getResult(false, "system.admin_service.error_no_exist_company_role");

        // 2. sortNo 앞자리 CompanyRole 조회
        CompanyRole savedCompanyRolePrevious = companyRoleDslRepository.findPreviousCompanyRoleBySortNo(projectId, savedCompanyRole.getSortNo());

        try {
            // 3. sortNo Swap
            int tmpSortNo = savedCompanyRole.getSortNo();
            savedCompanyRole.setSortNoAtAdminService(savedCompanyRolePrevious.getSortNo());
            savedCompanyRolePrevious.setSortNoAtAdminService(tmpSortNo);
            return proc.getResult(true, "system.admin_service.put_company_role_sort_no_desc");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
