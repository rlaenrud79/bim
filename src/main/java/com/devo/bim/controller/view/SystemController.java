package com.devo.bim.controller.view;

import com.devo.bim.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/system")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_SYSTEM")
public class SystemController extends AbstractController {

    private final ProjectService projectService;

    @GetMapping("/projectList")
    public String projectList(Model model) {
        model.addAttribute("list", projectService.findAllProjectListForAdminSystem());
        return "system/projectList/page";
    }
}
