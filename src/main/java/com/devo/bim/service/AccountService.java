package com.devo.bim.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.AccountDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.dto.RocketChatAPIResponse;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.AccountGrantor;
import com.devo.bim.model.entity.AccountReference;
import com.devo.bim.model.entity.Company;
import com.devo.bim.model.entity.MySnapShot;
import com.devo.bim.model.entity.MySnapShotFile;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.CompanyRoleType;
import com.devo.bim.model.enumulator.RoleCode;
import com.devo.bim.model.vo.AccountVO;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.model.vo.SearchAccountVO;
import com.devo.bim.repository.dsl.AccountDTODslRepository;
import com.devo.bim.repository.dsl.AccountDslRepository;
import com.devo.bim.repository.spring.AccountGrantorRepository;
import com.devo.bim.repository.spring.AccountReferenceRepository;
import com.devo.bim.repository.spring.AccountRepository;
import com.devo.bim.repository.spring.CompanyRepository;
import com.devo.bim.repository.spring.MySnapShotFileRepository;
import com.devo.bim.repository.spring.MySnapShotRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService extends AbstractService {

    // 주의 : ProjectService 추가 금지
    //       ProjectService 를 사용하면 순환 참조 오류가 발생하므로 이곳에서는 ProjectService 사용하지 마세요

    private final AccountRepository accountRepository;
    private final MySnapShotRepository mySnapShotRepository;
    private final MySnapShotFileRepository mySnapShotFileRepository;
    private final AccountDTODslRepository accountDTODslRepository;
    private final AccountDslRepository accountDslRepository;
    private final CompanyRepository companyRepository;
    private final AccountGrantorRepository accountGrantorRepository;
    private final AccountReferenceRepository accountReferenceRepository;
    private final ConfigService configService;

    private final FileDeleteService fileDeleteService;
    private final ChatAPIService chatAPIService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public Account findById(long accountId) {
        return accountRepository.findById(accountId).orElseGet(Account::new);
    }

    public List<Account> findByIds(List<Long> ids) {
        return accountDslRepository.findByIds(userInfo.getProjectId(), ids);
    }

    public int findUserCntByCompanyId(long companyId) {
        return accountRepository.findByCompanyId(companyId).size();
    }

    public List<AccountDTO> findAccountDTOsByIds(List<Long> userIds) {
        return accountDTODslRepository.findAccountDTOsByIds(userInfo.getProjectId(), userIds);
    }

    public List<Long> getUserWorksIds(long accountId) {
        return findById(accountId).getWorks().stream().map(o -> o.getId()).collect(Collectors.toList());
    }

    @Transactional
    public void setUserInfo(String email, String sessionId) {
        String productProvider = configService.findConfigForCache("SYSTEM", "PRODUCT_PROVIDER", userInfo.getProjectId());
        accountRepository
                .findByEmail(email)
                .map(loginAccount -> {
                    userInfo.setUserInfo(loginAccount, sessionId, productProvider);
                    return loginAccount;
                }).orElseGet(Account::new);
    }

    public Account findLoginAccount() {
        return accountRepository
                .findById(userInfo.getId())
                .orElseGet(Account::new);
    }

    public boolean haveWorks(List<Work> works) {
        return accountRepository
                .findById(userInfo.getId())
                .map(savedAccount -> {
                    for (Work userWork : savedAccount.getWorks()) {
                        if (works.stream().filter(t -> t.getId() == userWork.getId()).count() > 0) return true;
                    }
                    return false;
                })
                .orElseGet(() -> {
                    return false;
                });
    }

    @Transactional
    public void deleteMySnapShot(long mySnapShotId) {
    	List<MySnapShotFile> savedMySnapShotFiles = mySnapShotFileRepository.findByMySnapShotId(mySnapShotId);

    	savedMySnapShotFiles.forEach(t -> {
    		fileDeleteService.deletePhysicalFile(t.getFilePath());
    	});

    	mySnapShotRepository.deleteByOwnerIdAndId(userInfo.getId(), mySnapShotId);
    }

    @Transactional
    public void postMySnapShotTitle(long mySnapShotId, String mySnapShotIdTitle) {
    	mySnapShotRepository
	    	.findById(mySnapShotId)
	        .map(m -> m.setMySnapShotTitle(mySnapShotIdTitle));
    }

    @Transactional
    public JsonObject postMySnapShot(String title, String source, String viewPointJson, String viewModelJson, String viewModelId) {
    	return accountRepository
    			.findById(userInfo.getId())
    	        .map(m ->
                proc.getResult(true
                        , "system.account.snapshot.save_success"
                        , mySnapShotRepository.save(new MySnapShot(m.getMySnapShots().size() + 1, title, source, viewPointJson, viewPointJson, viewModelId, m.getId())))
        ).orElseGet(() -> proc.getMessageResult(false, "system.account.snapshot.save_failure"));
    }
    
    @Transactional
    public JsonObject deleteMySnapShotFile(long mySnapShotFileId) {
    	// 1. view point 파일 조회
    	MySnapShotFile mySnapShotFile = mySnapShotFileRepository.findById(mySnapShotFileId).orElseGet(MySnapShotFile::new);
    	
    	// 2. 파일 정보 없으면 오류
    	if (mySnapShotFile.getId() == 0) return proc.getMessageResult(true, "system.account_service.error_no_exist_snap_shot_file");
    	
    	// 3. 작성자가 아니면 삭제 불가
    	if (haveFileDeleteWight(mySnapShotFile)) return proc.getMessageResult(true, "system.account_service.error_no_owner");
    	
    	try {
    		// 4. 물리적 파일 삭제
    		fileDeleteService.deletePhysicalFile(mySnapShotFile.getFilePath());
    		
    		// 5. DB 제거
    		mySnapShotFileRepository.deleteById(mySnapShotFileId);
    		
    		return proc.getResult(true, "system.account_service.delete_snap_shot_file");
    	} catch (Exception e) {
    		log.error("error", e); // 2) 로그를 작성
    		return proc.getMessageResult(false, e.getMessage());
    	}
    }

    private boolean haveFileDeleteWight(MySnapShotFile mySnapShotFile) {
    	return mySnapShotFile.getMySnapShot().getOwner().getId() != userInfo.getId();
    }
    
    public List<MySnapShot> findMySnapShots() {
    	return mySnapShotRepository
    			.findByOwnerId(userInfo.getId());
    }

    public MySnapShot findSnapShotId(long mySnapShotId) {
        return mySnapShotRepository.findMySnapShotFileByIdAndOwnerId(mySnapShotId, userInfo.getId()).orElseGet(MySnapShot::new);
    }

    public Account findLoginAccountViewPoint() {
        return accountRepository.findAccountViewPointById(userInfo.getId());
    }

    public Account findLoginMySnapShot() {
        return accountRepository.findMySnapShotById(userInfo.getId());
    }

    public String getRealPhotoPath() {

        String osName = System.getProperty("os.name");
        String win = winPathUpload;
        String mac = macPathUpload;
        String linux = linuxPathUpload;

        if (osName.toLowerCase().contains("win")) return "/" + win.substring(win.lastIndexOf("/") + 1);
        if (osName.toLowerCase().contains("mac")) return "/" + mac.substring(mac.lastIndexOf("/") + 1);
        return "/" + linux.substring(linux.lastIndexOf("/") + 1);
    }


    public List<MySnapShot> findMySnapShots(MySnapShotShareVO mySnapShotShareVO) {
    	return mySnapShotRepository.findByIdInAndOwnerId(mySnapShotShareVO.targetMySnapShotIds(), userInfo.getId());
    }

    public MySnapShot findMySnapShotsJobSheetProcessItem(MySnapShotShareVO mySnapShotShareVO) {
        return mySnapShotRepository.findByIdAndOwnerId(mySnapShotShareVO.getMySnapShotId(), userInfo.getId());
    }

    public JsonObject findMySnapShotsJobSheetProcessItemJson(MySnapShotShareVO mySnapShotShareVO) {

        MySnapShot mySnapShot = mySnapShotRepository.findByIdAndOwnerId(mySnapShotShareVO.getMySnapShotId(), userInfo.getId());
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", true);
        jsonObj.addProperty("source", mySnapShot.getSource());
        jsonObj.addProperty("id", mySnapShot.getId());
        jsonObj.addProperty("title", mySnapShot.getTitle());
        return jsonObj;

        /**
        return proc.getResult(
                true
                , mySnapShotRepository.findByIdAndOwnerId(mySnapShotShareVO.getMySnapShotId(), userInfo.getId())
        );
        **/
    }

    public JsonObject checkEmail(String email) {
        return accountRepository
                .findByEmail(email)
                .map(savedAccount -> proc.getResult(false, "system.account_service.email_is_duplication"))
                .orElseGet(() -> proc.getResult(true, "system.account_service.email_is_not_duplication"));
    }

    @Transactional
    public JsonObject procPostUser(AccountVO accountVO) {

        if(!isUnderProjectUserMaxCnt()){
            return proc.getResult(false, "system.account_service.error_max_user_cnt_over");
        }

        Map<String, Object> createAccountResult = createAccount(accountVO);

        if (createAccountResult.get("isSuccess").equals(false)) {
            return proc.getMessageResult((Boolean) createAccountResult.get("isSuccess"), (String) createAccountResult.get("message"));
        }

        long userId = (long) createAccountResult.get("userId");
        String companyName = (String) createAccountResult.get("companyName");

        boolean createRocketChatResult = createRocketChatUser(accountVO, userId, companyName);

        if (!createRocketChatResult) {
            return proc.getResult(false, "system.chat_service.create_user_failed");
        }

        return proc.getResult(true, userId, "system.account_service.user_add_success");
    }

    private boolean isUnderProjectUserMaxCnt(){
        // 1. project maxUserCount 조회
        int projectMaxUserCount =  accountRepository.findById(userInfo.getId())
                .orElseGet(Account::new)
                .getCompany()
                .getProject()
                .getMaxUserCount();

        // 2. 현재 프로젝트 소속 회사의 직원수 조회 (ROLE_ADMIN_SYSTEM 사용자는 제외)
        int projectUsedUserCount = 0;
        List<Account> projectAccounts =accountDslRepository.findByProjectId(userInfo.getProjectId());

        for (Account projectAccount : projectAccounts) {
            if(projectAccount.getRoles()
                    .stream()
                    .filter(s -> s.getCode() == RoleCode.ROLE_ADMIN_SYSTEM)
                    .count() == 0)
                projectUsedUserCount++;
        }

        // 3. project maxUserCount 와 현재 등록된 직원수 + 1 비교
        if((projectUsedUserCount + 1) > projectMaxUserCount ) return false;
        return true;
    }

    private Map<String, Object> createAccount(AccountVO accountVO) {
        Map<String, Object> result = new HashMap<>();

        Company company = companyRepository
                .findById(accountVO.getCompanyId())
                .orElseGet(Company::new);

        if (company.getId() == 0) {
            result.put("isSuccess", false);
            result.put("message", proc.translate("system.account_service.company_is_not_exist"));
            return result;
        }

        if (StringUtils.isEmpty(accountVO.getWorks())) {
            if (company.getCompanyRole().equals(CompanyRoleType.PARTNER)) {
                result.put("isSuccess", false);
                result.put("message", proc.translate("system.account_service.work_must_to_select_one_more"));
                return result;
            }
        }

        Account duplicationAccount = accountRepository.findByEmail(accountVO.getEmail()).orElseGet(Account::new);
        if (duplicationAccount.getId() > 0) {
            result.put("isSuccess", false);
            result.put("message", proc.translate("system.account_service.email_is_duplication"));
            return result;
        }

        if (accountVO.isResponsible()) {
            Account responsibleAccount = accountRepository.findByCompanyIdAndResponsible(accountVO.getCompanyId(), true).orElseGet(Account::new);
            if (responsibleAccount.getId() > 0) {
                result.put("isSuccess", false);
                result.put("message", proc.translate("system.account_service.email_is_duplication", new String[]{responsibleAccount.getUserName()}));
                return result;
            }
        }

        accountVO.setEncodedPassword(passwordEncoder.encode(accountVO.getPassword()));
        Account newAccount = accountRepository.save(new Account(accountVO, userInfo.getId()));

        result.put("isSuccess", true);
        result.put("userId", newAccount.getId());
        result.put("companyName", company.getName());
        return result;
    }

    @Transactional
    public boolean createRocketChatUser(AccountVO accountVO, long userId, String companyName) {
        try {
            RocketChatAPIResponse response = chatAPIService.createUser(accountVO, companyName);

            Account account = accountRepository.findById(userId).orElseGet(Account::new);
            if (account.getId() == 0) {
                return false;
            }
            account.setRocketChatId(response.getUser().get_id());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public JsonObject procPutUser(AccountVO accountVO) {
        Map<String, Object> updateAccountResult = updateAccount(accountVO);
        if (updateAccountResult.get("isSuccess").equals(false)) {
            return proc.getMessageResult((Boolean) updateAccountResult.get("isSuccess"), (String) updateAccountResult.get("message"));
        }

        if ((boolean) updateAccountResult.get("isRocketChatUpdate")) {
            String rocketChatId = (String) updateAccountResult.get("rocketChatId");
            if (!StringUtils.isBlank(rocketChatId)) {
                boolean updateRocketChatResult = updateRocketChatUser(accountVO, rocketChatId);
                if (!updateRocketChatResult) {
                    return proc.getResult(false, "system.chat_service.update_user_failed");
                }
            }
        }

        return proc.getResult(true, "system.account_service.update_account_success");
    }

    private Map<String, Object> updateAccount(AccountVO accountVO) {
        Map<String, Object> result = new HashMap<>();

        Company company = companyRepository
                .findById(accountVO.getCompanyId())
                .orElseGet(Company::new);

        if (company.getId() == 0) {
            result.put("isSuccess", false);
            result.put("message", proc.translate("system.account_service.company_is_not_exist"));
            return result;
        }

        if (StringUtils.isEmpty(accountVO.getWorks())) {
            if (company.getCompanyRole().equals(CompanyRoleType.PARTNER)) {
                result.put("isSuccess", false);
                result.put("message", proc.translate("system.account_service.work_must_to_select_one_more"));
                return result;
            }
        }

        if (accountVO.isResponsible()) {
            Account responsibleAccount = accountRepository.findByCompanyIdAndResponsible(accountVO.getCompanyId(), true).orElseGet(Account::new);
            if (responsibleAccount.getId() > 0) {
                result.put("isSuccess", false);
                result.put("message", proc.translate("system.account_service.already_responsible_exist_in_company", new String[]{responsibleAccount.getUserName()}));
                return result;
            }
        }

        Account savedAccount = accountRepository.findById(accountVO.getId()).orElseGet(Account::new);
        if (savedAccount.getId() == 0) {
            result.put("isSuccess", false);
            result.put("message", proc.translate("system.account_service.user_is_not_exist"));
            return result;
        }

        if (!savedAccount.getUserName().equals(accountVO.getUserName())) {
            result.put("isRocketChatUpdate", true);
            result.put("rocketChatId", savedAccount.getRocketChatId());
        } else {
            result.put("isRocketChatUpdate", false);
        }

        accountVO.setRoles(roleService.getValidRoleString(accountVO.getRoles(), savedAccount.isRoleSystemAdmin()));

        savedAccount.putAccount(accountVO, userInfo.getId());
        result.put("isSuccess", true);
        return result;
    }

    private boolean updateRocketChatUser(AccountVO accountVO, String rocketChatId) {
        try {
            RocketChatAPIResponse rocketChatAPIResponse = chatAPIService.updateUser(accountVO, rocketChatId);
            return rocketChatAPIResponse.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public JsonObject initPassword(long accountId) {
        String password = Utils.getRandomString(8);
        return accountRepository
                .findById(accountId)
                .map(savedAccount -> {
                    savedAccount.setAccountPassword(passwordEncoder.encode(password));
                    return proc.getResult(true, "system.account_service.password_initialize_success", new String[]{password});
                })
                .orElseGet(() -> proc.getResult(false, "system.account_service.user_is_not_exist"));
    }

    public PageDTO<AccountDTO> findAccountDTOsByProjectId(SearchAccountVO searchAccountVO, Pageable pageable) {
        return accountDTODslRepository.findAccountDTOsByProjectId(searchAccountVO, userInfo.getProjectId(), pageable);
    }

    public List<AccountDTO> findAccountDTOsBySearchCondition(SearchAccountVO searchAccountVO) {
        return accountDTODslRepository.findAccountDTOsBySearchCondition(searchAccountVO, userInfo.getProjectId());
    }

    public Account getAccountGrantor() {
    	Account account = findById(userInfo.getId());
        Account accountGrantor = findById(account.getAccountGrantors()
                .stream()
                .max(Comparator.comparingLong(AccountGrantor::getId))
                .orElseGet(AccountGrantor::new)
                .getGrantorId());
        return accountGrantor;
    }

    public List<Account> getAccountReferences() {
    	Account account = findById(userInfo.getId());
        List<Long> ids = account.getAccountReferences()
                .stream()
                .sorted(Comparator.comparingInt(AccountReference::getSortNo))
                .map(t -> t.getReferenceId())
                .collect(Collectors.toList());

        List<Account> accountReferences = new ArrayList<>();
        if(ids.size() > 0) {
        	accountReferences = findByIds(ids);
        }
        return accountReferences;
    }

    @Transactional
    public JsonObject procPutMyInfo(AccountVO accountVO) {
        if (!userInfo.isMe(accountVO.getId())) return proc.getResult(false, "system.account_service.user_is_not_same_login_user");

        Map<String, Object> updateAccountResult = updateMyAccount(accountVO);
        if (updateAccountResult.get("isSuccess").equals(false)) {
            return proc.getMessageResult((Boolean) updateAccountResult.get("isSuccess"), (String) updateAccountResult.get("message"));
        }

        if ((boolean) updateAccountResult.get("isRocketChatUpdate")) {
            String rocketChatId = (String) updateAccountResult.get("rocketChatId");
            if (!StringUtils.isBlank(rocketChatId)) {
                boolean updateRocketChatResult = updateRocketChatUser(accountVO, rocketChatId);
                if (!updateRocketChatResult) {
                    return proc.getResult(false, "system.chat_service.update_user_failed");
                }
            }
        }
        return proc.getResult(true, "system.account_service.update_account_success");
    }

    private Map<String, Object> updateMyAccount(AccountVO accountVO) {
        Map<String, Object> result = new HashMap<>();
        Account savedAccount = accountRepository.findById(accountVO.getId()).orElseGet(Account::new);

        if (savedAccount.getId() == 0) {
            result.put("isSuccess", false);
            result.put("message", proc.translate("system.account_service.user_is_not_exist"));
            return result;
        }

        if (accountVO.isChangePassword()) {
            if (passwordEncoder.matches(accountVO.getPassword(), savedAccount.getPassword())) {
                accountVO.setEncodedPassword(passwordEncoder.encode(accountVO.getNewPassword()));
            } else {
                result.put("isSuccess", false);
                result.put("message", proc.translate("system.account_service.password_is_not_same_saved_password"));
                return result;
            }
        }

        if (!savedAccount.getUserName().equals(accountVO.getUserName())) {
            result.put("isRocketChatUpdate", true);
            result.put("rocketChatId", savedAccount.getRocketChatId());
        } else {
            result.put("isRocketChatUpdate", false);
        }

        saveAccountGrantor(savedAccount, accountVO.getUserGrantorId());
        saveAccountReference(savedAccount, accountVO.getUserReferencesIds());

        savedAccount.putMyAccount(accountVO, userInfo.getId());
        result.put("isSuccess", true);

        return result;
    }

    private void saveAccountGrantor(Account account, long grantorId){
        if(grantorId == 0) return;

        // 기존 결재자들 삭제
        account.getAccountGrantors().forEach(t -> {
            accountGrantorRepository.delete(t);
        });

        // 신규 결재자 추가(현재는 1:1)
        accountGrantorRepository.save(new AccountGrantor(account.getId(), grantorId));
    }

    private void saveAccountReference(Account account, String referenceIds){
        if(StringUtils.isEmpty(referenceIds)) return;

        // 기존 참조자 삭제
        account.getAccountReferences().forEach(t -> {
            accountReferenceRepository.delete(t);
        });

        // 신규 참조자 List화
        List<String> ids = new ArrayList<>();

        if(referenceIds.contains(",")) ids = Arrays.stream(referenceIds.split(",")).collect(Collectors.toList());
        else ids.add(referenceIds);

        // 신규 참조자 저장
        int sortNo = 0;
        for (String id : ids) {
            sortNo ++;
            accountReferenceRepository.save((new AccountReference(account.getId(), Long.parseLong(id), sortNo)));
        }
    }

    public boolean isSystemAdmin(String email, String password) {
        return accountRepository.findByEmail(email)
                .map(savedAccount -> {
                    if (passwordEncoder.matches(password, savedAccount.getPassword())) {    // 비밀번호 비교
                        return savedAccount.getRoles().stream().filter(t -> t.getCode() == RoleCode.ROLE_ADMIN_SYSTEM).count() > 0;   // 시스템 관리자 확인
                    } else return false;
                })
                .orElse(false);
    }
}
