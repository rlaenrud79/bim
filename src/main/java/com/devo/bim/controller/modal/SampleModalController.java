package com.devo.bim.controller.modal;

import com.devo.bim.controller.view.AbstractController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/sampleModal")
@RequiredArgsConstructor
public class SampleModalController extends AbstractController {

    @GetMapping("/item")
    public String item() {
        return "sample/modal";
    }
}
