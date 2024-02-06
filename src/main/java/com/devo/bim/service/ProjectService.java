package com.devo.bim.service;

import com.devo.bim.component.NetworkDevice;
import com.devo.bim.model.dto.NetworkDeviceDTO;
import com.devo.bim.model.dto.ProjectDTO;
import com.devo.bim.model.entity.PayHistory;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.entity.ProjectLicense;
import com.devo.bim.model.vo.ProjectVO;
import com.devo.bim.repository.dsl.ProjectDslRepository;
import com.devo.bim.repository.spring.PayHistoryRepository;
import com.devo.bim.repository.spring.ProjectImageRepository;
import com.devo.bim.repository.spring.ProjectRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService extends AbstractService {
    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;
    private final PayHistoryRepository payHistoryRepository;
    private final FileDeleteService fileDeleteService;
    private final ProjectDslRepository projectDslRepository;
    private final NetworkDevice networkDevice;
    private final PasswordEncoder passwordEncoder;

    public Project findById() {
        return projectRepository.findById(userInfo.getProjectId()).orElseGet(Project::new);
    }

    public Project findMyProject() {
        return projectRepository.findMyProjectById(userInfo.getProjectId()).orElseGet(Project::new);
    }

    @Transactional
    public JsonObject deleteProjectImage(long projectImageId) {
        return projectImageRepository
                .findById(projectImageId)
                .map(savedItem -> {
                    try {
                        fileDeleteService.deletePhysicalFile(savedItem.getPath());
                        projectImageRepository.delete(savedItem);

                        return proc.getResult(true, "system.project_service.delete_project_image_file");
                    } catch (Exception e) {
                        return proc.getMessageResult(false, e.getMessage());
                    }
                })
                .orElseGet(() -> proc.getResult(false, "system.project_service.not_exist_project_image_file"));
    }

    @Transactional
    public JsonObject postPayHistory(BigDecimal totalPay, BigDecimal totalCost) {
        try {
            payHistoryRepository.save(new PayHistory(userInfo.getProjectId(), totalPay, totalCost, userInfo.getId()));
            return proc.getResult(true, "system.project_service.insert_pay_history_success");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putProjectContents(String contents) {
        return projectRepository
                .findById(userInfo.getProjectId())
                .map(savedItem -> {
                    try {
                        savedItem.putContents(contents);
                        return proc.getResult(true, "system.project_service.update_project_contents_success");
                    } catch (Exception e) {
                        return proc.getMessageResult(false, e.getMessage());
                    }
                })
                .orElseGet(() -> proc.getResult(false, "system.project_service.not_exist_project"));

    }

    @Transactional
    public JsonObject putProject(ProjectVO projectVO) {
        return projectRepository
                .findById(userInfo.getProjectId())
                .map(savedItem -> {
                    try {
                        savedItem.putProject(projectVO);
                        return proc.getResult(true);
                    } catch (Exception e) {
                        return proc.getMessageResult(false, e.getMessage());
                    }
                })
                .orElseGet(() -> proc.getResult(false, "system.project_service.not_exist_project"));
    }

    public List<Project> findEnabledProjectList() {
        LocalDateTime dateTime = LocalDateTime.now();
        return projectDslRepository.findEnabledProjectList(dateTime);
    }

    public List<Project> findAllProjectList() {
        return projectDslRepository.findAllProjectList();
    }

    @Transactional
    public void validateProjectLicense() {

        // 1. 프로젝트 리스트 전체 조회
        List<Project> savedProjects = findAllProjectList();

        // 2. mac address 리스트 조회(암호화 된 주소 리턴)
        List<NetworkDeviceDTO> networkDeviceDTOs = networkDevice.getNetworkDeviceDTO();

        // 3. mac address 로 라이선스 체크
        matchesSavedProjects(savedProjects, networkDeviceDTOs);
    }

    private void matchesSavedProjects(List<Project> savedProjects, List<NetworkDeviceDTO> networkDeviceDTOs) {
        savedProjects.forEach(t -> {
            // 프로젝트별 라이선스 비교
            matchesProjectLicense(networkDeviceDTOs, t);
        });
    }

    private void setProjectLicense(Project project, boolean value) {
        project.putIsValidLicense(value);
    }

    private void matchesProjectLicense(List<NetworkDeviceDTO> networkDeviceDTOs, Project project) {
        project.getProjectLicenses().forEach(s -> {
            // 프로젝트별 > 라이선스별 비교
            matchesNetworkDeviceDTO(networkDeviceDTOs, project, s);
        });
    }

    private void matchesNetworkDeviceDTO(List<NetworkDeviceDTO> networkDeviceDTOs, Project project, ProjectLicense projectLicense) {
        networkDeviceDTOs.forEach(o -> {
            // 프로젝트별 > 라이선스별 > mac address 별 비교
            matchesMacAddress(project, projectLicense, o);
        });
    }

    private void matchesMacAddress(Project project, ProjectLicense projectLicense, NetworkDeviceDTO networkDeviceDTO) {
        if(passwordEncoder.matches(networkDeviceDTO.getMacAddress(), projectLicense.getLicenseNo())) {
            setProjectLicense(project, true);
        }
    }

    public List<ProjectDTO> findAllProjectListForAdminSystem() {
        return projectDslRepository.findAllProjectListForAdminSystem();
    }
}
