package com.devo.bim.controller.view;

import com.devo.bim.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/coWork")
@RequiredArgsConstructor
public class ErrorController extends AbstractController {

    @GetMapping("/serviceUnavailableException")
    public String serviceUnavailableException(){

        new ServiceUnavailableException(proc.translate("system.exception.service_unavailable.not_valid_project"));

        return null;
    }
}
