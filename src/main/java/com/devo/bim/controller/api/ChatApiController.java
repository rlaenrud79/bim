package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.vo.AccountVO;
import com.devo.bim.repository.spring.AccountRepository;
import com.devo.bim.service.AccountService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/chatApi")
@RequiredArgsConstructor
public class ChatApiController extends AbstractController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @ResponseBody
    @PostMapping("/auth/token")
    public JsonObject getAuthToken() {
        JsonObject obj = new JsonObject();
        obj.addProperty("loginToken", userInfo.getRocketChatToken());
        return obj;
    }

    @GetMapping("/user/createAll")
    public String createAll() {
        try{
            List<Account> notRegisteredList = accountRepository.findAll().stream().filter(t -> t.getRocketChatId() == null).collect(Collectors.toList());

            for (Account account : notRegisteredList) {
                AccountVO accountVO = new AccountVO();
                accountVO.setEmail(account.getEmail());
                accountVO.setUserName(account.getUserName());
                accountVO.setPassword("1111");

                String companyName = account.getCompany().getName();

                accountService.createRocketChatUser(accountVO, account.getId(), companyName);
            }

            return "success";
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            return "failed: " + e.getMessage();
        }
    }
}
