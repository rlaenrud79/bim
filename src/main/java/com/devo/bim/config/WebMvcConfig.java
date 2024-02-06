package com.devo.bim.config;

import com.devo.bim.component.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${system.path.win_upload}")
    protected String winPathUpload;

    @Value("${system.path.linux_upload}")
    protected String linuxPathUpload;

    @Value("${system.path.mac_upload}")
    protected String macPathUpload;

    @Autowired
    @Qualifier(value = "httpInterceptor")
    private HandlerInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/plugins/**")
                .excludePathPatterns("/dist/**")
                .excludePathPatterns("/error/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file_upload/**")
                .addResourceLocations("file:///" + Utils.getSaveBasePath(winPathUpload,linuxPathUpload,macPathUpload) + "/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

}