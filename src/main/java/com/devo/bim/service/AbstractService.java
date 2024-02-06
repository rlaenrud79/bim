package com.devo.bim.service;

import com.devo.bim.component.Message;
import com.devo.bim.component.Proc;
import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Locale;

@Service
public class AbstractService {

    @Value("${system.path.win_upload}")
    protected String winPathUpload;

    @Value("${system.path.linux_upload}")
    protected String linuxPathUpload;

    @Value("${system.path.mac_upload}")
    protected String macPathUpload;

    @Autowired protected Proc proc;
    @Resource protected UserInfo userInfo;
    @Autowired protected Message message;

    protected String getLanguage()
    {
        return LocaleContextHolder.getLocale().getLanguage().toUpperCase(Locale.ROOT);
    }
}
