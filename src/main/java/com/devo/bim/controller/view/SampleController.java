package com.devo.bim.controller.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/sample")
public class SampleController extends AbstractController {

    @GetMapping("/index")
    public String index(Model model) {
        return "sample/index";
    }

    @GetMapping("/list")
    public String list() {
        return "sample/list";
    }

    @GetMapping("/item")
    public String item() {
        return "sample/item";
    }


}
