package com.devo.bim;

import com.devo.bim.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class LicenseApplicationRunner implements ApplicationRunner {

    private final ProjectService projectService;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        projectService.validateProjectLicense();
    }
}
