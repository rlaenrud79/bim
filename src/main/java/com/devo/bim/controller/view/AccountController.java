package com.devo.bim.controller.view;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devo.bim.model.entity.Account;
import com.devo.bim.repository.spring.AccountRepository;
import com.devo.bim.service.AccountService;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController extends AbstractController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Value("${application.project.admin.init.password}")
    private String adminInitPassword;

    @GetMapping("/login")
    public String login(@RequestParam(value="type", defaultValue = "") String type,
                        @RequestParam(value="email", defaultValue = "") String email, Model model) {

        model.addAttribute("locale", LocaleContextHolder.getLocale());
        model.addAttribute("defaultLangCode",  configService.defaultSelectedLanguageConfigs());
        model.addAttribute("type",  type);
        model.addAttribute("email",  email);

        return "account/login";
    }

    @GetMapping("/resetPassword")
    @Secured("ROLE_ADMIN_PROJECT")
    public String resetPassword(String email) {
        accountRepository
            .findByEmail(email)
            .map(savedAccount -> {
                if (savedAccount.isSameProject(userInfo.getProjectId())) {
                    savedAccount.setAccountPassword(passwordEncoder.encode(adminInitPassword));
                    return accountRepository.save(savedAccount);
                } else return new Account();
            })
            .orElseGet(Account::new);
        return "redirect:/main/index";
    }

    @GetMapping("/resetAdminPassword/{email}")
    public String resetAdminPassword(@PathVariable String email) {
        accountRepository
                .findByEmail(email)
                .map(savedAccount -> {
                    if (adminInitPassword.equals(savedAccount.getPassword())) {   // 프로젝트 최초 관리자 설정 비밀번호 체크
                        savedAccount.setAccountPassword(passwordEncoder.encode(adminInitPassword));
                        return accountRepository.save(savedAccount);
                    } else return new Account();
                })
                .orElseGet(Account::new);
        return "redirect:/logout";
    }

    @GetMapping("/mySnapShot")
    public String getMySnapShot(Model model) {
    	return getMySnapShotPage(model);
    }

    @NotNull
    private String getMySnapShotPage(Model model) {
    	model.addAttribute("mySnapShots", accountService.findLoginAccount().getMySnapShotsByIdAsc());
    	return "account/modal/mySnapShot";
    }

    @PostMapping("/mySnapShot")
    public String postMySnapShot(String title, String source, String viewPointJson, String viewModelJson, String viewModelId) {
    	accountService.postMySnapShot(title, source, viewPointJson, viewModelJson, viewModelId);
    	return "redirect:/account/mySnapShot";
    }

    @PostMapping("/saveMySnapShot/beforeJobSheet")
    @ResponseBody
    public JsonObject saveMySnapShotBeforeJobSheet(String title, String source, String viewPointJson, String viewModelJson, String viewModelId) {
    	JsonObject jsonObject = accountService.postMySnapShot(title, source, viewPointJson, viewModelJson, viewModelId);
    	return jsonObject;
    }

    @DeleteMapping("/mySnapShot/{mySnapShotId}")
    public String deleteMySnapShot(@PathVariable long mySnapShotId, Model model) {
    	accountService.deleteMySnapShot(mySnapShotId);
    	return getMySnapShotPage(model);
    }

    @PutMapping("/mySnapShotTitle")
    public String putMySnapShotTitle(long id, String title, Model model) {
    	accountService.postMySnapShotTitle(id, title);
    	return getMySnapShotPage(model);
    }

    @GetMapping("/viewerSetting")
    public ResponseEntity<String> getViewerSetting(long id){
        try{
            Account account = accountService.findById(id);
            return new ResponseEntity<>(account.getViewerSetting(), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("NG", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 뷰포인트 저장
    @PutMapping("/viewerSetting")
    public ResponseEntity<String> postViewerSetting(long id, String viewerSetting){
        try {
            accountRepository.findById(id)
                    .map(account -> {
                        account.setViewerSetting(viewerSetting);
                        return accountRepository.save(account);
                    });
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }catch (Exception e){
            return  new ResponseEntity<>("NG", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
