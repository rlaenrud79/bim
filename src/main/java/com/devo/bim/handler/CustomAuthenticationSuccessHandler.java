package com.devo.bim.handler;

import com.devo.bim.service.AccessLogService;
import com.devo.bim.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 주의 : ProjectService 추가 금지
    //       ProjectService 를 사용하면 순환 참조 오류가 발생하므로 이곳에서는 ProjectService 사용하지 마세요
    private final RequestCache requestCache = new HttpSessionRequestCache();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final AccountService accountService;

    private final AccessLogService accessLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setUserInfo(request);
        setAccessLogAtLogin(request);
        setTargetUrl(request, response, "/main/index");
    }

    private void setTargetUrl(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws IOException {
        setDefaultTargetUrl(redirectUrl);
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if(savedRequest != null){
            String targetUrl = savedRequest.getRedirectUrl();

            if(targetUrl.split("/").length > 3) redirectStrategy.sendRedirect(request, response,targetUrl);
            else redirectStrategy.sendRedirect(request, response,getDefaultTargetUrl());
        } else redirectStrategy.sendRedirect(request, response,getDefaultTargetUrl());

    }

    private void setAccessLogAtLogin(HttpServletRequest request) {
        accessLogService.setAccessLog(request.getRequestURI());
    }

    private void setUserInfo(HttpServletRequest request) {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) email = ((UserDetails) principal).getUsername();
        else email = principal.toString();

        accountService.setUserInfo(email, request.getSession().getId());
    }
}
