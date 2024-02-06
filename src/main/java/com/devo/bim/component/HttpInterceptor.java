package com.devo.bim.component;

import com.devo.bim.exception.ServiceUnavailableException;
import com.devo.bim.component.NetworkDevice;
import com.devo.bim.model.dto.NetworkDeviceDTO;
import com.devo.bim.model.entity.ProjectLicense;
import com.devo.bim.repository.dsl.ProjectLicenseDslRepository;
import com.devo.bim.service.AccessLogService;
import com.devo.bim.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpInterceptor implements HandlerInterceptor {

    private final AccessLogService accessLogService;
    private final MessageSource messageSource;
    private final ConfigService configService;
    private final Proc proc;
    private final NetworkDevice networkDevice;
    private final ProjectLicenseDslRepository projectLicenseDslRepository;
    private final PasswordEncoder passwordEncoder;
    @Resource
    protected UserInfo userInfo;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String uri = request.getRequestURI();

        if(uri.contains("/file_upload")) return true;

        // 로그인 유저가 라이선스 프로젝트 사용자인지 여부 체크
        if(userInfo.getId() > 0 && !userInfo.isValidLicense()){
            response.sendError(503, messageSource.getMessage("system.exception.service_unavailable.not_valid_project", null, LocaleContextHolder.getLocale()) + " : userInfo.getId : " + userInfo.getId() + ", userInfo.isValidLicense : " + userInfo.isValidLicense());
            return false;
        }

        if(userInfo.getId() > 0 && !checkUserProjectLicense(userInfo)){
            //response.sendError(503, messageSource.getMessage("system.exception.service_unavailable.not_valid_project", null, LocaleContextHolder.getLocale()) + " : userInfo.getId : " + userInfo.getId() + ", userInfo.isValidLicense : " + userInfo.isValidLicense());
            //return false;
        }

        if (request.getHeader("x-requested-with") != null) {
            if (userInfo.getId() == 0 && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
                response.sendError(900, messageSource.getMessage("system.session.ajax_request_login_expired", null, LocaleContextHolder.getLocale()));
                return false;
            }
        }

        // 로그인 후 구입 제품 체크
        if (userInfo.getId() > 0 ) {
            if(uri.contains("/process/")) {
                if (configService.isProductLicense("PROCESS")) return true;
                response.sendError(403, messageSource.getMessage("system.http.interceptor.product_4d_do_not_purchase", null, LocaleContextHolder.getLocale()));
                return false;
            }

            if(uri.contains("/cost/")) {
                if (configService.isProductLicense("ESTIMATE")) return true;
                response.sendError(403, messageSource.getMessage("system.http.interceptor.product_5d_do_not_purchase", null, LocaleContextHolder.getLocale()));
                return false;
            }
        }

        return true;
    }

    public boolean checkUserProjectLicense(UserInfo userInfo){
        //1. 유저의 맥어드래스 가져오기
        List<NetworkDeviceDTO> networkDeviceDTOs = networkDevice.getNetworkDeviceDTO();

        for (int i = 0; i < networkDeviceDTOs.size(); i++){
            //2. project_license 테이블에서 맥어드레스에 해당하는 데이터 가져오기
            List<ProjectLicense> licenseInvalidCheck = findMacAddressInProjectLicenseList(networkDeviceDTOs.get(i).getMacAddress());
            System.out.println("networkDeviceDTOs.get(i).getMacAddress() : " + networkDeviceDTOs.get(i).getMacAddress());
            //3. project_license 테이블의 접근하고자 하는 프로젝트에 유저의 맥어드레스가 등록되어 있는지 확인
            for (int j = 0; j < licenseInvalidCheck.size(); j++){
                System.out.println("licenseInvalidCheck.get(j).getLicenseNo() : " + licenseInvalidCheck.get(j).getLicenseNo());
                if (passwordEncoder.matches(networkDeviceDTOs.get(i).getMacAddress(), licenseInvalidCheck.get(j).getLicenseNo())){
                    if (userInfo.getProjectId() == licenseInvalidCheck.get(j).getProject().getId()){
                        return true;
                    }
                }
            }
        }

        //4. 있다면 true 없으면 false 리턴
        return false;
    }

    public List<ProjectLicense> findMacAddressInProjectLicenseList(String macAddress) {
        return projectLicenseDslRepository.findMacAddressInProjectLicenseList(macAddress);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        accessLogService.setAccessLog(request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
    }
}
