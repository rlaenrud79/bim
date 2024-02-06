package com.devo.bim.service;

import com.devo.bim.model.dto.CompanyDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Company;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.entity.WorkPlan;
import com.devo.bim.model.vo.SearchCompanyVO;
import com.devo.bim.repository.dsl.CompanyDTODslRepository;
import com.devo.bim.repository.spring.CompanyRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService extends AbstractService {
    private final CompanyRepository companyRepository;
    private final CompanyDTODslRepository companyDTODslRepository;

    public List<Company> findByProjectId()
    {
        return companyRepository.findByProjectId(userInfo.getProjectId());
    }

    public PageDTO<CompanyDTO> findCompanyDTOs(SearchCompanyVO searchCompanyVO, Pageable pageable){
        return getCompanyDTOsSortPage(searchCompanyVO.getSortProp(), companyDTODslRepository.findCompanyDTOs(searchCompanyVO), pageable);
    }

    public List<CompanyDTO> findCompanyDTOs(SearchCompanyVO searchCompanyVO){
        return getSortedCompanyDTOs(searchCompanyVO.getSortProp(), companyDTODslRepository.findCompanyDTOs(searchCompanyVO));
    }

    private PageDTO<CompanyDTO> getCompanyDTOsSortPage(String sortProp, List<CompanyDTO> companyDTOS, Pageable pageable) {
        List<CompanyDTO> sortedCompanyDTOs = getSortedCompanyDTOs(sortProp, companyDTOS);
        return new PageDTO<>(sortedCompanyDTOs.subList(getStartPagingIndex(pageable), getEndPagingIndex(pageable, sortedCompanyDTOs.size())) , pageable, sortedCompanyDTOs.size());
    }

    private int getEndPagingIndex(Pageable pageable, int totalSize) {
        if((pageable.getPageNumber() + 1) * pageable.getPageSize() > totalSize) return totalSize;
        return (pageable.getPageNumber() + 1) * pageable.getPageSize();
    }

    private int getStartPagingIndex(Pageable pageable) {
        return pageable.getPageNumber() * pageable.getPageSize();
    }

    private List<CompanyDTO> getSortedCompanyDTOs(String sortProp, List<CompanyDTO> companyDTOS) {
        if ("companyNameASC".equalsIgnoreCase(sortProp)) return getCompanyNameSortASC(companyDTOS);
        if ("companyNameDESC".equalsIgnoreCase(sortProp)) return getCompanyNameSortDESC(companyDTOS);

        if ("responsibleUserNameASC".equalsIgnoreCase(sortProp)) return getResponsibleUserNameSortASC(companyDTOS);
        if ("responsibleUserNameDESC".equalsIgnoreCase(sortProp)) return getResponsibleUserNameSortDESC(companyDTOS);

        if ("companyRoleNameASC".equalsIgnoreCase(sortProp)) return getCompanyRoleNameSortASC(companyDTOS);
        if ("companyRoleNameDESC".equalsIgnoreCase(sortProp)) return getCompanyRoleNameSortDESC(companyDTOS);

        if ("worksNamesASC".equalsIgnoreCase(sortProp)) return getWorksNamesSortASC(companyDTOS);
        if ("worksNamesDESC".equalsIgnoreCase(sortProp)) return getWorksNamesSortDESC(companyDTOS);

        if ("isDisplayASC".equalsIgnoreCase(sortProp)) return getIsDisplaySortASC(companyDTOS);
        if ("isDisplayDESC".equalsIgnoreCase(sortProp)) return getIsDisplaySortDESC(companyDTOS);

        if ("statusASC".equalsIgnoreCase(sortProp)) return getStatusSortASC(companyDTOS);
        if ("statusDESC".equalsIgnoreCase(sortProp)) return getStatusSortDESC(companyDTOS);

        if ("lastModifyDateASC".equalsIgnoreCase(sortProp)) return getLastModifyDateSortASC(companyDTOS);
        if ("lastModifyDateDESC".equalsIgnoreCase(sortProp)) return getLastModifyDateSortDESC(companyDTOS);

        return companyDTOS;
    }

    @NotNull
    private List<CompanyDTO> getLastModifyDateSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getLastModifyDate).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getLastModifyDateSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getLastModifyDate)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getStatusSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getStatus).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getStatusSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getStatus)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getIsDisplaySortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::isDisplay).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getIsDisplaySortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::isDisplay)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getWorksNamesSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getWorksNames).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getWorksNamesSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getWorksNames)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getCompanyRoleNameSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getCompanyRoleName).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getCompanyRoleNameSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getCompanyRoleName)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getResponsibleUserNameSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getResponsibleUserName).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getResponsibleUserNameSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getResponsibleUserName)).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getCompanyNameSortDESC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getName).reversed()).collect(Collectors.toList());
    }

    @NotNull
    private List<CompanyDTO> getCompanyNameSortASC(List<CompanyDTO> companyDTOS) {
        return companyDTOS.stream().sorted(Comparator.comparing(CompanyDTO::getName)).collect(Collectors.toList());
    }

    public Company findById(long companyId){
        return companyRepository.findByIdAndProjectId(companyId, userInfo.getProjectId()).orElseGet(Company::new);
    }

    @Transactional
    public JsonObject postCompany(Company company){
        // 1. 회사 등록 권한 체크
        if(!haveRightForAddCompany()) return proc.getResult(false, "system.company_service.add_company.no_have_right_add_company");

        try{
            // 2. company 저장
            companyRepository.save(company);

            return proc.getResult(true, "system.company_service.post_company_success");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putCompany(Company company){
        // 1. 회사 조회
        Company savedCompany = companyRepository.findByIdAndProjectId(company.getId(), userInfo.getProjectId()).orElseGet(Company::new);
        if(savedCompany.getId() == 0) return proc.getResult(false, "system.company_service.update_company.no_company");

        // 2. 권한 체크
        if(!haveRightForAddCompany()) return proc.getResult(false, "system.company_service.update_company.no_have_right_update_company");

        try{

            // 3. company data 변환
            savedCompany.setCompanyAtPutCompany(company);

            return proc.getResult(true, "system.company_service.put_company_success");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean haveRightForAddCompany(){
        if(userInfo.isRoleAdminProject()) return true;
        return false;
    }

    public JsonObject deleteCompany(long id) {
        Company savedCompany = companyRepository.findById(id).orElseGet(Company::new);
        if (savedCompany.getId() == 0) {
            return proc.getResult(false, "admin.company_list.not_exist_company");
        }

        try {
            companyRepository.delete(savedCompany);

            return proc.getResult(true, "admin.company_list.delete_company");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
