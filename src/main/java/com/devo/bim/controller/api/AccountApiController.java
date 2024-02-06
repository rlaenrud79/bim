package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.service.AccountService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/accountApi")
@RequiredArgsConstructor
public class AccountApiController extends AbstractController {

    private final AccountService accountService;

    @PostMapping("/mySnapShot")
    JsonObject postMySnapShot(String title, String source, String viewPointJson, String viewModelJson, String viewModelId) {
        return accountService.postMySnapShot(title, source, viewPointJson, viewModelJson, viewModelId);
    }
}
